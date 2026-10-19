package com.skillchain.util;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: EXCEPTION HANDLING (CUSTOM EXCEPTION 1)
 * ============================================================================
 * Checked exception thrown when a student attempts registration using an
 * email address that is already registered in the system.
 */
public class DuplicateEmailException extends Exception {

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
