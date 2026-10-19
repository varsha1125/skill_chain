package com.skillchain.model;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: ABSTRACTION
 * ============================================================================
 * Person is an abstract class serving as the base model for individuals in
 * the system. It encapsulates shared identity attributes (id, name, email)
 * and enforces the implementation of the abstract method {@code getRole()}.
 *
 * Why Abstraction fits here:
 * A generic "Person" cannot log into the platform directly without account
 * credentials, nor can they hold a skill inventory. By declaring Person
 * abstract, we prevent instantiation of incomplete identities while providing
 * a robust, extensible foundation for authenticated users.
 */
public abstract class Person {

    private int id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public Person() {
    }

    /**
     * Parameterized constructor initializing core personal identity.
     *
     * @param id        Unique identifier
     * @param name      Full name of the person
     * @param email     Email address
     * @param createdAt Account creation timestamp
     */
    public Person(int id, String name, String email, LocalDateTime createdAt) {
        setId(id);
        setName(name);
        setEmail(email);
        this.createdAt = createdAt;
    }

    /**
     * Abstract method defining the functional role of the individual.
     * Must be overridden by concrete subclasses (Demonstrating Polymorphism).
     *
     * @return The specific role description string.
     */
    public abstract String getRole();

    /**
     * Abstract template method returning a concise summary of the individual.
     * Must be overridden by concrete subclasses (Demonstrating Polymorphism).
     *
     * @return Summary string of identity details.
     */
    public abstract String getSummary();

    // ========================================================================
    // ENCAPSULATION: Getters and Setters with Business Validation
    // ========================================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Person ID cannot be negative.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format provided.");
        }
        this.email = email.trim().toLowerCase();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %d, Email: %s)", name, id, email);
    }
}
