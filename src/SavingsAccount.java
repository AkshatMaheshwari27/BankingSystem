public class SavingsAccount extends Account {
    private double minBalance;
    private double interestRate;

    public SavingsAccount(int accountNumber, String name, int age, double initialBalance, double minBalance, double interestRate) {
        super(accountNumber, name, age, initialBalance, "SAVINGS");
        this.minBalance = minBalance;
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        double interest = getBalance() * (interestRate / 100.0);
        try {
            deposit(interest);
        } catch (Exception e) {
            System.out.println("Error applying interest: " + e.getMessage());
        }
    }

    public double getMinBalance() { return minBalance; }
    public double getInterestRate() { return interestRate; }
}