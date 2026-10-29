package com.skillchain.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * ============================================================================
 * DATABASE CONNECTION MANAGER (JDBC)
 * ============================================================================
 * Reads database credentials securely from 'config.properties'.
 * Never hardcodes passwords or database secrets in the source code.
 *
 * Implements a robust configuration loading strategy:
 * 1. Checks the application working directory (for root config.properties).
 * 2. Falls back to classpath resources (for packaged deployments).
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE_NAME = "config.properties";
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static boolean initialized = false;

    // Private constructor prevents instantiation
    private DatabaseConnection() {
    }

    /**
     * Initializes database properties from config.properties.
     */
    private static synchronized void initialize() {
        if (initialized) {
            return;
        }

        Properties properties = new Properties();
        File externalConfigFile = new File(CONFIG_FILE_NAME);

        try {
            if (externalConfigFile.exists()) {
                try (InputStream fis = new FileInputStream(externalConfigFile)) {
                    properties.load(fis);
                }
            } else {
                // Try classpath resource
                try (InputStream cis = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
                    if (cis != null) {
                        properties.load(cis);
                    } else {
                        throw new DatabaseException(
                                "Configuration file '" + CONFIG_FILE_NAME + "' not found! " +
                                "Please copy 'config.properties.example' to 'config.properties' " +
                                "and supply your MySQL database credentials."
                        );
                    }
                }
            }

            dbUrl = properties.getProperty("db.url");
            dbUser = properties.getProperty("db.user");
            dbPassword = properties.getProperty("db.password", "");

            if (dbUrl == null || dbUser == null) {
                throw new DatabaseException("db.url and db.user must be configured in " + CONFIG_FILE_NAME);
            }

            // Ensure MySQL JDBC Driver is registered
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new DatabaseException("MySQL JDBC Driver (com.mysql.cj.jdbc.Driver) not found on classpath.", e);
            }

            initialized = true;

        } catch (IOException e) {
            throw new DatabaseException("Failed to read " + CONFIG_FILE_NAME + ": " + e.getMessage(), e);
        }
    }

    /**
     * Obtains a new active JDBC database connection.
     * Callers must manage the connection lifecycle via try-with-resources.
     *
     * @return Fresh JDBC Connection instance.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Diagnostic helper method to verify database connectivity.
     *
     * @return true if connection succeeds, false otherwise.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns a setup problem without throwing it into the Swing event flow.
     * A null result means the configuration file is readable and the JDBC
     * driver is available; it does not guarantee that MySQL is running.
     */
    public static String getConfigurationIssue() {
        try {
            initialize();
            return null;
        } catch (DatabaseException e) {
            return e.getMessage();
        }
    }

    /**
     * Saves local MySQL settings entered by the user in the setup dialog.
     * The file is ignored by Git and is never bundled into the repository.
     */
    public static synchronized void saveConfiguration(String url, String user, String password) {
        if (url == null || url.isBlank() || user == null || user.isBlank()) {
            throw new DatabaseException("Database URL and username are required.");
        }

        Properties properties = new Properties();
        properties.setProperty("db.url", url.trim());
        properties.setProperty("db.user", user.trim());
        properties.setProperty("db.password", password == null ? "" : password);

        try (OutputStream output = new FileOutputStream(CONFIG_FILE_NAME)) {
            properties.store(output, "Skill Chain local database configuration");
            initialized = false;
            initialize();
        } catch (IOException e) {
            throw new DatabaseException("Unable to save config.properties: " + e.getMessage(), e);
        }
    }

    /**
     * Re-initializes configuration if the user modifies config.properties at runtime.
     */
    public static synchronized void reloadConfig() {
        initialized = false;
        initialize();
    }
}
