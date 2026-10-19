package com.skillchain.util;

import com.skillchain.model.SkillCategoryNode;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: RECURSION
 * ============================================================================
 * Utility providing recursive traversal algorithms over hierarchical
 * skill category trees.
 *
 * Why recursion fits here:
 * Academic and industry skill taxonomies naturally form nested trees
 * (Root -> Domain -> Subdomain -> Specific Skills).
 * A recursive method is the most elegant, mathematically rigorous way to
 * traverse arbitrarily deep tree hierarchies without requiring explicit
 * manual stack management or nested loop boilerplate.
 */
public final class SkillTreeUtil {

    private SkillTreeUtil() {
    }

    /**
     * Builds a predefined default category tree representing student academic skills.
     *
     * @return Root node of the category taxonomy.
     */
    public static SkillCategoryNode createDefaultSkillTree() {
        SkillCategoryNode root = new SkillCategoryNode("Academic & Technical Skills");

        // Software Engineering Domain
        SkillCategoryNode software = new SkillCategoryNode("Software Engineering");
        
        SkillCategoryNode programming = new SkillCategoryNode("Programming Languages");
        programming.addSkill("Java");
        programming.addSkill("Python");
        programming.addSkill("C++");
        programming.addSkill("Go");
        
        SkillCategoryNode webDev = new SkillCategoryNode("Web Technologies");
        webDev.addSkill("JavaScript");
        webDev.addSkill("React");
        webDev.addSkill("Node.js");
        
        software.addChild(programming);
        software.addChild(webDev);

        // Data & Systems Domain
        SkillCategoryNode dataSystems = new SkillCategoryNode("Data & Intelligent Systems");
        
        SkillCategoryNode databases = new SkillCategoryNode("Databases & Storage");
        databases.addSkill("SQL");
        databases.addSkill("PostgreSQL");
        
        SkillCategoryNode ai = new SkillCategoryNode("Artificial Intelligence");
        ai.addSkill("Machine Learning");
        ai.addSkill("Deep Learning");
        
        dataSystems.addChild(databases);
        dataSystems.addChild(ai);

        // Creative & Media Domain
        SkillCategoryNode creative = new SkillCategoryNode("Creative & Visual Arts");
        
        SkillCategoryNode design = new SkillCategoryNode("Digital Design");
        design.addSkill("UI/UX Design");
        design.addSkill("Graphic Design");
        design.addSkill("3D Modeling");
        
        creative.addChild(design);

        root.addChild(software);
        root.addChild(dataSystems);
        root.addChild(creative);

        return root;
    }

    /**
     * RECURSIVE METHOD 1: Tree Visualization
     * Recursively walks the category tree, formatting an indented visual outline.
     *
     * Base Case: When a node has no child categories, it prints its own skills
     * and returns to the previous recursive call.
     * Recursive Step: For each child node in children, calls traverseTreeRecursive(child, depth + 1, ...).
     *
     * @param node   Current category node being processed
     * @param depth  Current depth level in the tree (controls indentation)
     * @param buffer StringBuilder accumulating formatted output
     */
    public static void traverseTreeRecursive(SkillCategoryNode node, int depth, StringBuilder buffer) {
        if (node == null) {
            return; // Guard base case
        }

        // Indentation proportional to depth
        String indent = "   ".repeat(depth);
        buffer.append(indent)
              .append("📂 ")
              .append(node.getName())
              .append("\n");

        // Print leaf skills attached to this category
        for (String skill : node.getSkills()) {
            buffer.append(indent)
                  .append("   ├── ⚡ ")
                  .append(skill)
                  .append("\n");
        }

        // Recursive call for each sub-category
        for (SkillCategoryNode child : node.getChildren()) {
            traverseTreeRecursive(child, depth + 1, buffer);
        }
    }

    /**
     * RECURSIVE METHOD 2: Skill Collection
     * Recursively aggregates all leaf skill names under any category node.
     *
     * @param node The starting category node
     * @return Comprehensive list of skill names in the subtree
     */
    public static List<String> collectAllSkillsRecursive(SkillCategoryNode node) {
        List<String> accumulatedSkills = new ArrayList<>();
        if (node == null) {
            return accumulatedSkills;
        }

        // Add local skills
        accumulatedSkills.addAll(node.getSkills());

        // Recursively accumulate from children
        for (SkillCategoryNode child : node.getChildren()) {
            accumulatedSkills.addAll(collectAllSkillsRecursive(child));
        }

        return accumulatedSkills;
    }

    /**
     * RECURSIVE METHOD 3: Subtree Search
     * Recursively searches for a category node matching targetName.
     *
     * @param current    Current node inspected
     * @param targetName Name of the category to find
     * @return Matching SkillCategoryNode or null if not found
     */
    public static SkillCategoryNode findCategoryRecursive(SkillCategoryNode current, String targetName) {
        if (current == null || targetName == null) {
            return null;
        }

        if (current.getName().equalsIgnoreCase(targetName.trim())) {
            return current; // Match found at current node
        }

        // Recursively check children
        for (SkillCategoryNode child : current.getChildren()) {
            SkillCategoryNode found = findCategoryRecursive(child, targetName);
            if (found != null) {
                return found;
            }
        }

        return null; // Not found in this branch
    }
}
