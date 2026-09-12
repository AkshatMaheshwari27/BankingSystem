///*public class TestAccount {
//    public static void main(String[] args) {
//        // ==========================================
//        // TESTING ACTIVITY 1 & 2
//        // ==========================================
//        System.out.println("=".repeat(40));
//        System.out.println("       GLOBAL DIGITAL BANK             ");
//        System.out.println("           ACCOUNT TEST                ");
//        System.out.println("=".repeat(40));
//
//        System.out.println(">>> 1. Creating Account");
//        Account acc1 = new Account(1001, "John Doe", 25, 1000.0, "Savings");
//        System.out.println("Account created!");
//        printAccount(acc1);
//
//        System.out.println("\n>>> 2. Deposit Money");
//        boolean dep1 = acc1.deposit(500.0);
//        System.out.println("Depositing 500.0: " + (dep1 ? "SUCCESS" : "FAILED"));
//        System.out.println("New balance: ₹" + acc1.getBalance());
//
//        boolean dep2 = acc1.deposit(-100.0);
//        System.out.println("Depositing -100.0: " + (dep2 ? "SUCCESS" : "FAILED (Invalid amount)"));
//
//        System.out.println("\n>>> 3. Withdraw Money");
//        boolean with1 = acc1.withdraw(200.0);
//        System.out.println("Withdrawing 200.0: " + (with1 ? "SUCCESS" : "FAILED"));
//        System.out.println("New balance: ₹" + acc1.getBalance());
//
//        boolean with2 = acc1.withdraw(2000.0);
//        System.out.println("Withdrawing 2000.0: " + (with2 ? "SUCCESS" : "FAILED (Insufficient balance)"));
//        System.out.println("Current balance: ₹" + acc1.getBalance());
//
//        System.out.println("\n>>> 4. Creating Another Account");
//        Account acc2 = new Account(1002, "Jane Smith", 30, 2000.0, "Current");
//        printAccount(acc2);
//
//        System.out.println("\n>>> 5. All Accounts");
//        printAccount(acc1);
//        printAccount(acc2);
//
//        // ==========================================
//        // TESTING ACTIVITY 3 (ENHANCED ACCOUNT)
//        // ==========================================
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("      TESTING ACTIVITY 3 (ENHANCED)     ");
//        System.out.println("=".repeat(40));
//
//        // Test age & account type self-correction (Underage 15 becomes 18, invalid type becomes Savings)
//        AccountEnhanced enhAcc = new AccountEnhanced(1003, "Alex", 15, 200.0, "Crypto");
//        System.out.println("Created under 18 with 200 balance:");
//        System.out.println("Age: " + enhAcc.getAge() + " | Type: " + enhAcc.getAccountType() + " | Balance: ₹" + enhAcc.getBalance());
//
//        // Test PIN setup & verified withdrawal
//        enhAcc.setPin(1234);
//        System.out.println("PIN Set: " + enhAcc.hasPin());
//
//        boolean pinWithdraw = enhAcc.withdraw(100.0, 1234);
//        System.out.println("Withdraw ₹100 with PIN 1234: " + (pinWithdraw ? "SUCCESS" : "FAILED"));
//
//        System.out.println("=".repeat(40));
//        System.out.println("            ALL TESTS COMPLETED!        ");
//        System.out.println("=".repeat(40));
//
//
//        //Test 1:Valid Account Creation
//        AccountEnhanced acc3 = new AccountEnhanced(1001, "John Doe", 25, 1000 , "Savings");
//        printAccount(acc3);
//    }
//    private static void printAccount(Account acc) {
//        System.out.println("Account #" + acc.getAccountNumber() + " | " + acc.getName() +
//                " (" + acc.getAge() + " yrs) | " + acc.getAccountType() +
//                " | " + acc.getBalance() + " | " + acc.getStatus());
//    }
//    private static void printAccount(AccountEnhanced acc) {
//        System.out.println("Account #" + acc.getAccountNumber() + " | " + acc.getName() +
//                " (" + acc.getAge() + " yrs) | " + acc.getAccountType() +
//                " | " + acc.getBalance() + " | " + acc.getStatus());
//    }
//}
//*/
//public class TestAccount {
//    public static void main(String[] args) {
//        System.out.println("========================================");
//        System.out.println("      GLOBAL DIGITAL BANK (ACTIVITY 5)  ");
//        System.out.println("========================================");
//
//        try {
//            // 1. Creating Account
//            System.out.println("\n>>> 1. Creating Account");
//            Account acc1 = new Account(1001, "John Doe", 25, 1000.0, "Savings");
//            System.out.println("Account created!");
//            printAccount(acc1);
//
//            acc1.setPin(1234);
//
//            // 2. Deposit Money
//            System.out.println("\n>>> 2. Deposit Money");
//            acc1.deposit(500.0);
//            System.out.println("Depositing 500.0: SUCCESS");
//            System.out.println("New balance: " + acc1.getBalance());
//
//            try {
//                acc1.deposit(-100.0);
//            } catch (InvalidAmountException e) {
//                System.out.println("Depositing -100.0: FAILED (" + e.getMessage() + ")");
//            }
//
//            // 3. Withdraw Money
//            System.out.println("\n>>> 3. Withdraw Money");
//            acc1.withdraw(200.0, 1234);
//            System.out.println("Withdrawing 200.0: SUCCESS");
//            System.out.println("New balance: " + acc1.getBalance());
//
//            try {
//                acc1.withdraw(2000.0, 1234);
//            } catch (InsufficientBalanceException e) {
//                System.out.println("Withdrawing 2000.0: FAILED (" + e.getMessage() + ")");
//            }
//            System.out.println("Current balance: " + acc1.getBalance());
//
//        } catch (Exception e) {
//            System.out.println("Unexpected Error: " + e.getMessage());
//        }
//    }
//
//    private static void printAccount(Account acc) {
//        System.out.println("Account #" + acc.getAccountNumber() + " | " +
//                acc.getName() + " (" + acc.getAge() + " yrs) | " +
//                acc.getAccountType() + " | " + acc.getBalance() + " | " +
//                acc.getStatus());
//    }
//}