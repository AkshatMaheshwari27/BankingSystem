package com.gdb.db;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Initializes the database schema idempotently by executing schema.sql DDL statements.
 */
public class SchemaInitializer {

    public static void initialize(ConnectionProvider provider) {
        if (provider == null) return;
        String sqlScript = readSchemaSql();
        if (sqlScript == null || sqlScript.trim().isEmpty()) return;

        String[] statements = sqlScript.split(";");
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    public static String readSchemaSql() {
        InputStream input = SchemaInitializer.class.getResourceAsStream("/schema.sql");
        if (input == null) {
            input = SchemaInitializer.class.getResourceAsStream("schema.sql");
        }
        if (input == null) {
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema.sql");
        }
        if (input == null) {
            String[] paths = {
                "src/main/resources/schema.sql",
                "bin/schema.sql",
                "schema.sql"
            };
            for (String p : paths) {
                File f = new File(p);
                if (f.exists()) {
                    try {
                        input = new FileInputStream(f);
                        break;
                    } catch (IOException ignored) {}
                }
            }
        }

        if (input == null) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
