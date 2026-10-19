package com.skillchain.util;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: EXCEPTION HANDLING (CUSTOM EXCEPTION 3)
 * ============================================================================
 * Checked exception thrown when a user attempts to add a skill they have
 * already cataloged under the same type (TEACH or LEARN).
 */
public class DuplicateSkillException extends Exception {

    public DuplicateSkillException(String message) {
        super(message);
    }

    public DuplicateSkillException(String message, Throwable cause) {
        super(message, cause);
    }
}
