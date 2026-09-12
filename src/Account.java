/*public class Account {
    private int accountNumber;
    private String name;
    private int age;
    private double balance;
    private String accountType;
    private String status;

    public Account(int accountNumber, String name, int age, double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.age = age;
        this.balance = balance;
        this.accountType = accountType;
        this.status = "Active";
    }

    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        } else {
            this.balance += amount;
            return true;
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > this.balance) {
            return false;
        } else {
            this.balance -= amount;
            return true;
        }
    }

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public String getStatus() { return status; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
}*/
/*public class Account {
    private static final double MIN_BALANCE_SAVINGS = 500.0;
    private static final double MIN_BALANCE_CURRENT = 1000.0;
    private static final int MIN_AGE = 18;
    private static final int MIN_PIN = 1000;
    private static final int MAX_PIN = 9999;

    private int accountNumber;
    private String name;
    private int age;
    private double balance;
    private String accountType;
    private String status;
    private Integer pin;

    public Account(int accountNumber, String name, int age, double initialBalance, String accountType)
            throws IllegalArgumentException {
        if (age < MIN_AGE) {
            throw new IllegalArgumentException("Age must be at least " + MIN_AGE);
        }

        // UPDATED: Accept new account types
        if (!"Savings".equalsIgnoreCase(accountType) &&
                !"Current".equalsIgnoreCase(accountType) &&
                !"FIXED_DEPOSIT".equalsIgnoreCase(accountType) &&
                !"SALARY".equalsIgnoreCase(accountType)) {
            throw new IllegalArgumentException("Account type must be 'Savings', 'Current', 'FIXED_DEPOSIT', or 'SALARY'");
        }

        // UPDATED: Adjust minimum balance requirements dynamically
        double minReq = MIN_BALANCE_SAVINGS;
        if ("Current".equalsIgnoreCase(accountType)) {
            minReq = MIN_BALANCE_CURRENT;
        } else if ("FIXED_DEPOSIT".equalsIgnoreCase(accountType) || "SALARY".equalsIgnoreCase(accountType)) {
            minReq = 0.0;
        }

        if (initialBalance < minReq) {
            throw new IllegalArgumentException("Initial balance below minimum required: " + minReq);
        }

        this.accountNumber = accountNumber;
        this.name = name;
        this.age = age;
        this.balance = initialBalance;
        this.accountType = accountType;
        this.status = "Active";
        this.pin = null;
    }

    public void deposit(double amount) throws InvalidAmountException, InactiveAccountException {
        validateActive();
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }
        this.balance += amount;
    }

    public void withdraw(double amount, int pin) throws InvalidAmountException,
            InsufficientBalanceException, MinimumBalanceViolationException,
            InactiveAccountException, InvalidPinException {
        validateActive();
        if (!hasPin()) {
            throw new InvalidPinException("PIN not set");
        }
        if (!verifyPin(pin)) {
            throw new InvalidPinException("Incorrect PIN");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }
        if (amount > this.balance) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        if ((this.balance - amount) < getMinimumBalance()) {
            throw new MinimumBalanceViolationException("Minimum balance violation");
        }
        this.balance -= amount;
    }

    public void closeAccount() throws IllegalStateException {
        if ("Inactive".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Account is already closed");
        }
        this.status = "Inactive";
    }

    public void reopenAccount() throws IllegalStateException {
        if ("Active".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Account is already active");
        }
        this.status = "Active";
    }

    public void setPin(int pin) throws IllegalArgumentException {
        if (pin < MIN_PIN || pin > MAX_PIN) {
            throw new IllegalArgumentException("PIN must be a 4-digit number");
        }
        this.pin = pin;
    }

    public boolean verifyPin(int pin) {
        return this.pin != null && this.pin == pin;
    }

    public boolean hasPin() {
        return this.pin != null;
    }

    private double getMinimumBalance() {
        // UPDATED: Ensure withdrawals work smoothly for new account types
        if ("Current".equalsIgnoreCase(this.accountType)) return MIN_BALANCE_CURRENT;
        if ("FIXED_DEPOSIT".equalsIgnoreCase(this.accountType) || "SALARY".equalsIgnoreCase(this.accountType)) return 0.0;
        return MIN_BALANCE_SAVINGS;
    }

    private void validateActive() throws InactiveAccountException {
        if (!"Active".equalsIgnoreCase(this.status)) {
            throw new InactiveAccountException("Account is inactive");
        }
    }

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public String getStatus() { return status; }
}*/
public abstract class Account {
    protected static final int MIN_AGE = 18;
    protected static final int MIN_PIN = 1000;
    protected static final int MAX_PIN = 9999;

