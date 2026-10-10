package com.gdb.tests;

import com.gdb.db.ConnectionProvider;
import com.gdb.db.JdbcConnectionProvider;
import com.gdb.db.SchemaInitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestJdbcConnection {
    public static void main(String[] args) throws Exception {
        String testDbUrl = "jdbc:sqlite:test_gdb.db";
        ConnectionProvider provider = new JdbcConnectionProvider(testDbUrl);

        // STEP 8: Test Direct JDBC Connection & Simple Query
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            if (rs.next()) {
                System.out.println("[STEP 8] Connected to: " + testDbUrl);
                System.out.println("[STEP 8] Driver Name: " + conn.getMetaData().getDriverName());
            }
        }

        // STEP 9: Initialize Schema & Verify Tables
        SchemaInitializer.initialize(provider);
        System.out.println("[STEP 9] Tables initialized successfully.");

        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")) {
            boolean accountsFound = false;
            boolean transactionsFound = false;
            while (rs.next()) {
                String tableName = rs.getString("name");
                if ("accounts".equalsIgnoreCase(tableName)) accountsFound = true;
                if ("transactions".equalsIgnoreCase(tableName)) transactionsFound = true;
            }
            System.out.println("[STEP 9] Table 'accounts' exists: " + accountsFound);
            System.out.println("[STEP 9] Table 'transactions' exists: " + transactionsFound);
        }

        provider.shutdown();
    }
}
