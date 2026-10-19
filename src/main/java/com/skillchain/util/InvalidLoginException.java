package com.skillchain.util;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: EXCEPTION HANDLING (CUSTOM EXCEPTION 2)
 * ============================================================================
 * Checked exception thrown when an authentication attempt fails due to
 * non-existent user credentials or an incorrect password hash match.
 */
public class InvalidLoginException extends Exception {

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
