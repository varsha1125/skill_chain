package com.skillchain.model;

/**
 * ============================================================================
 * JAVA OOP CONCEPTS: ENUM, ARRAYS & SWITCH DECISION-MAKING
 * ============================================================================
 * Represents whether a skill entry is offered by a user (TEACH)
 * or requested by a user (LEARN).
 */
public enum SkillType {
    TEACH,
    LEARN;

    /**
     * Meaningful Array Usage:
     * Represents the fixed list of skill exchange modalities in the platform.
     * Arrays provide bounded memory footprint and instant O(1) indexed access
     * for populating UI dropdown selectors (JComboBox).
     */
    public static final String[] VALUES_ARRAY = new String[]{"TEACH", "LEARN"};

    /**
     * Parses a string into its corresponding SkillType using a SWITCH statement.
     * Demonstrates decision making in Java.
     *
     * @param typeString The string representation ("TEACH" or "LEARN")
     * @return The matched SkillType
     * @throws IllegalArgumentException if the type is unrecognized
     */
    public static SkillType fromString(String typeString) {
        if (typeString == null) {
            throw new IllegalArgumentException("Skill type cannot be null.");
        }

        switch (typeString.trim().toUpperCase()) {
            case "TEACH":
                return TEACH;
            case "LEARN":
                return LEARN;
            default:
                throw new IllegalArgumentException("Unknown skill type: " + typeString + ". Must be TEACH or LEARN.");
        }
    }
}
