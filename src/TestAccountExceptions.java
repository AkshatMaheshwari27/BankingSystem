/*public class TestAccountExceptions {
    public static void main(String[] args) {
        System.out.println(">>> Testing Invalid Age Exception");
        try {
            Account acc = new Account(1001, "Kid", 15, 500.0, "Savings");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
        }

        System.out.println("\n>>> Testing Minimum Balance Exception on Creation");
        try {
            Account acc = new Account(1002, "Bob", 25, 200.0, "Savings");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
        }

        System.out.println("\n>>> Testing Withdrawal Exception with Unset PIN");
        try {
            Account acc = new Account(1003, "Alice", 25, 1000.0, "Savings");
            acc.withdraw(100.0, 1234);
        } catch (AccountException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
        }
    }
}*/
import java.util.ArrayList;

public class TestAccountExceptions {
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("ACCOUNT TEST WITH EXCEPTIONS");
        System.out.println("=".repeat(50));

        // List to hold successfully created accounts for Test 11
        ArrayList<Account> allAccounts = new ArrayList<>();

        // Test 1: Valid Account Creation
        System.out.println(">>> Test 1: Valid Account Creation");
        try {
            Account acc1 = new Account(1001, "John Doe", 25, 1000.0, "Savings");
            System.out.print("SUCCESS: ");
            printAccount(acc1);
            allAccounts.add(acc1);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 2: Invalid Age (under 18)
        System.out.println("\n>>> Test 2: Invalid Age (under 18)");
        try {
            Account acc2 = new Account(1002, "Young Kid", 16, 500.0, "Savings");
            allAccounts.add(acc2);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 3: Invalid Account Type
        System.out.println("\n>>> Test 3: Invalid Account Type");
        try {
            Account acc3 = new Account(1003, "Test User", 25, 500.0, "Invalid");
            allAccounts.add(acc3);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 4: Minimum Balance on Creation
        System.out.println("\n>>> Test 4: Minimum Balance on Creation");
        System.out.println("Creating Savings account with 300");
        try {
            Account acc4 = new Account(1004, "Bob Wilson", 25, 300.0, "Savings");
            allAccounts.add(acc4);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 5: Valid Deposit and Withdrawal
        System.out.println("\n>>> Test 5: Valid Deposit and Withdrawal");
        try {
            Account acc5 = new Account(1005, "Alice Brown", 30, 1000.0, "Current");
            allAccounts.add(acc5);
            System.out.print("Account: ");
            printAccount(acc5);

            acc5.setPin(1234);
            System.out.println("Setting PIN 1234: SUCCESS");

            acc5.deposit(500.0);
            System.out.println("Depositing 500.0: SUCCESS");
            System.out.println("Balance after deposit: " + acc5.getBalance());

            acc5.withdraw(200.0, 1234);
            System.out.println("Withdrawing 200.0: SUCCESS");
            System.out.println("Balance after withdrawal: " + acc5.getBalance());
            printAccount(acc5);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 6: Invalid Deposit (Negative Amount)
        System.out.println("\n>>> Test 6: Invalid Deposit (Negative Amount)");
        System.out.println("Attempting to deposit -100.0");
        try {
            Account accTemp = new Account(9999, "Temp", 25, 1000.0, "Savings");
            accTemp.deposit(-100.0);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 7: Insufficient Balance
        System.out.println("\n>>> Test 7: Insufficient Balance");
        try {
            Account acc6 = new Account(1006, "Charlie Green", 35, 500.0, "Savings");
            allAccounts.add(acc6);
            acc6.setPin(1234);
            System.out.print("Account: ");
            printAccount(acc6);

            System.out.println("Attempting to withdraw 1000.0");
            acc6.withdraw(1000.0, 1234);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 8: Minimum Balance Violation
        System.out.println("\n>>> Test 8: Minimum Balance Violation");
        try {
            Account acc7 = new Account(1007, "Diana Prince", 28, 1000.0, "Savings");
            allAccounts.add(acc7);
            acc7.setPin(1234);
            System.out.print("Account: ");
            printAccount(acc7);

            System.out.println("Attempting to withdraw 600.0");
            acc7.withdraw(600.0, 1234);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 9: Inactive Account Operations
        System.out.println("\n>>> Test 9: Inactive Account Operations");
        try {
            Account acc8 = new Account(1008, "Eve Wilson", 32, 2000.0, "Current");
            allAccounts.add(acc8);
            System.out.print("Account: ");
            printAccount(acc8);

            acc8.closeAccount();
            System.out.println("Closing account: SUCCESS");

            System.out.println("Attempting to deposit 100.0 on closed account");
            acc8.deposit(100.0);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        try {
            Account acc8 = allAccounts.get(allAccounts.size() - 1);
            acc8.reopenAccount();
            System.out.println("Reopening account: SUCCESS");
            acc8.deposit(100.0);
            System.out.println("Depositing 100.0 after reopen: SUCCESS");
            System.out.println("Balance after deposit: " + acc8.getBalance());
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 10: PIN Verification
        System.out.println("\n>>> Test 10: PIN Verification");
        try {
            Account acc9 = new Account(1009, "Frank Miller", 40, 1500.0, "Savings");
            allAccounts.add(acc9);
            System.out.print("Account: ");
            printAccount(acc9);

            acc9.setPin(1234);
            System.out.println("Setting PIN 1234: SUCCESS");

            acc9.withdraw(200.0, 1234);
            System.out.println("Withdrawing 200.0 with correct PIN: SUCCESS");
            System.out.println("Balance: " + acc9.getBalance());

            System.out.println("Attempting to withdraw 100.0 with incorrect PIN (9999)");
            acc9.withdraw(100.0, 9999);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        try {
            Account accTemp2 = new Account(9998, "Temp2", 25, 1000.0, "Savings");
            System.out.println("Attempting to withdraw 100.0 without PIN set");
            accTemp2.withdraw(100.0, 1234);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        // Test 11: All Accounts Summary
        System.out.println("\n>>> Test 11: All Accounts Summary");
        for (Account acc : allAccounts) {
            printAccount(acc);
        }

        System.out.println("=".repeat(50));
        System.out.println("TEST COMPLETED!");
        System.out.println("=".repeat(50));
    }

    private static void printAccount(Account acc) {
        System.out.println("Account #" + acc.getAccountNumber() + " | " + acc.getName() +
                " (" + acc.getAge() + " yrs) | " + acc.getAccountType() +
                " | " + acc.getBalance() + " | " + acc.getStatus() +
                " | PIN: " + (acc.hasPin() ? "Yes" : "No"));
    }
}