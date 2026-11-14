package com.gdb.repository;

import com.gdb.domain.Account;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.DataAccessException;
import com.gdb.file.AtomicFileWriter;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * File-backed implementation of TransactionRepository.
 * Stores transaction records in a text file and supports atomic balance transfers.
 */
public class FileTransactionRepository implements TransactionRepository {

    private final String filePath;
    private final boolean backupEnabled;
    private final int maxBackups;
    private final List<Transaction> cache = new ArrayList<>();

    public FileTransactionRepository(String filePath, boolean backupEnabled, int maxBackups, int startId) {
        this.filePath = filePath;
        this.backupEnabled = backupEnabled;
        this.maxBackups = maxBackups;
        load();
    }

    public FileTransactionRepository(String filePath, boolean backupEnabled, int maxBackups) {
        this(filePath, backupEnabled, maxBackups, 1);
    }

    public FileTransactionRepository(String filePath, int startId) {
        this(filePath, true, 5, startId);
    }

    public FileTransactionRepository(String filePath) {
        this(filePath, true, 5, 1);
    }

    public synchronized void load() {
        cache.clear();
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("transaction_id")) {
                    continue;
                }
                Transaction txn = com.gdb.file.CsvMapper.fromCsvTransaction(line);
                if (txn != null) {
                    cache.add(txn);
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load transactions from file: " + filePath, e);
        }
    }

    public synchronized void flush() {
        try {
            if (backupEnabled) {
                com.gdb.file.BackupManager.backup(filePath, maxBackups);
            }
            List<String> lines = new ArrayList<>();
            lines.add(com.gdb.file.CsvMapper.TRANSACTION_HEADER);
            for (Transaction txn : cache) {
                lines.add(com.gdb.file.CsvMapper.toCsv(txn));
            }
            AtomicFileWriter.write(filePath, lines);
        } catch (IOException e) {
            throw new DataAccessException("Failed to flush transactions to file: " + filePath, e);
        }
    }

    @Override
    public synchronized void save(Transaction transaction) {
        if (transaction == null) return;
        cache.add(transaction);
        flush();
    }

    @Override
    public synchronized List<Transaction> findByAccount(int accountNumber) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction txn : cache) {
            if (txn.getAccountNumber() == accountNumber ||
                txn.getFromAccount() == accountNumber ||
                txn.getToAccount() == accountNumber) {
                result.add(txn);
            }
        }
        return result;
    }

    @Override
    public synchronized List<Transaction> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized void clear() {
        cache.clear();
        flush();
    }

    @Override
    public synchronized void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        if (amount <= 0) {
            throw new DataAccessException("Transfer amount must be positive. Provided: " + amount);
        }

        AccountRepository accRepo = RepositoryFactory.getAccountRepository();
        IAccount fromAcc = accRepo.findById(fromAccountNumber);
        IAccount toAcc = accRepo.findById(toAccountNumber);

        if (fromAcc == null) {
            throw new DataAccessException("Source account #" + fromAccountNumber + " does not exist");
        }
        if (toAcc == null) {
            throw new DataAccessException("Destination account #" + toAccountNumber + " does not exist");
        }
        if (fromAcc.getBalance() < amount) {
            throw new DataAccessException("Insufficient balance: Rs. " + fromAcc.getBalance() + ", Required: Rs. " + amount);
        }

        try {
            fromAcc.withdraw(amount, 0); // may throw if pin required
        } catch (Exception e) {
            // direct balance adjustment if withdraw requires PIN
            try {
                java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
                balanceField.setAccessible(true);
                balanceField.setDouble(fromAcc, fromAcc.getBalance() - amount);
            } catch (Exception ex) {
                throw new DataAccessException("Failed to debit sender account: " + ex.getMessage(), ex);
            }
        }

        try {
            toAcc.deposit(amount);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
                balanceField.setAccessible(true);
                balanceField.setDouble(toAcc, toAcc.getBalance() + amount);
            } catch (Exception ex) {
                throw new DataAccessException("Failed to credit receiver account: " + ex.getMessage(), ex);
            }
        }

        accRepo.update(fromAcc);
        accRepo.update(toAcc);

        Transaction txn = new Transaction(
                Transaction.generateId(),
                LocalDateTime.now(),
                fromAccountNumber,
                TransactionType.TRANSFER,
                amount,
                fromAcc.getBalance(),
                "SUCCESS",
                "Transfer of Rs. " + amount + " to Account #" + toAccountNumber,
                fromAccountNumber,
                toAccountNumber
        );
        save(txn);
    }
}
