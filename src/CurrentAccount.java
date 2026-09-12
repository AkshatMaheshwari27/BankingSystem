/*public class CurrentAccount extends Account {
    private double overdraftLimit;

    public CurrentAccount(int accountNumber, String name, int age, double initialBalance, double overdraftLimit) {
        super(accountNumber, name, age, initialBalance, "CURRENT");
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}*/
public class CurrentAccount extends Account {
    // ===== Constants =====
    private static final double MINIMUM_BALANCE = 1000.0;
    private static final String ACCOUNT_TYPE = "Current";
    private static final double OVERDRAFT_LIMIT = 5000.0;

    // ===== Fields =====
    private double overdraftUsed;

    // ===== Constructor =====
    public CurrentAccount(int accountNumber, String name, int age, double initialBalance) throws IllegalArgumentException {
        super(accountNumber, name, age, initialBalance);
        this.overdraftUsed = 0.0;
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

    // ===== Override Withdraw for Overdraft =====
    @Override
    public void withdraw(double amount, int pin) throws InvalidAmountException,
            InsufficientBalanceException, MinimumBalanceViolationException,
            InactiveAccountException, InvalidPinException {

        // Call parent validation (active, PIN, amount)
        validateActive();
        validatePin(pin);
        validateAmount(amount);

        // Check with overdraft
        double availableBalance = getBalance() - getMinimumBalance() + OVERDRAFT_LIMIT - overdraftUsed;
        if (amount > availableBalance) {
            throw new InsufficientBalanceException(
                    "Insufficient funds. Available: Rs." + availableBalance +
                            " (including Rs." + OVERDRAFT_LIMIT + " overdraft), Requested: Rs." + amount
            );
        }

        // Apply overdraft if balance goes below minimum
        double newBalance = getBalance() - amount;
        if (newBalance < getMinimumBalance()) {
            double overdraftAmount = getMinimumBalance() - newBalance;
            this.overdraftUsed += overdraftAmount;
        } else {
            this.overdraftUsed = 0.0; // Reset if we are above minimum balance
        }

        // Update balance using parent's protected setter method
        setBalance(newBalance);
    }

    // ===== Current-Specific Methods =====
    public double getOverdraftLimit() {
        return OVERDRAFT_LIMIT;
    }

    public double getOverdraftUsed() {
        return overdraftUsed;
    }

    public double getAvailableOverdraft() {
        return OVERDRAFT_LIMIT - overdraftUsed;
    }

    public boolean isUsingOverdraft() {
        return overdraftUsed > 0;
    }

    public void repayOverdraft(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive");
        }
        if (amount > overdraftUsed) {
            throw new IllegalArgumentException("Amount exceeds overdraft used (Rs." + overdraftUsed + ")");
        }

        this.overdraftUsed -= amount;
        setBalance(getBalance() + amount);
    }
}