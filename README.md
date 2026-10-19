# 🎓 SKILL CHAIN: Campus Peer-to-Peer Skill Exchange Platform

> **A Desktop Application Built with Core Java, Swing GUI, OOP Principles, and MySQL Database Persistence.**

---

## 📌 1. Project Overview

**Skill Chain** is a desktop application designed for university campuses. It connects students who wish to exchange academic and technical knowledge without monetary transactions. 

Through a **Reciprocal Matching Algorithm**, the platform automatically identifies two-way skill overlaps between students:
$$\text{Student A teaches what Student B wants to learn} \quad \land \quad \text{Student B teaches what Student A wants to learn}$$

Students can browse cataloged skills, register custom offerings, send direct exchange proposals, and manage incoming invitations with real-time status updates (`PENDING`, `ACCEPTED`, `REJECTED`).

---

## 🏛️ 2. Core Java OOP Concepts Mapping

This project is built to demonstrate enterprise-grade **Object-Oriented Programming (OOP)** patterns and best practices for academic evaluation:

| OOP Concept | Project Implementation | File Reference |
| :--- | :--- | :--- |
| **Abstraction** | Abstract base class `Person` defining template methods (`getRole()`, `getSummary()`). | [`Person.java`](file:///src/main/java/com/skillchain/model/Person.java) |
| **Inheritance** | `User` inherits identity attributes and common methods from `Person`. | [`User.java`](file:///src/main/java/com/skillchain/model/User.java) |
| **Encapsulation** | Strict private fields with validated public getters/setters throughout all domain models. | [`User.java`](file:///src/main/java/com/skillchain/model/User.java), [`Skill.java`](file:///src/main/java/com/skillchain/model/Skill.java) |
| **Polymorphism (Runtime / Overriding)** | `User` overrides `getRole()` and `getSummary()` defined in abstract `Person`. | [`User.java`](file:///src/main/java/com/skillchain/model/User.java) |
| **Polymorphism (Compile-time / Overloading)** | Overloaded methods in DAO and Service layers (`search(keyword)` vs `search(keyword, category)`). | [`SkillDao.java`](file:///src/main/java/com/skillchain/dao/SkillDao.java) |
| **Interfaces & Generics** | Generic interface `Searchable<T>` implemented by data access contracts. | [`Searchable.java`](file:///src/main/java/com/skillchain/dao/Searchable.java) |
| **Composition** | `UserSkill` and `LearningRequest` compose relationships between `User` and `Skill` entities. | [`UserSkill.java`](file:///src/main/java/com/skillchain/model/UserSkill.java) |
| **Custom Exception Hierarchy** | Application-specific checked and unchecked exceptions for domain errors. | [`DuplicateEmailException.java`](file:///src/main/java/com/skillchain/util/DuplicateEmailException.java)<br>[`DuplicateSkillException.java`](file:///src/main/java/com/skillchain/util/DuplicateSkillException.java)<br>[`InvalidLoginException.java`](file:///src/main/java/com/skillchain/util/InvalidLoginException.java) |
| **Data Structures & Recursion** | Multi-branch N-ary Category Tree traversed recursively to generate academic taxonomy views. | [`SkillTreeUtil.java`](file:///src/main/java/com/skillchain/util/SkillTreeUtil.java) |
| **Security & Hashing** | Cryptographically salted SHA-256 password hashing for safe credential storage. | [`PasswordUtil.java`](file:///src/main/java/com/skillchain/util/PasswordUtil.java) |

---

## 🗄️ 3. Database Architecture (MySQL)

The database schema (`skill_chain_db`) contains 4 normalized relational tables with referential integrity constraints:

```text
  +------------------+         +----------------------+         +------------------+
  |      users       | 1     * |      user_skills     | *     1 |      skills      |
  |------------------|---------|----------------------|---------|------------------|
  | PK user_id       |         | PK user_skill_id     |         | PK skill_id      |
  |    name          |         | FK user_id           |         |    skill_name    |
  |    email (UNIQUE)|         | FK skill_id          |         |    category      |
  |    password_hash |         |    skill_type        |         +------------------+
  |    salt          |         +----------------------+                   |
  +------------------+                                                    |
           |                                                              |
           | 1                                                            | 1
           |                                                              |
           | *                                                            | *
  +--------------------------------------------------------------------------------+
  |                               learning_requests                                |
  |--------------------------------------------------------------------------------|
  | PK request_id                                                                  |
  | FK sender_id          -> users(user_id)                                        |
  | FK receiver_id        -> users(user_id)                                        |
  | FK offered_skill_id   -> skills(skill_id)                                      |
  | FK requested_skill_id -> skills(skill_id)                                      |
  |    status             ENUM('PENDING', 'ACCEPTED', 'REJECTED')                  |
  |    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP                      |
  +--------------------------------------------------------------------------------+
```

---

## ⚡ 4. How to Run the Application

### Prerequisites
* **Java Development Kit (JDK 17 or higher)**
* **MySQL Server 8.0+** running on `localhost:3306`

### Step 1: Initialize Database
Open MySQL Workbench and execute [`schema.sql`](file:///schema.sql). This creates the database, all four tables, and populates initial demo accounts and skills.

### Step 2: Configure Credentials
Ensure [`config.properties`](file:///config.properties) matches your local MySQL setup:
```properties
db.url=jdbc:mysql://localhost:3306/skill_chain_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=root
```

### Step 3: Compile and Launch
* To recompile sources: Double-click **[`build.bat`](file:///build.bat)**
* To run the desktop application: Double-click **[`run.bat`](file:///run.bat)**

---

## 👥 5. Demo Accounts for Testing & Viva Presentation

All demo student accounts are configured with the password: **`Password123!`**

| Student Name | Email Address | Teaches | Wants to Learn | Immediate Mutual Match |
| :--- | :--- | :--- | :--- | :--- |
| **Alice Johnson** | `alice@university.edu` | Java, C++ | Python, UI/UX | **Bob Smith** (Java ⇄ Python) |
| **Bob Smith** | `bob@university.edu` | Python, JavaScript | Java, Graphic Design | **Alice Johnson** (Python ⇄ Java) |
| **Charlie Davis** | `charlie@university.edu` | React, Node.js | SQL | **Diana Prince** (React ⇄ SQL) |
| **Diana Prince** | `diana@university.edu` | SQL, ML | React | **Charlie Davis** (SQL ⇄ React) |

---

## 🖥️ 6. Screen Features Walkthrough

1. **Login & Register:**
   * High-contrast glassmorphic login screen.
   * Toggle between Student Sign-In and New Student Registration.
   * Auto-validates institutional email formats (`@university.edu` or valid domains).
2. **Dashboard:**
   * 4 dynamic elevated metric cards: *Skills I Teach*, *Skills I Learn*, *Reciprocal Matches*, and *Exchange Requests*.
3. **My Skills Tab:**
   * Add skills from the curated 10-skill catalog or input custom skills.
   * Tag each skill as `TEACH` (Offering) or `LEARN` (Seeking).
   * Filter portfolio by type or keyword.
   * Explore the recursive category taxonomy via the *"View Skill Tree"* dialog.
4. **Reciprocal Matches Tab:**
   * Automated dual-conditional match calculation.
   * Click *"Send Swap Invitation ✉"* to propose a trade.
5. **Learning Requests Tab:**
   * Accept or decline incoming swap proposals.
   * Monitor the status of outgoing proposals.
