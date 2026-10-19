package com.skillchain.dao;

import com.skillchain.model.Skill;
import com.skillchain.model.SkillType;
import com.skillchain.model.UserSkill;
import com.skillchain.util.DatabaseConnection;
import com.skillchain.util.DuplicateSkillException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of SkillDao and Searchable<Skill>.
 * Demonstrates:
 * - Interface Implementation
 * - Method Overloading & Overriding
 * - Try-with-resources and PreparedStatements
 * - Looping with while(rs.next())
 */
public class SkillDaoImpl implements SkillDao {

    @Override
    public List<Skill> findAll() throws SQLException {
        final String sql = "SELECT skill_id, skill_name, category FROM skills ORDER BY skill_name ASC";
        List<Skill> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapRowToSkill(rs));
            }
        }
        return results;
    }

    @Override
    public Optional<Skill> findById(int skillId) throws SQLException {
        final String sql = "SELECT skill_id, skill_name, category FROM skills WHERE skill_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, skillId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Skill> findByName(String skillName) throws SQLException {
        final String sql = "SELECT skill_id, skill_name, category FROM skills WHERE LOWER(skill_name) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, skillName.toLowerCase().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Skill createSkill(Skill skill) throws SQLException {
        final String sql = "INSERT INTO skills (skill_name, category) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, skill.getSkillName().trim());
            stmt.setString(2, skill.getCategory().trim());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    skill.setSkillId(generatedKeys.getInt(1));
                }
            }
            return skill;
        }
    }

    // ========================================================================
    // METHOD OVERLOADING (Polymorphism) & SEARCHABLE INTERFACE IMPLEMENTATION
    // ========================================================================

    /**
     * Search Variant 1: Inherited from Searchable<Skill>.
     */
    @Override
    public List<Skill> search(String keyword) throws SQLException {
        String pattern = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
        final String sql = "SELECT skill_id, skill_name, category FROM skills " +
                           "WHERE LOWER(skill_name) LIKE ? OR LOWER(category) LIKE ? " +
                           "ORDER BY skill_name ASC";

        List<Skill> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSkill(rs));
                }
            }
        }
        return list;
    }

    /**
     * Search Variant 2: Overloaded method filtering by category.
     */
    @Override
    public List<Skill> search(String keyword, String category) throws SQLException {
        if (category == null || category.equalsIgnoreCase("All Categories") || category.trim().isEmpty()) {
            return search(keyword); // Delegate to single-parameter overload
        }

        String pattern = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
        final String sql = "SELECT skill_id, skill_name, category FROM skills " +
                           "WHERE LOWER(skill_name) LIKE ? AND LOWER(category) = ? " +
                           "ORDER BY skill_name ASC";

        List<Skill> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, category.trim().toLowerCase());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSkill(rs));
                }
            }
        }
        return list;
    }

    // ========================================================================
    // USER SKILLS OPERATIONS
    // ========================================================================

    @Override
    public void addUserSkill(int userId, int skillId, SkillType skillType) throws DuplicateSkillException, SQLException {
        // Guard against duplicate skill associations
        if (hasUserSkill(userId, skillId, skillType)) {
            throw new DuplicateSkillException("You have already added this skill under " + skillType.name() + ".");
        }

        final String sql = "INSERT INTO user_skills (user_id, skill_id, skill_type) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, skillId);
            stmt.setString(3, skillType.name());

            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateSkillException("You have already added this skill under " + skillType.name() + ".", e);
            }
            throw e;
        }
    }

    @Override
    public void removeUserSkill(int userSkillId, int userId) throws SQLException {
        // Enforce ownership: only the user owning the record can delete it
        final String sql = "DELETE FROM user_skills WHERE user_skill_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userSkillId);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        }
    }

    // ========================================================================
    // METHOD OVERLOADING: getUserSkills
    // ========================================================================

    /**
     * User Skills Variant 1: Retrieves all skills (both TEACH and LEARN).
     */
    @Override
    public List<UserSkill> getUserSkills(int userId) throws SQLException {
        final String sql = "SELECT us.user_skill_id, us.user_id, us.skill_id, s.skill_name, s.category, " +
                           "us.skill_type, us.created_at " +
                           "FROM user_skills us " +
                           "JOIN skills s ON us.skill_id = s.skill_id " +
                           "WHERE us.user_id = ? " +
                           "ORDER BY us.skill_type ASC, s.skill_name ASC";

        List<UserSkill> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUserSkill(rs));
                }
            }
        }
        return list;
    }

    /**
     * User Skills Variant 2: Overloaded to filter by SkillType.
     */
    @Override
    public List<UserSkill> getUserSkills(int userId, SkillType skillType) throws SQLException {
        final String sql = "SELECT us.user_skill_id, us.user_id, us.skill_id, s.skill_name, s.category, " +
                           "us.skill_type, us.created_at " +
                           "FROM user_skills us " +
                           "JOIN skills s ON us.skill_id = s.skill_id " +
                           "WHERE us.user_id = ? AND us.skill_type = ? " +
                           "ORDER BY s.skill_name ASC";

        List<UserSkill> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, skillType.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUserSkill(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean hasUserSkill(int userId, int skillId, SkillType skillType) throws SQLException {
        final String sql = "SELECT 1 FROM user_skills WHERE user_id = ? AND skill_id = ? AND skill_type = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, skillId);
            stmt.setString(3, skillType.name());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Skill mapRowToSkill(ResultSet rs) throws SQLException {
        return new Skill(
                rs.getInt("skill_id"),
                rs.getString("skill_name"),
                rs.getString("category")
        );
    }

    private UserSkill mapRowToUserSkill(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new UserSkill(
                rs.getInt("user_skill_id"),
                rs.getInt("user_id"),
                rs.getInt("skill_id"),
                rs.getString("skill_name"),
                rs.getString("category"),
                SkillType.fromString(rs.getString("skill_type")),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}
