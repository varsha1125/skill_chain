package com.skillchain.util;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: UNCHECKED EXCEPTION HANDLING
 * ============================================================================
 * Runtime exception wrapping low-level JDBC SQLExceptions, decoupling UI
 * components from raw database drivers and connection faults.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
