package com.skillchain.model;

import java.util.Objects;

/**
 * ============================================================================
 * JAVA OOP CONCEPTS: ENCAPSULATION & MEANINGFUL ARRAY USAGE
 * ============================================================================
 * Represents a skill entity available within the system's global catalog.
 */
public class Skill {

    /**
     * MEANINGFUL ARRAY USAGE:
     * Fixed array of approved skill categories in the college exchange system.
     * An array fits naturally here because:
     * 1. The categories are known at compile time and have a fixed length (4).
     * 2. Arrays avoid dynamic collection resizing overhead.
     * 3. Perfect for directly supplying items to Swing JComboBoxes.
     */
    public static final String[] CATEGORIES = new String[]{
            "Programming",
            "Web Development",
            "Database & Data",
            "Design & Creative"
    };

    private int skillId;
    private String skillName;
    private String category;

    public Skill() {
    }

    public Skill(int skillId, String skillName, String category) {
        setSkillId(skillId);
        setSkillName(skillName);
        setCategory(category);
    }

    public Skill(String skillName, String category) {
        this(0, skillName, category);
    }

    // ========================================================================
    // ENCAPSULATION: Getters & Setters with Validation
    // ========================================================================

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        if (skillId < 0) {
            throw new IllegalArgumentException("Skill ID cannot be negative.");
        }
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        if (skillName == null || skillName.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be empty.");
        }
        // Normalize name: trimmed
        this.skillName = skillName.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            this.category = "General";
        } else {
            this.category = category.trim();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill)) return false;
        Skill skill = (Skill) o;
        return skillId == skill.skillId ||
                skillName.equalsIgnoreCase(skill.skillName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillName.toLowerCase());
    }

    @Override
    public String toString() {
        return skillName + " (" + category + ")";
    }
}
