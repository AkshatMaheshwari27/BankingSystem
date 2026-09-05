public class AccountEnhanced {
    private int accountNumber;
    private String name;
    private int age;
    private double balance;
    private String accountType;
    private String status;
    private Integer pin = null;

    public AccountEnhanced(int accountNumber, String name, int age, double initialBalance, String accountType) {
        this.accountNumber = accountNumber;
        this.name = name;

        // Single line simple check converted to ternary
        this.age = (age < 18) ? 18 : age;

        if ("Savings".equalsIgnoreCase(accountType) || "Current".equalsIgnoreCase(accountType)) {
            this.accountType = accountType;
        } else {
            this.accountType = "Savings";
        }

        double minBalance;
        if ("Current".equalsIgnoreCase(this.accountType)) {
            minBalance = 1000.0;
        } else {
            minBalance = 500.0;
        }

        if (initialBalance < minBalance) {
            this.balance = minBalance;
        } else {
            this.balance = initialBalance;
        }

        this.status = "Active";
    }

    public boolean deposit(double amount) {
        if (!"Active".equalsIgnoreCase(this.status) || amount <= 0) {
            return false;
        } else {
            this.balance += amount;
            return true;
        }
    }

    public boolean withdraw(double amount, int pin) {
        if (!"Active".equalsIgnoreCase(this.status) || !verifyPin(pin)) {
            return false;
        }

        double minBalance;
        if ("Current".equalsIgnoreCase(this.accountType)) {
            minBalance = 1000.0;
        } else {
            minBalance = 500.0;
        }

        if (amount <= 0 || (this.balance - amount) < minBalance) {
            return false;
        } else {
            this.balance -= amount;
            return true;
        }
    }

    public boolean closeAccount() {
        if ("Inactive".equalsIgnoreCase(this.status)) {
            return false;
        } else {
            this.status = "Inactive";
            return true;
        }
    }

    public boolean reopenAccount() {
        if ("Active".equalsIgnoreCase(this.status)) {
            return false;
        } else {
            this.status = "Active";
            return true;
        }
    }

    public boolean setPin(int pin) {
        if (pin >= 1000 && pin <= 9999) {
            this.pin = pin;
            return true;
        } else {
            return false;
        }
    }

    public boolean verifyPin(int pin) {
        if (this.pin == null) {
            return false;
        } else {
            return this.pin == pin;
        }
    }

    public boolean hasPin() {
        return this.pin != null;
    }

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public String getStatus() { return status; }
}