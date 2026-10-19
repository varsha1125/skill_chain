package com.skillchain.dao;

import com.skillchain.model.User;
import com.skillchain.util.DatabaseConnection;
import com.skillchain.util.DuplicateEmailException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * JDBC implementation of UserDao using PreparedStatements and try-with-resources.
 */
public class UserDaoImpl implements UserDao {

    @Override
    public User save(User user) throws DuplicateEmailException, SQLException {
        // Decision making: Check for existing email to prevent duplicate accounts
        if (existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("An account with email '" + user.getEmail() + "' already exists.");
        }

        final String sql = "INSERT INTO users (name, email, password_hash, salt) VALUES (?, ?, ?, ?)";

        // Try-with-resources guarantees automatic resource cleanup
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail().toLowerCase().trim());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());

            stmt.executeUpdate();

            // Retrieve generated primary key ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }

            return user;
        } catch (SQLException e) {
            // MySQL error code 1062 corresponds to ER_DUP_ENTRY for UNIQUE constraints
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEmailException("An account with email '" + user.getEmail() + "' already exists.", e);
            }
            throw e;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        final String sql = "SELECT user_id, name, email, password_hash, salt, created_at FROM users WHERE LOWER(email) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.toLowerCase().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                // Looping / Record advancement using standard while loop
                while (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int userId) throws SQLException {
        final String sql = "SELECT user_id, name, email, password_hash, salt, created_at FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        final String sql = "SELECT 1 FROM users WHERE LOWER(email) = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.toLowerCase().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public java.util.List<com.skillchain.model.PeerStudent> findAllPeersWithSkills(int excludeUserId) throws SQLException {
        final String sql =
                "SELECT u.user_id, u.name, u.email, " +
                "       COALESCE(GROUP_CONCAT(DISTINCT CASE WHEN us.skill_type = 'TEACH' THEN s.skill_name END SEPARATOR ', '), 'None listed yet') AS teach_skills, " +
                "       COALESCE(GROUP_CONCAT(DISTINCT CASE WHEN us.skill_type = 'LEARN' THEN s.skill_name END SEPARATOR ', '), 'None listed yet') AS learn_skills " +
                "FROM users u " +
                "LEFT JOIN user_skills us ON u.user_id = us.user_id " +
                "LEFT JOIN skills s ON us.skill_id = s.skill_id " +
                "WHERE u.user_id != ? " +
                "GROUP BY u.user_id, u.name, u.email " +
                "ORDER BY u.name ASC";

        java.util.List<com.skillchain.model.PeerStudent> peers = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, excludeUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    peers.add(new com.skillchain.model.PeerStudent(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("teach_skills"),
                            rs.getString("learn_skills")
                    ));
                }
            }
        }
        return peers;
    }

    /**
     * Helper method to map a ResultSet row to a User entity.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("created_at");
        return new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}

