package com.skillchain.model;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: INHERITANCE & METHOD OVERRIDING (POLYMORPHISM)
 * ============================================================================
 * User extends Person, inheriting foundational identity fields (id, name,
 * email, createdAt) while augmenting the model with security credentials
 * (passwordHash, salt).
 *
 * DESIGN JUSTIFICATION (Why User extends Person rather than Student/Mentor):
 * In a peer-to-peer reciprocal skill exchange platform, every participant
 * is simultaneously a Mentor (teaching skills they excel at) and a Learner
 * (seeking skills they want to acquire).
 *
 * Forcing separate "Student" and "Mentor" classes creates an artificial,
 * inflexible boundary or requires multiple inheritance (illegal in Java).
 * Modeling authenticated participants as `User extends Person` maintains a
 * clean single-inheritance model where every student member has equal peer
 * capabilities to offer and receive knowledge.
 */
public class User extends Person {

    private String passwordHash;
    private String salt;

    /**
     * Default constructor.
     */
    public User() {
        super();
    }

    /**
     * Parameterized constructor for User.
     *
     * @param id           Unique user id
     * @param name         Student's full name
     * @param email        Institutional or personal email
     * @param passwordHash Hex-encoded SHA-256 password hash
     * @param salt         Hex-encoded random salt
     * @param createdAt    Creation timestamp
     */
    public User(int id, String name, String email, String passwordHash, String salt, LocalDateTime createdAt) {
        super(id, name, email, createdAt);
        setPasswordHash(passwordHash);
        setSalt(salt);
    }

    /**
     * POLYMORPHISM: Method Overriding
     * Implements the abstract contract from {@link Person#getRole()}.
     *
     * @return Description of this member's system role.
     */
    @Override
    public String getRole() {
        return "Student Member (Peer Exchange)";
    }

    /**
     * POLYMORPHISM: Method Overriding
     * Implements the abstract contract from {@link Person#getSummary()}.
     *
     * @return Summary string representing this authenticated member.
     */
    @Override
    public String getSummary() {
        return String.format("Student Member: %s (%s)", getName(), getEmail());
    }

    // ========================================================================
    // ENCAPSULATION: Getters & Setters with Defensive Validation
    // ========================================================================

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be empty.");
        }
        this.passwordHash = passwordHash.trim();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        if (salt == null || salt.trim().isEmpty()) {
            throw new IllegalArgumentException("Salt cannot be empty.");
        }
        this.salt = salt.trim();
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', role='%s'}",
                getId(), getName(), getEmail(), getRole());
    }
}