    private int accountNumber;
    private String name;
    private int age;
    private double balance;
    private String status;
    private Integer pin;

    // ===== Abstract Methods =====
    public abstract double getMinimumBalance();
    public abstract String getAccountType();

    // ===== Constructor =====
    public Account(int accountNumber, String name, int age, double initialBalance) throws IllegalArgumentException {
        if (age < MIN_AGE) {
            throw new IllegalArgumentException("Customer must be at least " + MIN_AGE + " years old. Provided: " + age);
        }

        double minBalance = getMinimumBalance();
        if (initialBalance < minBalance) {
            throw new IllegalArgumentException(getAccountType() + " account requires minimum balance of Rs." + minBalance + ". Provided: Rs." + initialBalance);
        }

        this.accountNumber = accountNumber;
        this.name = name;
        this.age = age;
        this.balance = initialBalance;
        this.status = "Active";
        this.pin = null;
    }

    // ===== Validation Methods =====
    protected void validateActive() throws InactiveAccountException {
        if (!"Active".equalsIgnoreCase(this.status)) {
            throw new InactiveAccountException("Account is inactive. Please reopen the account or contact support.");
        }
    }

    protected void validatePin(int pin) throws InvalidPinException {
        if (this.pin == null) {
            throw new InvalidPinException("PIN not set for this account");
        }
        if (this.pin != pin) {
            throw new InvalidPinException("Incorrect PIN");
        }
    }

    protected void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive. Provided: Rs." + amount);
        }
    }

    // ===== Core Operations =====
    public void deposit(double amount) throws InvalidAmountException, InactiveAccountException {
        validateActive();
        validateAmount(amount);
        this.balance += amount;
    }

    public void withdraw(double amount, int pin) throws InvalidAmountException,
            InsufficientBalanceException, MinimumBalanceViolationException,
            InactiveAccountException, InvalidPinException {
        validateActive();
        validatePin(pin);
        validateAmount(amount);

        if (amount > this.balance) {
            throw new InsufficientBalanceException("Insufficient balance. Available: Rs." + this.balance + ", Requested: Rs." + amount);
        }

        double minBalance = getMinimumBalance();
        if ((this.balance - amount) < minBalance) {
            throw new MinimumBalanceViolationException("Cannot withdraw. Minimum balance of Rs." + minBalance + " required. Available after withdrawal: Rs." + (this.balance - amount));
        }
        this.balance -= amount;
    }

    public void closeAccount() throws IllegalStateException {
        if (!"Active".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Account is already closed");
        }
        this.status = "Inactive";
    }

    public void reopenAccount() throws IllegalStateException {
        if ("Active".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Account is already active");
        }
        this.status = "Active";
    }

    public void setPin(int pin) throws IllegalArgumentException {
        if (pin < MIN_PIN || pin > MAX_PIN) {
            throw new IllegalArgumentException("PIN must be a 4-digit number (" + MIN_PIN + "-" + MAX_PIN + ")");
        }
        this.pin = pin;
    }

    public boolean verifyPin(int pin) {
        return this.pin != null && this.pin == pin;
    }

    public boolean hasPin() {
        return this.pin != null;
    }

    // ===== Getters & Setters =====
    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public Integer getPin() { return pin; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) {
        if (age >= MIN_AGE) {
            this.age = age;
        }
    }

    // Protected setter required for CurrentAccount overdraft logic
    protected void setBalance(double balance) {
        this.balance = balance;
    }
}