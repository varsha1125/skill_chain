package com.skillchain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: TREE DATA STRUCTURE FOR RECURSION
 * ============================================================================
 * Represents a hierarchical category node in the Skill Catalog tree
 * (e.g., Computer Science -> Programming -> Java, Python).
 * Used by recursive traversal algorithms to list and explore skills.
 */
public class SkillCategoryNode {

    private String name;
    private List<SkillCategoryNode> children;
    private List<String> skills;

    public SkillCategoryNode(String name) {
        this.name = name;
        this.children = new ArrayList<>();
        this.skills = new ArrayList<>();
    }

    public void addChild(SkillCategoryNode child) {
        this.children.add(child);
    }

    public void addSkill(String skill) {
        this.skills.add(skill);
    }

    public String getName() {
        return name;
    }

    public List<SkillCategoryNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<String> getSkills() {
        return Collections.unmodifiableList(skills);
    }
}
