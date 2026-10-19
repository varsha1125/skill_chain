package com.skillchain.model;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: COMPOSITION & ENCAPSULATION
 * ============================================================================
 * Represents a specific skill mapping associated with a user, designating
 * whether the student is offering to TEACH or wanting to LEARN this skill.
 */
public class UserSkill {

    private int userSkillId;
    private int userId;
    private int skillId;
    private String skillName;
    private String category;
    private SkillType skillType;
    private LocalDateTime createdAt;

    public UserSkill() {
    }

    public UserSkill(int userSkillId, int userId, int skillId, String skillName,
                     String category, SkillType skillType, LocalDateTime createdAt) {
        this.userSkillId = userSkillId;
        this.userId = userId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.skillType = skillType;
        this.createdAt = createdAt;
    }

    public UserSkill(int userId, int skillId, SkillType skillType) {
        this.userId = userId;
        this.skillId = skillId;
        this.skillType = skillType;
    }

    // ========================================================================
    // ENCAPSULATION: Getters and Setters
    // ========================================================================

    public int getUserSkillId() {
        return userSkillId;
    }

    public void setUserSkillId(int userSkillId) {
        this.userSkillId = userSkillId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", skillType, skillName, category);
    }
}
