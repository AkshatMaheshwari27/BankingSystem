package com.gdb.repository;

import java.io.*;
import java.util.Properties;

/**
 * Factory creating and supplying repository instances based on persistence.properties configuration.
 */
public class RepositoryFactory {

    private static AccountRepository accountRepositoryInstance;
    private static TransactionRepository transactionRepositoryInstance;

    public static String getPersistenceMode() {
        Properties props = new Properties();
        InputStream input = RepositoryFactory.class.getResourceAsStream("/config/persistence.properties");
        if (input == null) {
            input = RepositoryFactory.class.getResourceAsStream("config/persistence.properties");
        }
        if (input == null) {
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/persistence.properties");
        }
        if (input == null) {
            try {
                input = new FileInputStream("src/main/resources/config/persistence.properties");
            } catch (Exception ignored) {}
        }
        if (input != null) {
            try {
                props.load(input);
                input.close();
            } catch (Exception ignored) {}
        }
        return props.getProperty("persistence.mode", "memory");
    }

    public static synchronized AccountRepository getAccountRepository() {
        if (accountRepositoryInstance == null) {
            String mode = getPersistenceMode();
            if ("jdbc".equalsIgnoreCase(mode)) {
                throw new UnsupportedOperationException("JDBC repository not implemented yet - coming in Activity 22");
            } else if ("file".equalsIgnoreCase(mode)) {
                throw new UnsupportedOperationException("File repository not implemented yet");
            } else {
                accountRepositoryInstance = new InMemoryAccountRepository();
            }
        }
        return accountRepositoryInstance;
    }

    public static synchronized TransactionRepository getTransactionRepository() {
        if (transactionRepositoryInstance == null) {
            String mode = getPersistenceMode();
            if ("jdbc".equalsIgnoreCase(mode)) {
                throw new UnsupportedOperationException("JDBC repository not implemented yet - coming in Activity 22");
            } else if ("file".equalsIgnoreCase(mode)) {
                throw new UnsupportedOperationException("File repository not implemented yet");
            } else {
                transactionRepositoryInstance = new InMemoryTransactionRepository();
            }
        }
        return transactionRepositoryInstance;
    }
}
