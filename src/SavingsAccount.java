/*public class SavingsAccount extends Account {
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
}*/
public class SavingsAccount extends Account {
    // ===== Constants =====
    private static final double MINIMUM_BALANCE = 500.0;
    private static final String ACCOUNT_TYPE = "Savings";
    private static final double INTEREST_RATE = 4.0; // 4% per annum

    // ===== Constructor =====
    public SavingsAccount(int accountNumber, String name, int age, double initialBalance) throws IllegalArgumentException {
        super(accountNumber, name, age, initialBalance);
    }

    // ===== Abstract Method Implementations =====
    @Override
    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    @Override
    public String getAccountType() {
        return ACCOUNT_TYPE;
    }

    // ===== Savings-Specific Methods =====
    public double calculateInterest(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years must be non-negative");
        }
        return getBalance() * (INTEREST_RATE / 100) * years;
    }

    public double getInterestRate() {
        return INTEREST_RATE;
    }
}