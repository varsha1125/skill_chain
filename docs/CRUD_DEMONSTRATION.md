# CRUD Demonstration Guide

This guide maps the project’s database operations to visible application actions for the evaluation.

| Operation | UI action | Service/DAO path | SQL evidence |
|---|---|---|---|
| Create user | Complete registration form | `UserService.register` → `UserDaoImpl.save` | `INSERT INTO users` |
| Read user | Enter login credentials | `UserService.login` → `UserDaoImpl.findByEmail` | `SELECT ... FROM users` |
| Create skill mapping | Add a catalogue or custom skill | `SkillService.addUserSkill` → `SkillDaoImpl.addUserSkill` | `INSERT INTO user_skills` |
| Read skills | Open My Skills tab | `SkillService.getUserSkills` → `SkillDaoImpl.getUserSkills` | `SELECT ... JOIN skills` |
| Create request | Send swap invitation | `RequestService.sendRequest` → `LearningRequestDaoImpl.save` | `INSERT INTO learning_requests` |
| Read requests | Open incoming/outgoing requests | `RequestService.getIncomingRequests` / `getOutgoingRequests` | `SELECT ... JOIN users/skills` |
| Update request | Accept or reject invitation | `RequestService.acceptRequest` / `rejectRequest` | `UPDATE learning_requests SET status` |
| Delete skill mapping | Select a skill and choose Remove | `SkillService.removeUserSkill` → `SkillDaoImpl.removeUserSkill` | `DELETE FROM user_skills` |

## Viva sequence

1. Log in as `alice@university.edu` using the seeded password from the README.
2. Show Alice’s teaching and learning skills.
3. Show the reciprocal match with Bob.
4. Send a swap invitation and show the new pending request.
5. Log in as Bob and accept the request.
6. Return to Alice and show the accepted status/partnership.
7. Add and then remove a test skill, showing the corresponding database rows.

## Evidence to capture

For each action, capture both the application result and the relevant MySQL query/result. Record the date, account used, operation, expected result, and actual result in the test plan.
