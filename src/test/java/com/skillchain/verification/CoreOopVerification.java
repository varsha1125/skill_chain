package com.skillchain.verification;

import com.skillchain.model.Person;
import com.skillchain.model.Skill;
import com.skillchain.model.SkillCategoryNode;
import com.skillchain.model.SkillType;
import com.skillchain.model.User;
import com.skillchain.util.PasswordUtil;
import com.skillchain.util.SkillTreeUtil;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dependency-free smoke test for the core OOP implementation.
 * Run with assertions enabled using: java -ea ... CoreOopVerification
 */
public final class CoreOopVerification {

    private CoreOopVerification() {
    }

    public static void main(String[] args) {
        verifyInheritanceAndPolymorphism();
        verifyEncapsulationAndValidation();
        verifyEnumAndRecursion();
        verifyPasswordHashing();
        System.out.println("Core OOP verification passed.");
    }

    private static void verifyInheritanceAndPolymorphism() {
        Person person = new User(1, "Alice Johnson", "alice@example.com", "hash", "salt", LocalDateTime.now());
        check(person instanceof User, "User must inherit from Person");
        check(person.getRole().contains("Student"), "Runtime overriding must return the User role");
        check(person.getSummary().contains("Alice Johnson"), "Runtime overriding must return the User summary");
    }

    private static void verifyEncapsulationAndValidation() {
        Skill skill = new Skill(" Java ", " Programming ");
        check("Java".equals(skill.getSkillName()), "Skill name should be normalized");
        check("Programming".equals(skill.getCategory()), "Skill category should be normalized");
        expectIllegalArgument(() -> new Skill("", "Programming"), "Empty skill names must be rejected");
    }

    private static void verifyEnumAndRecursion() {
        check(SkillType.TEACH == SkillType.fromString("teach"), "SkillType parsing should be case-insensitive");
        SkillCategoryNode root = SkillTreeUtil.createDefaultSkillTree();
        List<String> skills = SkillTreeUtil.collectAllSkillsRecursive(root);
        check(skills.contains("Java"), "Recursive tree collection should include Java");
        check(SkillTreeUtil.findCategoryRecursive(root, "Programming Languages") != null,
                "Recursive category search should find nested categories");
    }

    private static void verifyPasswordHashing() {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword("Password123!", salt);
        check(PasswordUtil.verifyPassword("Password123!", salt, hash), "Correct password should verify");
        check(!PasswordUtil.verifyPassword("wrong-password", salt, hash), "Incorrect password should fail");
    }

    private static void expectIllegalArgument(Runnable action, String message) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError(message);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
