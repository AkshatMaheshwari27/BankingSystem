//public class TestAccountSubclasses {
//    public static void main(String[] args) {
//        System.out.println("=== Activity 7: Account Subclasses Test ===");
//
//        // 1. Savings Account
//        SavingsAccount savings = new SavingsAccount(2001, "John Doe", 25, 10000.0, 1000.0, 4.0);
//        System.out.println("Savings Account Created: Balance Rs " + savings.getBalance() + " | Min Balance: Rs " + savings.getMinBalance());
//
//        // 2. Current Account
//        CurrentAccount current = new CurrentAccount(2002, "Alice Smith", 30, 15000.0, 25000.0);
//        System.out.println("Current Account Created: Overdraft Limit Rs " + current.getOverdraftLimit());
//
//        // 3. Fixed Deposit Account
//        FixedDepositAccount fd = new FixedDepositAccount(2003, "Bob Jones", 40, 50000.0, 12, 6.5);
//        System.out.println("Fixed Deposit Created: Tenure " + fd.getTenureMonths() + " months | Interest: " + fd.getInterestRate() + "%");
//
//        // 4. Salary Account
//        SalaryAccount salary = new SalaryAccount(2004, "Charlie Brown", 28, 20000.0, "Infosys");
//        System.out.println("Salary Account Created: Employer " + salary.getEmployerName());
//
//        System.out.println("All subclasses instantiated successfully!");
//    }
//}
import java.util.ArrayList;

public class TestAccountSubclasses {

    private static void printAccountInfo(Account acc) {
        String pinStatus = acc.hasPin() ? "Yes" : "No";
        System.out.println("Account #" + acc.getAccountNumber() + " | " + acc.getName() +
                " (" + acc.getAge() + " yrs) | " + acc.getAccountType() +
                " | " + acc.getBalance() + " | " + acc.getStatus() + " | PIN: " + pinStatus);
    }

