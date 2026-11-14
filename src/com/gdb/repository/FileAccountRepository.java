package com.gdb.repository;

import com.gdb.domain.*;
import com.gdb.exceptions.DataAccessException;
import com.gdb.file.AtomicFileWriter;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * File-backed implementation of AccountRepository.
 * Stores account records in a CSV/text file with atomic writes.
 */
public class FileAccountRepository implements AccountRepository {

    private final String filePath;
    private final boolean backupEnabled;
    private final int maxBackups;
    private int nextAccountNumber;
    private final Map<Integer, IAccount> cache = new HashMap<>();

    public FileAccountRepository(String filePath, boolean backupEnabled, int maxBackups, int startAccountNumber) {
        this.filePath = filePath;
        this.backupEnabled = backupEnabled;
        this.maxBackups = maxBackups;
        this.nextAccountNumber = startAccountNumber;
        load();
    }

    public FileAccountRepository(String filePath, boolean backupEnabled, int maxBackups) {
        this(filePath, backupEnabled, maxBackups, 1001);
    }

    public FileAccountRepository(String filePath, int startAccountNumber) {
        this(filePath, true, 5, startAccountNumber);
    }

    public FileAccountRepository(String filePath) {
        this(filePath, true, 5, 1001);
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
                if (line.isEmpty() || line.startsWith("account_number")) {
                    continue;
                }
                IAccount acc = com.gdb.file.CsvMapper.fromCsv(line);
                if (acc != null) {
                    cache.put(acc.getAccountNumber(), acc);
                    if (acc.getAccountNumber() >= nextAccountNumber) {
                        nextAccountNumber = acc.getAccountNumber() + 1;
                    }
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load accounts from file: " + filePath, e);
        }
    }

    public synchronized void flush() {
        try {
            if (backupEnabled) {
                com.gdb.file.BackupManager.backup(filePath, maxBackups);
            }
            List<String> lines = new ArrayList<>();
            lines.add(com.gdb.file.CsvMapper.ACCOUNT_HEADER);
            for (IAccount acc : cache.values()) {
                lines.add(com.gdb.file.CsvMapper.toCsv(acc));
            }
            AtomicFileWriter.write(filePath, lines);
        } catch (IOException e) {
            throw new DataAccessException("Failed to flush accounts to file: " + filePath, e);
        }
    }

    @Override
    public synchronized void save(IAccount account) {
        if (account == null) return;
        cache.put(account.getAccountNumber(), account);
        if (account.getAccountNumber() >= nextAccountNumber) {
            nextAccountNumber = account.getAccountNumber() + 1;
        }
        flush();
    }

    @Override
    public synchronized IAccount findById(int accountNumber) {
        return cache.get(accountNumber);
    }

    @Override
    public synchronized List<IAccount> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public synchronized void update(IAccount account) {
        if (account == null) return;
        if (cache.containsKey(account.getAccountNumber())) {
            cache.put(account.getAccountNumber(), account);
            flush();
        }
    }

    @Override
    public synchronized void delete(int accountNumber) {
        if (cache.remove(accountNumber) != null) {
            flush();
        }
    }

    @Override
    public synchronized boolean exists(int accountNumber) {
        return cache.containsKey(accountNumber);
    }

    @Override
    public synchronized int nextAccountNumber() {
        return nextAccountNumber++;
    }
}
