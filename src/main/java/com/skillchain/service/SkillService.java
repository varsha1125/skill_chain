package com.skillchain.service;

import com.skillchain.dao.SkillDao;
import com.skillchain.dao.SkillDaoImpl;
import com.skillchain.model.Skill;
import com.skillchain.model.SkillCategoryNode;
import com.skillchain.model.SkillType;
import com.skillchain.model.UserSkill;
import com.skillchain.util.DuplicateSkillException;
import com.skillchain.util.SkillTreeUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * SERVICE LAYER: SKILL CATALOG & PORTFOLIO MANAGEMENT
 * ============================================================================
 * Coordinates skill catalog searches, skill assignment, category trees,
 * and portfolio updates.
 */
public class SkillService {

    private final SkillDao skillDao;

    public SkillService() {
        this(new SkillDaoImpl());
    }

    public SkillService(SkillDao skillDao) {
        this.skillDao = skillDao;
    }

    public List<Skill> getAllSkills() throws SQLException {
        return skillDao.findAll();
    }

    // ========================================================================
    // POLYMORPHISM: METHOD OVERLOADING (Search)
    // ========================================================================

    /**
     * Overload 1: Keyword search across all categories.
     */
    public List<Skill> searchSkills(String keyword) throws SQLException {
        return skillDao.search(keyword);
    }

    /**
     * Overload 2: Keyword search restricted to a specific category.
     */
    public List<Skill> searchSkills(String keyword, String category) throws SQLException {
        return skillDao.search(keyword, category);
    }

    // ========================================================================
    // USER SKILLS MANAGEMENT
    // ========================================================================

    /**
     * Adds an existing catalog skill to the user's TEACH or LEARN list.
     */
    public void addUserSkill(int userId, int skillId, SkillType skillType)
            throws DuplicateSkillException, SQLException {
        if (userId <= 0 || skillId <= 0 || skillType == null) {
            throw new IllegalArgumentException("Invalid user, skill, or skill type provided.");
        }
        skillDao.addUserSkill(userId, skillId, skillType);
    }

    /**
     * Adds a skill to the user portfolio. If the skill doesn't exist in the catalog,
     * it creates it first, then attaches it to the user.
     */
    public void addCustomUserSkill(int userId, String skillName, String category, SkillType skillType)
            throws DuplicateSkillException, SQLException {
        if (skillName == null || skillName.trim().length() < 2) {
            throw new IllegalArgumentException("Skill name must be at least 2 characters long.");
        }

        String cleanedName = skillName.trim();
        String cleanedCat = (category == null || category.trim().isEmpty()) ? "General" : category.trim();

        // Check if skill already exists in catalog
        Optional<Skill> existingSkill = skillDao.findByName(cleanedName);
        Skill skill;
        if (existingSkill.isPresent()) {
            skill = existingSkill.get();
        } else {
            skill = skillDao.createSkill(new Skill(cleanedName, cleanedCat));
        }

        skillDao.addUserSkill(userId, skill.getSkillId(), skillType);
    }

    /**
     * Removes an associated skill from the user's portfolio.
     */
    public void removeUserSkill(int userSkillId, int userId) throws SQLException {
        if (userSkillId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid user skill identifier.");
        }
        skillDao.removeUserSkill(userSkillId, userId);
    }

    // ========================================================================
    // POLYMORPHISM: METHOD OVERLOADING (User Skills Retrieval)
    // ========================================================================

    /**
     * Overload 1: Retrieves all skills for the user (both TEACH and LEARN).
     */
    public List<UserSkill> getUserSkills(int userId) throws SQLException {
        return skillDao.getUserSkills(userId);
    }

    /**
     * Overload 2: Retrieves only skills of a specific type (TEACH or LEARN).
     */
    public List<UserSkill> getUserSkills(int userId, SkillType skillType) throws SQLException {
        return skillDao.getUserSkills(userId, skillType);
    }

    // ========================================================================
    // RECURSIVE CATEGORY TREE VISUALIZATION
    // ========================================================================

    /**
     * Invokes the recursive tree walker to generate an organized outline
     * of the academic skill category tree.
     *
     * @return Formatted multi-line string representing the category taxonomy.
     */
    public String getFormattedSkillTaxonomyTree() {
        SkillCategoryNode root = SkillTreeUtil.createDefaultSkillTree();
        StringBuilder buffer = new StringBuilder();
        buffer.append("=== ACADEMIC SKILL TAXONOMY (RECURSIVE TREE VIEW) ===\n\n");
        SkillTreeUtil.traverseTreeRecursive(root, 0, buffer);
        return buffer.toString();
    }
}
