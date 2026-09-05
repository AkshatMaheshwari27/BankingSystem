public class TestAccountSubclasses {
    public static void main(String[] args) {
        System.out.println("=== Activity 7: Account Subclasses Test ===");

        // 1. Savings Account
        SavingsAccount savings = new SavingsAccount(2001, "John Doe", 25, 10000.0, 1000.0, 4.0);
        System.out.println("Savings Account Created: Balance Rs " + savings.getBalance() + " | Min Balance: Rs " + savings.getMinBalance());

        // 2. Current Account
        CurrentAccount current = new CurrentAccount(2002, "Alice Smith", 30, 15000.0, 25000.0);
        System.out.println("Current Account Created: Overdraft Limit Rs " + current.getOverdraftLimit());

        // 3. Fixed Deposit Account
        FixedDepositAccount fd = new FixedDepositAccount(2003, "Bob Jones", 40, 50000.0, 12, 6.5);
        System.out.println("Fixed Deposit Created: Tenure " + fd.getTenureMonths() + " months | Interest: " + fd.getInterestRate() + "%");

        // 4. Salary Account
        SalaryAccount salary = new SalaryAccount(2004, "Charlie Brown", 28, 20000.0, "Infosys");
        System.out.println("Salary Account Created: Employer " + salary.getEmployerName());

        System.out.println("All subclasses instantiated successfully!");
    }
}