package com.skillchain.model;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: CLASSES & ENCAPSULATION
 * ============================================================================
 * Represents a reciprocal (mutual) match between two students where:
 * 1. User A teaches a skill that User B wants to learn.
 * 2. User B teaches a skill that User A wants to learn.
 */
public class ReciprocalMatch {

    private int matchedUserId;
    private String matchedUserName;
    private String matchedUserEmail;

    private int userOfferedSkillId;
    private String userOfferedSkillName;
    private String userOfferedCategory;

    private int partnerOfferedSkillId;
    private String partnerOfferedSkillName;
    private String partnerOfferedCategory;

    public ReciprocalMatch() {
    }

    public ReciprocalMatch(int matchedUserId, String matchedUserName, String matchedUserEmail,
                           int userOfferedSkillId, String userOfferedSkillName, String userOfferedCategory,
                           int partnerOfferedSkillId, String partnerOfferedSkillName, String partnerOfferedCategory) {
        this.matchedUserId = matchedUserId;
        this.matchedUserName = matchedUserName;
        this.matchedUserEmail = matchedUserEmail;
        this.userOfferedSkillId = userOfferedSkillId;
        this.userOfferedSkillName = userOfferedSkillName;
        this.userOfferedCategory = userOfferedCategory;
        this.partnerOfferedSkillId = partnerOfferedSkillId;
        this.partnerOfferedSkillName = partnerOfferedSkillName;
        this.partnerOfferedCategory = partnerOfferedCategory;
    }

    public int getMatchedUserId() {
        return matchedUserId;
    }

    public void setMatchedUserId(int matchedUserId) {
        this.matchedUserId = matchedUserId;
    }

    public String getMatchedUserName() {
        return matchedUserName;
    }

    public void setMatchedUserName(String matchedUserName) {
        this.matchedUserName = matchedUserName;
    }

    public String getMatchedUserEmail() {
        return matchedUserEmail;
    }

    public void setMatchedUserEmail(String matchedUserEmail) {
        this.matchedUserEmail = matchedUserEmail;
    }

    public int getUserOfferedSkillId() {
        return userOfferedSkillId;
    }

    public void setUserOfferedSkillId(int userOfferedSkillId) {
        this.userOfferedSkillId = userOfferedSkillId;
    }

    public String getUserOfferedSkillName() {
        return userOfferedSkillName;
    }

    public void setUserOfferedSkillName(String userOfferedSkillName) {
        this.userOfferedSkillName = userOfferedSkillName;
    }

    public String getUserOfferedCategory() {
        return userOfferedCategory;
    }

    public void setUserOfferedCategory(String userOfferedCategory) {
        this.userOfferedCategory = userOfferedCategory;
    }

    public int getPartnerOfferedSkillId() {
        return partnerOfferedSkillId;
    }

    public void setPartnerOfferedSkillId(int partnerOfferedSkillId) {
        this.partnerOfferedSkillId = partnerOfferedSkillId;
    }

    public String getPartnerOfferedSkillName() {
        return partnerOfferedSkillName;
    }

    public void setPartnerOfferedSkillName(String partnerOfferedSkillName) {
        this.partnerOfferedSkillName = partnerOfferedSkillName;
    }

    public String getPartnerOfferedCategory() {
        return partnerOfferedCategory;
    }

    public void setPartnerOfferedCategory(String partnerOfferedCategory) {
        this.partnerOfferedCategory = partnerOfferedCategory;
    }

    public String getExplanation() {
        return "You teach " + userOfferedSkillName + " (" + userOfferedCategory + ") ⇄ " +
                matchedUserName + " teaches " + partnerOfferedSkillName + " (" + partnerOfferedCategory + ")";
    }

    @Override
    public String toString() {
        return "ReciprocalMatch{" +
                "partner=" + matchedUserName +
                ", youTeach=" + userOfferedSkillName +
                ", theyTeach=" + partnerOfferedSkillName +
                '}';
    }
}
