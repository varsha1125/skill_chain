package com.skillchain.dao;

import com.skillchain.model.PeerStudent;
import com.skillchain.model.User;
import com.skillchain.util.DuplicateEmailException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface defining persistence operations for User accounts.
 */
public interface UserDao {

    /**
     * Persists a new user record into the database.
     *
     * @param user The user entity populated with name, email, passwordHash, and salt.
     * @return The saved User entity with auto-generated user_id.
     * @throws DuplicateEmailException if an account with this email already exists.
     * @throws SQLException            if a database access failure occurs.
     */
    User save(User user) throws DuplicateEmailException, SQLException;

    /**
     * Looks up a user record by email address.
     *
     * @param email The user email address.
     * @return Optional containing the User if found, or empty.
     * @throws SQLException if a database query failure occurs.
     */
    Optional<User> findByEmail(String email) throws SQLException;

    /**
     * Looks up a user record by primary key id.
     *
     * @param userId The unique user ID.
     * @return Optional containing the User if found, or empty.
     * @throws SQLException if a database query failure occurs.
     */
    Optional<User> findById(int userId) throws SQLException;

    /**
     * Checks if an email is already registered in the system.
     *
     * @param email The email to test.
     * @return true if email exists, false otherwise.
     * @throws SQLException if a database query failure occurs.
     */
    boolean existsByEmail(String email) throws SQLException;

    /**
     * Retrieves all student peers on campus along with aggregated skills they teach and learn.
     *
     * @param excludeUserId User ID to exclude (typically the active user).
     * @return List of PeerStudent entities.
     * @throws SQLException if a database query failure occurs.
     */
    List<PeerStudent> findAllPeersWithSkills(int excludeUserId) throws SQLException;
}

