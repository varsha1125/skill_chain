# Verification Results — 29 October 2026

## Completed locally

| Check | Result | Evidence |
|---|---|---|
| Java production compilation | PASS | `javac --release 17` compiled all files under `src/main/java` |
| Core OOP smoke test | PASS | `Core OOP verification passed.` |
| Source formatting check | PASS | `git diff --check` |
| OOP model validation | PASS | Encapsulation, inheritance, overriding, enum parsing, hashing, and recursion smoke-tested |
| Database schema source review | PASS | `schema.sql` contains four tables, primary keys, foreign keys, constraints, and seed data |
| JDBC implementation review | PASS | DAO/service classes use JDBC prepared statements and try-with-resources |
| UI startup | PASS | Swing application launches with the redesigned setup flow |

## Requires the team’s local environment

| Check | Current state | Required action |
|---|---|---|
| MySQL schema execution | NOT RUN HERE | Run `schema.sql` in MySQL Workbench or the MySQL client |
| Login/registration | NOT RUN HERE | Configure `config.properties`, then test with a real MySQL server |
| End-to-end CRUD | NOT RUN HERE | Complete T01–T15 in `TEST_PLAN.md` and record screenshots/results |
| Output screenshots | NOT PROVIDED | Capture real application and database screenshots under `docs/screenshots/` |
| Certificate/declaration | NOT PROVIDED | Fill the team, institution, staff, and signature fields in the report |
| GitHub push | BLOCKED | Authenticate GitHub, then run `git push origin main` |

The uncompleted items above depend on credentials, personal information, or a live database and must not be replaced with invented evidence.
