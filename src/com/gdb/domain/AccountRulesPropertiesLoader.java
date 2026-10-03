package com.gdb.domain;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loads account rules from properties files.
 * Each account type has its own properties file.
 */
public class AccountRulesPropertiesLoader {
    
    private static final String CONFIG_PATH = "main/resources/config/rules/";
    private Properties properties = new Properties();

    public AccountRulesPropertiesLoader(String configPath) {
        loadProperties(configPath);
    }

    private void loadProperties(String configPath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(configPath);
            if (is == null) {
                java.io.File file = new java.io.File(configPath);
                if (file.exists()) {
                    is = new java.io.FileInputStream(file);
                }
            }
            if (is != null) {
                try {
                    properties.load(is);
                } finally {
                    is.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load properties from " + configPath + ": " + e.getMessage());
        }
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        String val = properties.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue) {
        String val = properties.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static Map<Integer, AccountRulesEngine.Rule> loadRules(String accountType) {
        Map<Integer, AccountRulesEngine.Rule> rules = new HashMap<>();
        String propertyFile = CONFIG_PATH + accountType.toLowerCase() + ".properties";
        
        InputStream input = AccountRulesPropertiesLoader.class.getResourceAsStream("/" + propertyFile);
        if (input == null) {
            input = AccountRulesPropertiesLoader.class.getResourceAsStream("config/rules/" + accountType.toLowerCase() + ".properties");
        }
        if (input == null) {
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile);
        }
        if (input == null) {
            java.io.File file = new java.io.File("src/main/resources/config/rules/" + accountType.toLowerCase() + ".properties");
            if (file.exists()) {
                try {
                    input = new java.io.FileInputStream(file);
                } catch (Exception e) {}
            }
        }

        try {
            if (input == null) {
                return getDefaultRules(accountType);
            }
            
            Properties props = new Properties();
            props.load(input);
            input.close();
            
            String[] bucketKeys = getBucketKeys(props);
            
            for (String bucketKey : bucketKeys) {
                int tenure = Integer.parseInt(
                    props.getProperty("tenure.bucket." + bucketKey, "0"));
                
                double minBalance = Double.parseDouble(
                    props.getProperty("min.balance." + bucketKey, "0"));
                double interestRate = Double.parseDouble(
                    props.getProperty("interest.rate." + bucketKey, "0"));
                String featureName = props.getProperty(
                    "feature.name." + bucketKey, "Unknown");
                
                AccountRulesEngine.Rule rule = 
                    new AccountRulesEngine.Rule(minBalance, interestRate, featureName);
                
                String suffix = "." + bucketKey;
                for (String key : props.stringPropertyNames()) {
                    if (key.endsWith(suffix)) {
                        String prefix = key.substring(0, key.length() - suffix.length());
                        
                        if (prefix.equals("min.balance") || prefix.equals("interest.rate") ||
                            prefix.equals("feature.name") || prefix.equals("tenure.bucket")) {
                            continue;
                        }
                        
                        String value = props.getProperty(key);
                        String camelName = toCamelCase(prefix);
                        
                        Object parsedVal = parseValue(prefix, value);
                        rule.addFeature(camelName, parsedVal);
                        rule.addFeature(prefix, parsedVal);
                    }
                }
                
                rules.put(tenure, rule);
            }
        } catch (Exception e) {
            return getDefaultRules(accountType);
        }
        
        return rules;
    }
    
    private static String toCamelCase(String s) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '.') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    sb.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    private static Object parseValue(String prefix, String value) {
        if (value == null) return null;
        try {
            if (prefix.equals("overdraft.limit") || prefix.equals("penalty.percentage") || prefix.equals("daily.transfer.limit")) {
                return Double.parseDouble(value);
            }
            if (prefix.equals("lockin.months")) {
                return Integer.parseInt(value);
            }
            if (value.contains(".")) {
                return Double.parseDouble(value);
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return value;
            }
        } catch (Exception e) {
            return value;
        }
    }
    
    private static String[] getBucketKeys(Properties props) {
        return props.stringPropertyNames().stream()
            .filter(key -> key.startsWith("tenure.bucket."))
            .map(key -> key.substring("tenure.bucket.".length()))
            .toArray(String[]::new);
    }
    
    private static Map<Integer, AccountRulesEngine.Rule> getDefaultRules(String accountType) {
        Map<Integer, AccountRulesEngine.Rule> defaultRules = new HashMap<>();
        AccountRulesEngine.Rule defaultRule = 
            new AccountRulesEngine.Rule(1000, 4.0, "Default " + accountType);
        defaultRules.put(0, defaultRule);
        return defaultRules;
    }
}
