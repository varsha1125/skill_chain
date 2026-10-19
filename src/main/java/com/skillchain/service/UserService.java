package com.skillchain.service;

import com.skillchain.dao.UserDao;
import com.skillchain.dao.UserDaoImpl;
import com.skillchain.model.User;
import com.skillchain.util.DuplicateEmailException;
import com.skillchain.util.InvalidLoginException;
import com.skillchain.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;

/**
 * ============================================================================
 * SERVICE LAYER: BUSINESS LOGIC & AUTHENTICATION
 * ============================================================================
 * Enforces business rules, string validation, password hashing, and credentials
 * verification before passing control to the DAO layer.
 */
public class UserService {

    private final UserDao userDao;

    public UserService() {
        this(new UserDaoImpl());
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Registers a new student account.
     *
     * @param name            Student full name
     * @param email           Valid email address
     * @param password        Plaintext password
     * @param confirmPassword Password confirmation
     * @return Saved User entity
     * @throws IllegalArgumentException on client validation failure
     * @throws DuplicateEmailException  if the email is already taken
     * @throws SQLException             on database failure
     */
    public User register(String name, String email, String password, String confirmPassword)
            throws DuplicateEmailException, SQLException {

        // STRING VALIDATION & DECISION MAKING:
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("Full name must be at least 2 characters.");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Please enter a valid email address (e.g., student@university.edu).");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // Check if email already exists
        if (userDao.existsByEmail(email)) {
            throw new DuplicateEmailException("An account with email '" + email.trim() + "' already exists.");
        }

        // Cryptographic Salting & SHA-256 Hashing
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);

        User newUser = new User();
        newUser.setName(name.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPasswordHash(passwordHash);
        newUser.setSalt(salt);

        return userDao.save(newUser);
    }

    /**
     * Authenticates a student using their email and plaintext password.
     *
     * @param email    Student email
     * @param password Plaintext password
     * @return Authenticated User entity
     * @throws InvalidLoginException if credentials do not match
     * @throws SQLException          on database query error
     */
    public User login(String email, String password) throws InvalidLoginException, SQLException {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidLoginException("Please provide both email and password.");
        }

        Optional<User> userOpt = userDao.findByEmail(email.trim().toLowerCase());

        // Decision making: verify user existence
        if (userOpt.isEmpty()) {
            throw new InvalidLoginException("Invalid email or password. Please try again.");
        }

        User user = userOpt.get();

        // Constant-time salted SHA-256 password hash comparison
        boolean passwordMatches = PasswordUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new InvalidLoginException("Invalid email or password. Please try again.");
        }

        return user;
    }
}
