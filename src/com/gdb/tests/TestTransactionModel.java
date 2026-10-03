package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.service.TransferService;
import com.gdb.exceptions.*;

public class TestTransactionModel {
    public static void main(String[] args) throws Exception {
        TransferService svc = new TransferService();

        // STEP 10: Deposit with Transaction
        Account acc1 = new SavingsAccount(1001, "Rajesh Sharma", 30, 50000);
        acc1.setPin(1234);
        Transaction t10 = acc1.depositWithTransaction(5000);
        System.out.println("[STEP 10] Deposit Transaction: " + t10);

        // STEP 11: Withdraw with Transaction
        Transaction t11 = acc1.withdrawWithTransaction(2000, 1234);
        System.out.println("[STEP 11] Withdrawal Transaction: " + t11);

        // STEP 12: Transfer with Transaction
        Account acc2 = new SavingsAccount(1002, "Priya Patel", 28, 20000);
        Transaction t12 = svc.transferWithTransaction(acc1, acc2, 1000, 1234);
        System.out.println("[STEP 12] Transfer Transaction: " + t12);

        // STEP 13: Backward Compatibility Check
        acc1.deposit(1000);
        System.out.println("[STEP 13] Legacy Deposit +1000: " + acc1.getAccountInfo());
    }
}
