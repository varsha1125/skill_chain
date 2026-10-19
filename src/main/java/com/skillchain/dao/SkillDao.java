package com.skillchain.dao;

import com.skillchain.model.Skill;
import com.skillchain.model.SkillType;
import com.skillchain.model.UserSkill;
import com.skillchain.util.DuplicateSkillException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * JAVA OOP CONCEPTS: INTERFACE INHERITANCE & METHOD OVERLOADING
 * ============================================================================
 * SkillDao extends Searchable<Skill>, inheriting the base search contract,
 * and adds overloaded query variants and skill-assignment operations.
 */
public interface SkillDao extends Searchable<Skill> {

    /**
     * Lists all available skills in the global catalog.
     *
     * @return List of all Skill entities.
     * @throws SQLException if a query failure occurs.
     */
    List<Skill> findAll() throws SQLException;

    /**
     * Finds a skill by primary key ID.
     *
     * @param skillId Unique skill ID.
     * @return Optional containing the skill if found.
     * @throws SQLException if a query failure occurs.
     */
    Optional<Skill> findById(int skillId) throws SQLException;

    /**
     * Finds a skill by exact or case-insensitive name.
     *
     * @param skillName The name of the skill.
     * @return Optional containing the skill if found.
     * @throws SQLException if a query failure occurs.
     */
    Optional<Skill> findByName(String skillName) throws SQLException;

    /**
     * Creates and stores a new skill in the catalog.
     *
     * @param skill The Skill entity to persist.
     * @return The persisted Skill with auto-generated ID.
     * @throws SQLException if an insertion error occurs.
     */
    Skill createSkill(Skill skill) throws SQLException;

    // ========================================================================
    // POLYMORPHISM: METHOD OVERLOADING (Search Variants)
    // ========================================================================

    /**
     * Overloaded method 1: Searches skills matching keyword across all categories.
     * (Contract inherited from {@link Searchable#search(String)}).
     *
     * @param keyword Partial or complete skill name.
     * @return Matching skills.
     * @throws SQLException if an error occurs.
     */
    @Override
    List<Skill> search(String keyword) throws SQLException;

    /**
     * Overloaded method 2: Searches skills matching keyword filtered by category.
     * Demonstrates compile-time polymorphism (Method Overloading).
     *
     * @param keyword  Partial or complete skill name.
     * @param category Filter category name.
     * @return Matching skills within the selected category.
     * @throws SQLException if an error occurs.
     */
    List<Skill> search(String keyword, String category) throws SQLException;

    // ========================================================================
    // USER SKILLS MANAGEMENT
    // ========================================================================

    /**
     * Associates a skill with a user as either TEACH or LEARN.
     *
     * @param userId    Student's ID
     * @param skillId   Target skill's ID
     * @param skillType Either TEACH or LEARN
     * @throws DuplicateSkillException if this user already registered this skill as TEACH/LEARN
     * @throws SQLException            if a database query error occurs
     */
    void addUserSkill(int userId, int skillId, SkillType skillType) throws DuplicateSkillException, SQLException;

    /**
     * Removes an associated skill from a student's portfolio.
     *
     * @param userSkillId The primary key of the user_skill record
     * @param userId      The student ID owning the record (security check)
     * @throws SQLException if a deletion error occurs
     */
    void removeUserSkill(int userSkillId, int userId) throws SQLException;

    // ========================================================================
    // POLYMORPHISM: METHOD OVERLOADING (User Skills Retrieval)
    // ========================================================================

    /**
     * Overloaded method 1: Retrieves ALL skills (both TEACH and LEARN) for a user.
     *
     * @param userId The student's ID
     * @return List of UserSkill associations
     * @throws SQLException if an error occurs
     */
    List<UserSkill> getUserSkills(int userId) throws SQLException;

    /**
     * Overloaded method 2: Retrieves ONLY skills of a specific type (TEACH or LEARN).
     * Demonstrates compile-time polymorphism (Method Overloading).
     *
     * @param userId    The student's ID
     * @param skillType Target SkillType filter
     * @return List of UserSkill associations matching the filter
     * @throws SQLException if an error occurs
     */
    List<UserSkill> getUserSkills(int userId, SkillType skillType) throws SQLException;

    /**
     * Checks if a user already possesses a specific skill mapping.
     *
     * @param userId    The student's ID
     * @param skillId   The target skill's ID
     * @param skillType Either TEACH or LEARN
     * @return true if mapping exists, false otherwise
     * @throws SQLException if an error occurs
     */
    boolean hasUserSkill(int userId, int skillId, SkillType skillType) throws SQLException;
}
