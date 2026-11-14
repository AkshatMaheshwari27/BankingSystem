package com.gdb.repository;

import com.gdb.db.ConnectionProvider;
import com.gdb.domain.*;
import com.gdb.exceptions.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed implementation of AccountRepository.
 * Manages database persistence for accounts using PreparedStatements and ConnectionProvider.
 */
public class JdbcAccountRepository implements AccountRepository {

    private final ConnectionProvider provider;

    public JdbcAccountRepository(ConnectionProvider provider) {
        this.provider = provider;
    }

    // ============================================================
    // 📝 STEP 1: Implement save(IAccount account)
    //
    // INSTRUCTIONS:
    //   1. Guard against null account.
    //   2. Prepare an INSERT statement for table 'accounts':
    //      INSERT INTO accounts (account_number, account_holder_name, age, balance, account_type, status, pin, opening_date, overdraft_used, interest_earned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    //   3. Bind parameters: account_number, account_holder_name, age, balance, account_type, status, pin, opening_date, overdraft_used, interest_earned.
    //   4. Execute the update using try-with-resources.
    //   5. Catch SQLException and wrap in DataAccessException.
    // ============================================================
    @Override
    public void save(IAccount account) {
        if (account == null) {
            return;
        }

        String sql = "INSERT INTO accounts (account_number, account_holder_name, age, balance, account_type, status, pin, opening_date, overdraft_used, interest_earned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getAccountNumber());
            ps.setString(2, account.getAccountHolderName());
            int age = 18;
            Integer pin = null;
            double overdraftUsed = 0.0;
            double interestEarned = 0.0;

            if (account instanceof Account) {
                Account acc = (Account) account;
                try {
                    java.lang.reflect.Field ageField = Account.class.getDeclaredField("age");
                    ageField.setAccessible(true);
                    age = ageField.getInt(acc);
                } catch (Exception ignored) {}

                try {
                    java.lang.reflect.Field pinField = Account.class.getDeclaredField("pin");
                    pinField.setAccessible(true);
                    pin = (Integer) pinField.get(acc);
                } catch (Exception ignored) {}
            }

            if (account instanceof CurrentAccount) {
                overdraftUsed = ((CurrentAccount) account).getOverdraftUsed();
            } else if (account instanceof FixedDepositAccount) {
                interestEarned = ((FixedDepositAccount) account).getInterestEarned();
            }

            ps.setInt(3, Math.max(18, age));
            ps.setDouble(4, account.getBalance());
            ps.setString(5, account.getAccountType());
            ps.setString(6, account.isActive() ? "Active" : "Inactive");
            if (pin != null) {
                ps.setInt(7, pin);
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, account.getOpeningDate() != null ? account.getOpeningDate() : "2026-08-28");
            ps.setDouble(9, overdraftUsed);
            ps.setDouble(10, interestEarned);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save account: " + account.getAccountNumber(), e);
        }
    }

    // ============================================================
    // 📝 STEP 2: Implement findById(int accountNumber)
    //
    // INSTRUCTIONS:
    //   1. Prepare SELECT * FROM accounts WHERE account_number = ?
    //   2. Bind accountNumber.
    //   3. Execute query and map ResultSet to Account instance using mapRow(rs).
    //   4. Return account or null if not found.
    //   5. Catch SQLException and wrap in DataAccessException.
    // ============================================================
    @Override
    public IAccount findById(int accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find account by ID: " + accountNumber, e);
        }
    }

    // ============================================================
    // 📝 STEP 3: Implement findAll()
    //
    // INSTRUCTIONS:
    //   1. Prepare SELECT * FROM accounts
    //   2. Execute query and iterate over ResultSet, adding mapped accounts to a List.
    //   3. Return list of accounts.
    //   4. Catch SQLException and wrap in DataAccessException.
    // ============================================================
    @Override
    public List<IAccount> findAll() {
        String sql = "SELECT * FROM accounts";
        List<IAccount> list = new ArrayList<>();
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve all accounts", e);
        }
    }

    // ============================================================
    // 📝 STEP 4: Implement update(IAccount account)
    //
    // INSTRUCTIONS:
    //   1. Guard against null account.
    //   2. Prepare UPDATE accounts SET balance = ?, status = ?, overdraft_used = ?, interest_earned = ? WHERE account_number = ?
    //   3. Bind parameters and execute update.
    //   4. Catch SQLException and wrap in DataAccessException.
    // ============================================================
    @Override
    public void update(IAccount account) {
        if (account == null) {
            return;
        }

        String sql = "UPDATE accounts SET balance = ?, status = ?, overdraft_used = ?, interest_earned = ? WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            double overdraftUsed = 0.0;
            double interestEarned = 0.0;
            if (account instanceof CurrentAccount) {
                overdraftUsed = ((CurrentAccount) account).getOverdraftUsed();
            } else if (account instanceof FixedDepositAccount) {
                interestEarned = ((FixedDepositAccount) account).getInterestEarned();
            }

            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.isActive() ? "Active" : "Inactive");
            ps.setDouble(3, overdraftUsed);
            ps.setDouble(4, interestEarned);
            ps.setInt(5, account.getAccountNumber());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update account: " + account.getAccountNumber(), e);
        }
    }

    // ============================================================
    // 📝 STEP 5: Implement delete(int accountNumber)
    //
    // INSTRUCTIONS:
    //   1. Prepare DELETE FROM accounts WHERE account_number = ?
    //   2. Bind accountNumber and execute update.
    //   3. Catch SQLException and wrap in DataAccessException.
    // ============================================================
    @Override
    public void delete(int accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete account: " + accountNumber, e);
        }
    }

    // ============================================================
    // 📝 STEP 6: Implement exists(int accountNumber)
    //
    // INSTRUCTIONS:
    //   1. Prepare SELECT 1 FROM accounts WHERE account_number = ?
    //   2. Bind accountNumber and check if ResultSet contains any row.
    //   3. Return true if exists, false otherwise.
    // ============================================================
    @Override
    public boolean exists(int accountNumber) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check if account exists: " + accountNumber, e);
        }
    }

    // ============================================================
    // 📝 STEP 7: Implement nextAccountNumber()
    //
    // INSTRUCTIONS:
    //   1. Prepare SELECT COALESCE(MAX(account_number), 1000) + 1 AS next_id FROM accounts
    //   2. If max exists, return next_id; otherwise return 1001.
    // ============================================================
    @Override
    public int nextAccountNumber() {
        String sql = "SELECT COALESCE(MAX(account_number), 1000) + 1 AS next_id FROM accounts";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("next_id");
            }
            return 1001;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to generate next account number", e);
        }
    }

    public IAccount mapRow(ResultSet rs) throws SQLException {
        int accNo = rs.getInt("account_number");
        String name = rs.getString("account_holder_name");
        int age = rs.getInt("age");
        double balance = rs.getDouble("balance");
        String type = rs.getString("account_type");
        String status = rs.getString("status");
        int pin = rs.getInt("pin");

        try {
            Account acc = (Account) AccountFactory.createAccount(type, accNo, name, Math.max(18, age), balance);
            if (pin > 0) {
                try { acc.setPin(pin); } catch (Exception ignored) {}
            }
            if (!"Active".equalsIgnoreCase(status)) {
                try { acc.closeAccount(); } catch (Exception ignored) {}
            }
            return acc;
        } catch (Exception e) {
            throw new SQLException("Error mapping row to Account: " + e.getMessage(), e);
        }
    }
}
