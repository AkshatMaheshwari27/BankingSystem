package com.gdb.file;

import com.gdb.domain.*;
import com.gdb.exceptions.AccountException;

import java.time.LocalDateTime;

/**
 * Utility mapping between Domain models and CSV formatted rows.
 */
public class CsvMapper {

    public static final String ACCOUNT_HEADER = "account_number,account_holder_name,age,balance,account_type,status,pin,opening_date,overdraft_used,interest_earned";
    public static final String TRANSACTION_HEADER = "transaction_id,timestamp,account_number,type,amount,balance_after,status,from_account,to_account,description";

    // ============================================================
    // 📝 STEP 1: Implement toCsv(IAccount account)
    //
    // INSTRUCTIONS:
    //   Format account fields as comma-separated string matching ACCOUNT_HEADER.
    // ============================================================
    public static String toCsv(IAccount account) {
        if (account == null) {
            return "";
        }
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

        String pinStr = pin != null ? String.valueOf(pin) : "";
        String openingDate = account.getOpeningDate() != null ? account.getOpeningDate() : "2026-08-28";
        String status = account.isActive() ? "Active" : "Inactive";

        return String.format("%d,%s,%d,%.1f,%s,%s,%s,%s,%.1f,%.1f",
                account.getAccountNumber(),
                account.getAccountHolderName(),
                Math.max(18, age),
                account.getBalance(),
                account.getAccountType(),
                status,
                pinStr,
                openingDate,
                overdraftUsed,
                interestEarned
        );
    }

    public static IAccount fromCsv(String line) {
        if (line == null || line.trim().isEmpty() || line.startsWith("account_number")) {
            return null;
        }
        String[] parts = line.split(",", -1);
        if (parts.length < 6) {
            return null;
        }

        try {
            int accNo = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            int age = Integer.parseInt(parts[2].trim());
            double balance = Double.parseDouble(parts[3].trim());
            String type = parts[4].trim();
            String status = parts[5].trim();
            String pinStr = parts.length > 6 ? parts[6].trim() : "";

            IAccount acc = AccountFactory.createAccount(type, accNo, name, Math.max(18, age), balance);
            if (!pinStr.isEmpty()) {
                try {
                    acc.setPin(Integer.parseInt(pinStr));
                } catch (Exception ignored) {}
            }
            if (!"Active".equalsIgnoreCase(status)) {
                try {
                    acc.closeAccount();
                } catch (Exception ignored) {}
            }
            return acc;
        } catch (Exception e) {
            System.err.println("Error parsing account CSV row: " + e.getMessage());
            return null;
        }
    }

    public static String toCsv(Transaction transaction) {
        if (transaction == null) {
            return "";
        }
        String id = transaction.getTransactionId();
        String ts = transaction.getTimestamp() != null ? transaction.getTimestamp().toString() : LocalDateTime.now().toString();
        int accNo = transaction.getAccountNumber();
        String type = transaction.getType() != null ? transaction.getType().name() : "TRANSFER";
        double amount = transaction.getAmount();
        double balanceAfter = transaction.getBalanceAfter();
        String status = transaction.getStatus() != null ? transaction.getStatus() : "SUCCESS";
        int fromAcc = transaction.getFromAccount();
        int toAcc = transaction.getToAccount();
        String desc = transaction.getDescription() != null ? transaction.getDescription() : "";

        return String.format("%s,%s,%d,%s,%.1f,%.1f,%s,%d,%d,%s",
                id, ts, accNo, type, amount, balanceAfter, status, fromAcc, toAcc, desc);
    }

    public static Transaction fromCsvTransaction(String line) {
        if (line == null || line.trim().isEmpty() || line.startsWith("transaction_id")) {
            return null;
        }
        String[] parts = line.split(",", -1);
        if (parts.length < 7) {
            return null;
        }

        try {
            String id = parts[0].trim();
            String tsStr = parts[1].trim();
            LocalDateTime ts = LocalDateTime.now();
            try {
                ts = LocalDateTime.parse(tsStr);
            } catch (Exception ignored) {}

            int accNo = Integer.parseInt(parts[2].trim());
            String typeStr = parts[3].trim();
            TransactionType type = TransactionType.TRANSFER;
            try {
                type = TransactionType.valueOf(typeStr);
            } catch (Exception ignored) {}

            double amount = Double.parseDouble(parts[4].trim());
            double balanceAfter = Double.parseDouble(parts[5].trim());
            String status = parts[6].trim();
            int fromAcc = parts.length > 7 && !parts[7].trim().isEmpty() ? Integer.parseInt(parts[7].trim()) : 0;
            int toAcc = parts.length > 8 && !parts[8].trim().isEmpty() ? Integer.parseInt(parts[8].trim()) : 0;
            String desc = parts.length > 9 ? parts[9].trim() : "";

            return new Transaction(id, ts, accNo, type, amount, balanceAfter, status, desc, fromAcc, toAcc);
        } catch (Exception e) {
            System.err.println("Error parsing transaction CSV row: " + e.getMessage());
            return null;
        }
    }
}
