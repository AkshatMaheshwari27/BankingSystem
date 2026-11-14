package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.repository.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class TestFileRepository {
    public static void main(String[] args) throws Exception {
        System.out.println("============================================================");
        System.out.println("  ACTIVITY 26 - FILE REPOSITORY (CSV + ATOMIC WRITES + BACKUP)");
        System.out.println("============================================================");

        String accPath = "test_data/accounts.csv";
        String txnPath = "test_data/transactions.csv";

        // Clean test files
        File accFile = new File(accPath);
        File txnFile = new File(txnPath);
        if (accFile.exists()) accFile.delete();
        if (txnFile.exists()) txnFile.delete();

        File dir = new File("test_data");
        if (dir.exists()) {
            File[] oldFiles = dir.listFiles();
            if (oldFiles != null) {
                for (File f : oldFiles) f.delete();
            }
        }

        FileAccountRepository accRepo = new FileAccountRepository(accPath, true, 5, 1001);
        FileTransactionRepository txnRepo = new FileTransactionRepository(txnPath, true, 5, 1);

        // TEST 1: Account Save & Read Back from CSV
        System.out.println("[TEST 1] Account Save & Read Back from CSV:");
        IAccount acc1 = AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 50000.0);
        acc1.setPin(1234);
        accRepo.save(acc1);
        System.out.println("  Saved: " + acc1.getAccountInfo());

        // Create fresh repo pointing to same file to verify disk read
        FileAccountRepository verifyRepo = new FileAccountRepository(accPath, true, 5, 1001);
        IAccount found = verifyRepo.findById(1001);
        System.out.println("  Found: " + (found != null ? found.getAccountInfo() : "null"));
        assert found != null : "Account should be persisted and found from file";
        assert found.getAccountNumber() == 1001 : "Account number should match";
        assert "Rajesh Sharma".equals(found.getAccountHolderName()) : "Account holder name should match";
        assert found.getBalance() == 50000.0 : "Balance should match";
        System.out.println("  -> PASSED");

        // TEST 2: Multiple Accounts & Atomic Update
        System.out.println("\n[TEST 2] Multiple Accounts & Atomic Update:");
        IAccount acc2 = AccountFactory.createAccount("CURRENT", 1002, "Priya Patel", 28, 40000.0);
        accRepo.save(acc2);

        found.deposit(10000.0);
        accRepo.update(found);

        FileAccountRepository verifyRepo2 = new FileAccountRepository(accPath, true, 5, 1001);
        IAccount updated = verifyRepo2.findById(1001);
        System.out.println("  Updated balance: Rs. " + updated.getBalance() + " (Expected: 60000.0)");
        assert updated.getBalance() == 60000.0 : "Balance should be 60000.0";

        List<IAccount> allAccounts = verifyRepo2.findAll();
        System.out.println("  Total accounts in CSV: " + allAccounts.size() + " (Expected: 2)");
        assert allAccounts.size() == 2 : "Should have 2 accounts";
        System.out.println("  -> PASSED");

        // TEST 3: Transaction Save & Query in CSV
        System.out.println("\n[TEST 3] Transaction Save & Query in CSV:");
        Transaction t1 = new Transaction(
                "TXN-101", LocalDateTime.now(), 1001,
                TransactionType.DEPOSIT, 10000.0, 60000.0, "SUCCESS",
                "Deposit", 1001, 1001
        );
        Transaction t2 = new Transaction(
                "TXN-102", LocalDateTime.now(), 1001,
                TransactionType.TRANSFER, 5000.0, 55000.0, "SUCCESS",
                "Transfer to #1002", 1001, 1002
        );
        txnRepo.save(t1);
        txnRepo.save(t2);

        FileTransactionRepository verifyTxnRepo = new FileTransactionRepository(txnPath, true, 5, 1);
        List<Transaction> acc1Txns = verifyTxnRepo.findByAccount(1001);
        System.out.println("  Found transactions for #1001: " + acc1Txns.size() + " (Expected: 2)");
        assert acc1Txns.size() == 2 : "Account 1001 should have 2 transactions";

        List<Transaction> acc2Txns = verifyTxnRepo.findByAccount(1002);
        System.out.println("  Found transactions for #1002: " + acc2Txns.size() + " (Expected: 1)");
        assert acc2Txns.size() == 1 : "Account 1002 should have 1 transaction";
        System.out.println("  -> PASSED");

        // TEST 4: Atomic Transfer via FileAccountRepository
        System.out.println("\n[TEST 4] Atomic Transfer via FileAccountRepository:");
        // Transfer 5000 from 1001 to 1002
        IAccount srcAcc = accRepo.findById(1001);
        IAccount dstAcc = accRepo.findById(1002);
        srcAcc.withdraw(5000.0, 1234);
        dstAcc.deposit(5000.0);
        accRepo.update(srcAcc);
        accRepo.update(dstAcc);

        FileAccountRepository verifyRepo3 = new FileAccountRepository(accPath, true, 5, 1001);
        System.out.println("  Sender balance: Rs. " + verifyRepo3.findById(1001).getBalance() + " (Expected: 55000.0)");
        System.out.println("  Receiver balance: Rs. " + verifyRepo3.findById(1002).getBalance() + " (Expected: 45000.0)");
        assert verifyRepo3.findById(1001).getBalance() == 55000.0 : "Sender balance should be 55000.0";
        assert verifyRepo3.findById(1002).getBalance() == 45000.0 : "Receiver balance should be 45000.0";
        System.out.println("  -> PASSED");

        // TEST 5: Verify Timestamped Backups Created
        System.out.println("\n[TEST 5] Verify Timestamped Backups Created:");
        File testDir = new File("test_data");
        File[] backups = testDir.listFiles((d, name) -> name.startsWith("accounts.csv.bak."));
        int backupCount = backups != null ? backups.length : 0;
        System.out.println("  Found " + backupCount + " backup files for accounts.csv.");
        assert backupCount >= 1 : "At least 1 backup file should exist";
        System.out.println("  -> PASSED");

        // Clean up
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) f.delete();
            }
            dir.delete();
        }

        System.out.println("\n============================================================");
        System.out.println("  ALL ACTIVITY 26 FILE REPOSITORY TESTS PASSED!");
        System.out.println("============================================================");
    }
}
