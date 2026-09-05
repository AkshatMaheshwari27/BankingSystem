public class TestAccountExceptions {
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
}