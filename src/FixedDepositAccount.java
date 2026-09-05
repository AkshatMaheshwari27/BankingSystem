public class FixedDepositAccount extends Account {
    private int tenureMonths;
    private double interestRate;

    public FixedDepositAccount(int accountNumber, String name, int age, double initialBalance, int tenureMonths, double interestRate) {
        super(accountNumber, name, age, initialBalance, "FIXED_DEPOSIT");
        this.tenureMonths = tenureMonths;
        this.interestRate = interestRate;
    }

    public double calculateMaturityAmount() {
        // Simple or compound interest logic based on tenure
        double years = tenureMonths / 12.0;
        return getBalance() * Math.pow(1 + (interestRate / 100.0), years);
    }

    public int getTenureMonths() { return tenureMonths; }
    public double getInterestRate() { return interestRate; }
}