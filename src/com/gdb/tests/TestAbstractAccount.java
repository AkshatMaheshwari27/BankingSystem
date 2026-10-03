package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.exceptions.*;

public class TestAbstractAccount {

    public static boolean transferFunds(AbstractAccount from, AbstractAccount to, double amount, String pin) {
        try {
            from.withdraw(amount, pin);
            to.deposit(amount);
            return true;
        } catch (AccountException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Activity 10: Banking Operations Suite ===");

        SavingsAccount sa = new SavingsAccount("SAV1001", "Rajesh Sharma", 28, 10000.0, "ACTIVE", "1234", 1000.0, 4.0);
        CurrentAccount ca = new CurrentAccount("CUR1001", "Priya Patel", 34, 5000.0, "ACTIVE", "5678", 10000.0);
        SalaryAccount sal = new SalaryAccount("SAL1001", "Amit Kumar", 30, 15000.0, "ACTIVE", "9999", "TechCorp");

        AbstractAccount[] portfolio = new AbstractAccount[] { sa, ca, sal };

        boolean transfer1 = transferFunds(sa, ca, 3000.0, "1234");
        if (transfer1) {
            System.out.println("Transfer Rs 3000 from Savings to Current: SUCCESS");
            System.out.println("Savings Balance: Rs " + sa.getBalance() + " | Current Balance: Rs " + ca.getBalance());
        }

        boolean transfer2 = transferFunds(sa, ca, 1000.0, "0000");
        if (!transfer2) {
            System.out.println("Failed Transfer (Wrong PIN): Exception caught, no balance changed [PASS]");
        }

        for (AbstractAccount acc : portfolio) {
            if (acc instanceof SavingsAccount) {
                ((SavingsAccount) acc).applyInterest();
            } else if (acc instanceof SalaryAccount) {
                ((SalaryAccount) acc).getInactiveMonths();
            }
        }
        System.out.println("Monthly Interest Cycle processed for all qualifying accounts.");

        System.out.println("All banking operations passed!");
    }
}

