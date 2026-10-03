package com.gdb.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Account Rules Engine with Properties File Configuration.
 * Singleton pattern with hot-reload capability.
 * Dynamic rule sets loaded from external properties files.
 */
public class AccountRulesEngine {
    private static AccountRulesPropertiesLoader savingsLoader =
        new AccountRulesPropertiesLoader("src/main/resources/config/rules/savings.properties");

    private static AccountRulesEngine instance;
    private Map<String, Map<Integer, Rule>> rulesMap;
    private boolean loaded = false;
    private String lastLoadTime;
    
    // Rule class - holds all data for a tenure bucket
    public static class Rule {
        private double minBalance;
        private double interestRate;
        private String featureName;
        private Map<String, Object> additionalFeatures;
        
        public Rule(double minBalance, double interestRate, String featureName) {
            this.minBalance = minBalance;
            this.interestRate = interestRate;
            this.featureName = featureName;
            this.additionalFeatures = new ConcurrentHashMap<>();
        }
        
        public double getMinBalance() { return minBalance; }
        public double getInterestRate() { return interestRate; }
        public String getFeatureName() { return featureName; }
        public Map<String, Object> getAdditionalFeatures() { return additionalFeatures; }
        
        public void addFeature(String key, Object value) {
            additionalFeatures.put(key, value);
        }
        
        @Override
        public String toString() {
            return String.format("Min: Rs %,.0f, Interest: %.2f%%, Feature: %s",
                               minBalance, interestRate, featureName);
        }
    }
    
    private AccountRulesEngine() {
        loadAllRules();
    }
    
    public static AccountRulesEngine getInstance() {
        if (instance == null) {
            synchronized (AccountRulesEngine.class) {
                if (instance == null) {
                    instance = new AccountRulesEngine();
                }
            }
        }
        return instance;
    }
    
    public synchronized void loadAllRules() {
        rulesMap = new ConcurrentHashMap<>();
        String[] accountTypes = {"SAVINGS", "CURRENT", "FIXEDDEPOSIT", "SALARY"};
        
        for (String type : accountTypes) {
            Map<Integer, Rule> rules = AccountRulesPropertiesLoader.loadRules(type);
            if (rules != null && !rules.isEmpty()) {
                rulesMap.put(type, rules);
            }
        }
        
        loaded = true;
        lastLoadTime = java.time.LocalDateTime.now().toString();
        System.out.println("Rules loaded for: SAVINGS, CURRENT, FIXEDDEPOSIT, SALARY");
    }
    
    public synchronized void reloadRules() {
        loadAllRules();
    }
    
    private int getTenureBucket(int tenureYears) {
        if (tenureYears < 1) return 0;
        else if (tenureYears < 3) return 1;
        else if (tenureYears < 5) return 3;
        else return 5;
    }
    
    private Rule getRule(String accountType, int tenureYears) {
        String key = accountType.toUpperCase();
        Map<Integer, Rule> accountRules = rulesMap.get(key);
        if (accountRules == null) return null;
        
        int bucket = getTenureBucket(tenureYears);
        return accountRules.get(bucket);
    }
    
    public String getTenureBucketName(int tenureYears) {
        int bucket = getTenureBucket(tenureYears);
        switch (bucket) {
            case 0: return "NEW (0-1 year)";
            case 1: return "STANDARD (1-3 years)";
            case 3: return "PREMIUM (3-5 years)";
            case 5: return "PRIVILEGE (5+ years)";
            default: return "UNKNOWN";
        }
    }
    
    public double getMinimumBalance(String accountType, int tenureYears) {
        Rule rule = getRule(accountType, tenureYears);
        return rule != null ? rule.getMinBalance() : 0;
    }
    
    public double getInterestRate(String accountType, int tenureYears) {
        Rule rule = getRule(accountType, tenureYears);
        return rule != null ? rule.getInterestRate() : 0;
    }
    
    public String getFeatureName(String accountType, int tenureYears) {
        Rule rule = getRule(accountType, tenureYears);
        return rule != null ? rule.getFeatureName() : "Unknown Account";
    }
    
    public Object getAdditionalFeature(String accountType, int tenureYears, String key) {
        Rule rule = getRule(accountType, tenureYears);
        if (rule == null) return null;
        return rule.getAdditionalFeatures().get(key);
    }
    
    public double getDailyTransferLimit(String accountType, int tenureYears) {
        Object val = getAdditionalFeature(accountType, tenureYears, "dailyTransferLimit");
        if (val == null) return 0.0;
        return (Double) val;
    }

    public boolean hasAccountType(String accountType) {
        return rulesMap.containsKey(accountType.toUpperCase());
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public String getLastLoadTime() {
        return lastLoadTime;
    }
    
    public void printAllRules() {
        System.out.println("\n=== ALL ACCOUNT RULES (FROM PROPERTIES FILES) ===");
        for (String type : rulesMap.keySet()) {
            System.out.println("\n[Type] " + type + ":");
            Map<Integer, Rule> rules = rulesMap.get(type);
            for (Map.Entry<Integer, Rule> entry : rules.entrySet()) {
                int bucket = entry.getKey();
                Rule rule = entry.getValue();
                String bucketName = getTenureBucketName(bucket);
                System.out.printf("  %-25s -> %s%n", bucketName, rule);
                
                if (!rule.getAdditionalFeatures().isEmpty()) {
                    System.out.print("    Features: ");
                    for (Map.Entry<String, Object> feat : rule.getAdditionalFeatures().entrySet()) {
                        System.out.print(feat.getKey() + "=" + feat.getValue() + " ");
                    }
                    System.out.println();
                }
            }
        }
    }
    
    public int getAccountTypeCount() {
        return rulesMap.size();
    }

    // Static helper methods for backwards compatibility with earlier activities
    public static String getSavingsBucket(int tenureYears) {
        if (tenureYears >= 5) return "privilege";
        if (tenureYears >= 3) return "premium";
        if (tenureYears >= 1) return "standard";
        return "new";
    }

    public static double getSavingsMinBalance(int tenureYears) {
        String key = "min.balance." + getSavingsBucket(tenureYears);
        return savingsLoader.getDouble(key, 10000.0);
    }

    public static double getSavingsInterestRate(int tenureYears) {
        String key = "interest.rate." + getSavingsBucket(tenureYears);
        return savingsLoader.getDouble(key, 2.70);
    }

    public static double getCurrentOverdraftLimit(double monthlyTurnover) {
        return Math.max(25000.0, monthlyTurnover * 2.5);
    }

    public static double getFDInterestRate(int months) {
        if (months >= 36) return 7.50;
        if (months >= 12) return 6.50;
        return 5.00;
    }
}
