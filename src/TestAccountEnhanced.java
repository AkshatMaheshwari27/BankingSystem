public class TestAccountEnhanced {
    public static void main(String[] args) {
        System.out.println("ENHANCED ACCOUNT TEST (BOOLEAN RETURNS)");
        System.out.println("=".repeat(40));

        // Test 1: Valid Account Creation
        System.out.println("\n>>> Test 1: Valid Account Creation");
        AccountEnhanced acc1 = new AccountEnhanced(1001, "John Doe", 25, 1000.0, "Savings");
        printAccount(acc1);

        // Test 2: Invalid Age (under 18)
        System.out.println("\n>>> Test 2: Invalid Age (under 18)");
        System.out.println("Creating account with age 16");
        AccountEnhanced acc2 = new AccountEnhanced(1002, "Young Kid", 16, 500.0, "Savings");
        System.out.println("Age auto-corrected to: " + acc2.getAge());
        printAccount(acc2);

        // Test 3: Invalid Account Type
        System.out.println("\n>>> Test 3: Invalid Account Type");
        System.out.println("Creating account with type \"Invalid\"");
        AccountEnhanced acc3 = new AccountEnhanced(1003, "Test User", 25, 500.0, "Invalid");
        System.out.println("Account type defaulted to: " + acc3.getAccountType());
        printAccount(acc3);

        // Test 4: Minimum Balance Enforcement on Creation
        System.out.println("\n>>> Test 4: Minimum Balance Enforcement on Creation");
        System.out.println("Creating Savings account with 300 (below minimum)");
        AccountEnhanced acc4 = new AccountEnhanced(1004, "Bob Wilson", 25, 300.0, "Savings");
        System.out.println("Balance auto-corrected to minimum: " + acc4.getBalance());
        printAccount(acc4);

        // Test 5: Withdrawal with Minimum Balance
        System.out.println("\n>>> Test 5: Withdrawal with Minimum Balance");
        AccountEnhanced acc5 = new AccountEnhanced(1005, "Alice Brown", 30, 1000.0, "Savings");
        acc5.setPin(1234);
        System.out.print("Initial: ");
        printAccount(acc5);

        boolean w1 = acc5.withdraw(200.0, 1234);
        System.out.println("Withdrawing 200.0: " + (w1 ? "SUCCESS" : "FAILED"));
        System.out.println("New balance: ₹" + acc5.getBalance());
        System.out.print("After withdrawal: ");
        printAccount(acc5);

        boolean w2 = acc5.withdraw(900.0, 1234);
        System.out.println("Withdrawing 900.0 (would leave -100): " + (w2 ? "SUCCESS" : "FAILED (Minimum balance violation)"));
        System.out.println("Current balance: ₹" + acc5.getBalance());

        // Test 6: Account Status Management
        System.out.println("\n>>> Test 6: Account Status Management");
        AccountEnhanced acc6 = new AccountEnhanced(1006, "Charlie Green", 35, 2000.0, "Savings");
        System.out.print("Initial: ");
        printAccount(acc6);

        boolean closed = acc6.closeAccount();
        System.out.println("Closing account: " + (closed ? "SUCCESS" : "FAILED"));
        System.out.print("After close: ");
        printAccount(acc6);

        boolean dep = acc6.deposit(500.0);
        System.out.println("Depositing 500.0 to closed account: " + (dep ? "SUCCESS" : "FAILED (Account inactive)"));

        boolean reopened = acc6.reopenAccount();
        System.out.println("Reopening account: " + (reopened ? "SUCCESS" : "FAILED"));
        System.out.print("After reopen: ");
        printAccount(acc6);

        // Test 7: PIN Protection
        System.out.println("\n>>> Test 7: PIN Protection");
        AccountEnhanced acc7 = new AccountEnhanced(1007, "Diana Prince", 28, 1500.0, "Savings");
        boolean pSet = acc7.setPin(1234);
        System.out.println("Setting PIN 1234: " + (pSet ? "SUCCESS" : "FAILED"));

        boolean pWith1 = acc7.withdraw(200.0, 1234);
        System.out.println("Withdrawing 200.0 with correct PIN (1234): " + (pWith1 ? "SUCCESS" : "FAILED"));
        System.out.println("New balance: " + acc7.getBalance());

        boolean pWith2 = acc7.withdraw(100.0, 9999);
        System.out.println("Withdrawing 100.0 with incorrect PIN (9999): " + (pWith2 ? "SUCCESS" : "FAILED (Incorrect PIN)"));

        AccountEnhanced acc8NoPin = new AccountEnhanced(1008, "No Pin User", 30, 1000.0, "Savings");
        boolean pWith3 = acc8NoPin.withdraw(100.0, 1234);
        System.out.println("Withdrawing 100.0 with PIN not set: " + (pWith3 ? "SUCCESS" : "FAILED (PIN not set)"));

        // Test 8: All Accounts Summary
        System.out.println("\n>>> Test 8: All Accounts Summary");
        printAccount(acc1);
        printAccount(acc2);
        printAccount(acc3);
        printAccount(acc4);
        printAccount(acc5);
        printAccount(acc6);
        printAccount(acc7);

        System.out.println("\nENHANCED TEST COMPLETED!");
        System.out.println("=".repeat(40));
    }

    private static void printAccount(AccountEnhanced acc) {
        String pinStatus = acc.hasPin() ? "Yes" : "No";
        System.out.println("Account #" + acc.getAccountNumber() + " | " +
                acc.getName() + " (" + acc.getAge() + " yrs) | " +
                acc.getAccountType() + " | " + acc.getBalance() + " | " +
                acc.getStatus() + " | PIN: " + pinStatus);
    }
}