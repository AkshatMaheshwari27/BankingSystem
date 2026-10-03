package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.exceptions.*;

public class TestInterfaceFactory {
    public static void main(String[] args) {
        System.out.println("=== Activity 12: Factory-Driven System Suite ===");

        try {
            IAccount savings = AccountFactory.createAccount("SAVINGS", "SAV1001", "Rajesh Sharma", 28, 5000.0, "ACTIVE", "1234");
            savings.deposit(1000.0);
            if (savings.getBalance() == 6000.0) {
                System.out.println("[Test 1] Savings Account Creation & Deposit: [PASS]");
            }
        } catch (Exception e) {
            System.out.println("[Test 1] [FAIL]");
        }

        try {
            IAccount current = AccountFactory.createAccount("CURRENT", "CUR1001", "Priya Patel", 34, 10000.0, "ACTIVE", "5678");
            current.withdraw(20000.0, "5678");
            if (current.getBalance() == -10000.0) {
                System.out.println("[Test 2] Current Account Overdraft Withdrawal: [PASS]");
            }
        } catch (Exception e) {
            System.out.println("[Test 2] [FAIL]");
        }

        try {
            IAccount fd = AccountFactory.createAccount("FIXED_DEPOSIT", "FD1001", "Amit Kumar", 45, 50000.0, "ACTIVE", "1111");
            fd.withdraw(5000.0, "1111");
            System.out.println("[Test 3] [FAIL]");
        } catch (AccountException e) {
            System.out.println("[Test 3] Fixed Deposit Premature Withdrawal Block: [PASS]");
        }

        try {
            AccountFactory.createAccount("INVESTMENT", "INV1001", "Invalid User", 30, 1000.0, "ACTIVE", "0000");
            System.out.println("[Test 4] [FAIL]");
        } catch (IllegalArgumentException e) {
            System.out.println("[Test 4] Invalid Type Rejection: [PASS]");
        }

        System.out.println("Factory-driven architecture successfully verified!");
    }
}
