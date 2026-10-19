package com.skillchain.model;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: INHERITANCE & POLYMORPHISM
 * ============================================================================
 * PeerStudent extends Person, inheriting foundational identity fields
 * (id, name, email) while adding aggregated portfolio attributes
 * (teachingSkills, learningSkills) for student directory browsing.
 */
public class PeerStudent extends Person {

    private String teachingSkills;
    private String learningSkills;

    public PeerStudent() {
        super();
    }

    public PeerStudent(int userId, String name, String email, String teachingSkills, String learningSkills) {
        super(userId, name, email, null);
        this.teachingSkills = teachingSkills;
        this.learningSkills = learningSkills;
    }

    @Override
    public String getRole() {
        return "Campus Peer Student";
    }

    @Override
    public String getSummary() {
        return String.format("%s (%s) [Teaches: %s | Learns: %s]",
                getName(), getEmail(), getTeachingSkills(), getLearningSkills());
    }

    public int getUserId() {
        return getId();
    }

    public void setUserId(int userId) {
        setId(userId);
    }

    public String getTeachingSkills() {
        return teachingSkills != null && !teachingSkills.isEmpty() ? teachingSkills : "None listed";
    }

    public void setTeachingSkills(String teachingSkills) {
        this.teachingSkills = teachingSkills;
    }

    public String getLearningSkills() {
        return learningSkills != null && !learningSkills.isEmpty() ? learningSkills : "None listed";
    }

    public void setLearningSkills(String learningSkills) {
        this.learningSkills = learningSkills;
    }

    @Override
    public String toString() {
        return getName() + " (" + getEmail() + ")";
    }
}
