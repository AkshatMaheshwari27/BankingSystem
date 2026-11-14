package com.gdb.repository;

import com.gdb.domain.Transaction;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TransactionRepository backed by a List.
 */
public class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void save(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    @Override
    public List<Transaction> findByAccount(int accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber() == accountNumber || t.getFromAccount() == accountNumber || t.getToAccount() == accountNumber)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    @Override
    public void clear() {
        transactions.clear();
    }

    @Override
    public synchronized void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        Transaction txn = new Transaction(
                Transaction.generateId(),
                java.time.LocalDateTime.now(),
                fromAccountNumber,
                com.gdb.domain.TransactionType.TRANSFER,
                amount,
                0.0,
                "SUCCESS",
                "Transfer to Account #" + toAccountNumber,
                fromAccountNumber,
                toAccountNumber
        );
        save(txn);
    }
}