    private static void printException(Exception e) {
        System.out.println("EXCEPTION: " + e.getMessage());
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("ACCOUNT SUBCLASSES TEST (SAVINGS & CURRENT)");
        System.out.println("==================================================");

        ArrayList<Account> allAccounts = new ArrayList<>();

        // >>> Test 1: Creating Accounts
        System.out.println("\n>>> Test 1: Creating Accounts");
        SavingsAccount acc1 = new SavingsAccount(1001, "John Doe", 25, 1000.0);
        System.out.print("Savings Account: ");
        printAccountInfo(acc1);

        CurrentAccount acc2 = new CurrentAccount(1002, "Jane Smith", 30, 2000.0);
        System.out.print("Current Account: ");
        printAccountInfo(acc2);

        // >>> Test 2: Account Type and Minimum Balance
        System.out.println("\n>>> Test 2: Account Type and Minimum Balance");
        System.out.println("Savings Account - Type: " + acc1.getAccountType() + ", Minimum Balance: Rs " + acc1.getMinimumBalance());
        System.out.println("Current Account - Type: " + acc2.getAccountType() + ", Minimum Balance: Rs " + acc2.getMinimumBalance());

        // >>> Test 3: Savings Account - Interest Calculation
        System.out.println("\n>>> Test 3: Savings Account - Interest Calculation");
        System.out.print("Savings Account: ");
        printAccountInfo(acc1);
        System.out.println("Interest Rate: " + acc1.getInterestRate() + "% per annum");
        System.out.println("Interest for 1 year:  " + acc1.calculateInterest(1));
        System.out.println("Interest for 2 years:  " + acc1.calculateInterest(2));
        System.out.println("Interest for 5 years:  " + acc1.calculateInterest(5));
        System.out.println("After 2 years with interest: Balance would be " + (acc1.getBalance() + acc1.calculateInterest(2)));

        // >>> Test 4: Current Account - Overdraft Feature
        System.out.println("\n>>> Test 4: Current Account - Overdraft Feature");
        System.out.print("Current Account: ");
        printAccountInfo(acc2);
        System.out.println("Overdraft Limit:  " + acc2.getOverdraftLimit());
        System.out.println("Available Overdraft:  " + acc2.getAvailableOverdraft());
        System.out.println("Overdraft Used: Rs " + acc2.getOverdraftUsed());
        System.out.println("Is Using Overdraft: " + acc2.isUsingOverdraft());

        acc2.setPin(1234);
        System.out.println("\nWithdrawing 1500.0 (goes below minimum balance of 1000)");
        System.out.println("Balance before: Rs " + acc2.getBalance());
        try {
            acc2.withdraw(1500.0, 1234);
            System.out.println("Withdrawing: 1500.0 - SUCCESS");
        } catch (Exception e) { printException(e); }
        System.out.println("Balance after:  " + acc2.getBalance());
        System.out.println("Overdraft Used: " + acc2.getOverdraftUsed());
        System.out.println("Available Overdraft: Rs " + acc2.getAvailableOverdraft());
        System.out.println("Is Using Overdraft: " + acc2.isUsingOverdraft());

        System.out.println("\nAttempting to withdraw 4000.0 (would exceed overdraft)");
        System.out.println("Available funds: " + acc2.getBalance() + " (balance) + 4500.0 (overdraft) = 5000.0");
        try {
            acc2.withdraw(4000.0, 1234);
        } catch (Exception e) { printException(e); }

        System.out.println("\nRepaying overdraft of Rs 500.0");
        System.out.println("Balance before repayment: " + acc2.getBalance());
        System.out.println("Overdraft Used before: Rs " + acc2.getOverdraftUsed());
        try {
            acc2.repayOverdraft(500.0);
            System.out.println("Repaying 500.0 - SUCCESS");
        } catch (Exception e) { printException(e); }
        System.out.println("Balance after repayment:  " + acc2.getBalance());
        System.out.println("Overdraft Used after: Rs " + acc2.getOverdraftUsed());
        System.out.println("Is Using Overdraft: " + acc2.isUsingOverdraft());

        // >>> Test 5: Polymorphism - Treating Accounts Uniformly
        System.out.println("\n>>> Test 5: Polymorphism - Treating Accounts Uniformly");
        System.out.println("Processing accounts polymorphically:");

        allAccounts.add(acc1);
        allAccounts.add(acc2);
        allAccounts.add(new SavingsAccount(1003, "Bob Wilson", 35, 500.0));
        allAccounts.add(new CurrentAccount(1004, "Alice Brown", 28, 1500.0));

        double totalBalance = 0;
        for (Account acc : allAccounts) {
            System.out.println("Account #" + acc.getAccountNumber() + " | " + acc.getName() + " (" + acc.getAge() + " yrs) | " + acc.getAccountType() + " | " + acc.getBalance() + " | " + acc.getStatus() + " | Type: " + acc.getAccountType() + ", Min Balance: Rs " + acc.getMinimumBalance());
            totalBalance += acc.getBalance();
        }
        System.out.println("Total accounts: " + allAccounts.size());
        System.out.println("Total balance across all accounts: Rs " + totalBalance);

        // >>> Test 6: Validation - Invalid Creation Attempts
        System.out.println("\n>>> Test 6: Validation - Invalid Creation Attempts");
        System.out.println("Attempting to create SavingsAccount with 300 (below minimum)");
        try { new SavingsAccount(999, "Test", 25, 300.0); } catch (Exception e) { printException(e); }

        System.out.println("Attempting to create CurrentAccount with Rs 500 (below minimum)");
        try { new CurrentAccount(999, "Test", 25, 500.0); } catch (Exception e) { printException(e); }

        System.out.println("Attempting to create SavingsAccount with age 16");
        try { new SavingsAccount(999, "Test", 16, 1000.0); } catch (Exception e) { printException(e); }

        // >>> Test 7: Savings Account - PIN and Operations
        System.out.println("\n>>> Test 7: Savings Account - PIN and Operations");
        SavingsAccount acc5 = new SavingsAccount(1005, "Charlie Green", 40, 2000.0);
        System.out.print("Savings Account: ");
        printAccountInfo(acc5);
        acc5.setPin(1234);
        System.out.println("Setting PIN 1234: SUCCESS");

        try {
            acc5.deposit(500.0);
            System.out.println("Depositing 500.0: SUCCESS");
            System.out.println("Balance after deposit: " + acc5.getBalance());

            acc5.withdraw(300.0, 1234);
            System.out.println("Withdrawing 300.0 with correct PIN: SUCCESS");
            System.out.println("Balance after withdrawal: " + acc5.getBalance());

            System.out.println("Attempting to withdraw 2000.0 (would violate minimum balance)");
            acc5.withdraw(2000.0, 1234);
        } catch (Exception e) { printException(e); }
        allAccounts.add(acc5);

        // >>> Test 8: Current Account - Active Status Operations
        System.out.println("\n>>> Test 8: Current Account - Active Status Operations");
        CurrentAccount acc6 = new CurrentAccount(1006, "Diana Prince", 35, 3000.0);
        System.out.print("Current Account: ");
        printAccountInfo(acc6);

        acc6.closeAccount();
        System.out.println("Closing account: SUCCESS");
        System.out.println("Attempting to deposit 100.0 on closed account");
        try { acc6.deposit(100.0); } catch (Exception e) { printException(e); }

        acc6.reopenAccount();
        System.out.println("Reopening account: SUCCESS");
        try {
            acc6.deposit(100.0);
            System.out.println("Depositing 100.0 after reopen: SUCCESS");
            System.out.println("Balance after deposit: " + acc6.getBalance());
        } catch (Exception e) { printException(e); }
        allAccounts.add(acc6);

        // >>> Test 9: All Accounts Summary
        System.out.println("\n>>> Test 9: All Accounts Summary");
        for (Account acc : allAccounts) {
            printAccountInfo(acc);
        }

        System.out.println("==================================================");
        System.out.println("TEST COMPLETED!");
        System.out.println("==================================================");
    }
}