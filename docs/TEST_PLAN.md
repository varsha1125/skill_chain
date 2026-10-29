# Verification and Test Plan

## Test environment

- JDK 17 or later
- MySQL 8.0 or later
- Database created using `schema.sql`
- Local `config.properties` created from `config.properties.example`

## Automated smoke test

The database-independent smoke test checks domain validation, inheritance, overriding, enum parsing, password hashing, and recursive tree traversal.

```bash
rm -rf /tmp/skill-chain-test-classes
mkdir -p /tmp/skill-chain-test-classes
javac --release 17 -d /tmp/skill-chain-test-classes \
  $(find src/main/java -name '*.java') \
  src/test/java/com/skillchain/verification/CoreOopVerification.java
java -ea -cp /tmp/skill-chain-test-classes com.skillchain.verification.CoreOopVerification
```

Expected output:

```text
Core OOP verification passed.
```

## Functional test cases

| ID | Test | Expected result | Evidence/status |
|---|---|---|---|
| T01 | Execute `schema.sql` | Database and four tables are created | `[TO BE COMPLETED]` |
| T02 | Register a new valid student | New row appears in `users` | `[TO BE COMPLETED]` |
| T03 | Register with an existing email | Duplicate-email message is displayed | `[TO BE COMPLETED]` |
| T04 | Login with valid credentials | Dashboard opens | `[TO BE COMPLETED]` |
| T05 | Login with invalid credentials | Controlled login error is displayed | `[TO BE COMPLETED]` |
| T06 | Add catalogue skill as TEACH | Row appears in `user_skills` | `[TO BE COMPLETED]` |
| T07 | Add duplicate skill/type | Duplicate-skill message is displayed | `[TO BE COMPLETED]` |
| T08 | Search by keyword/category | Matching skills are listed | `[TO BE COMPLETED]` |
| T09 | Display reciprocal matches | Mutual skill overlap is listed | `[TO BE COMPLETED]` |
| T10 | Send exchange request | Pending row appears in `learning_requests` | `[TO BE COMPLETED]` |
| T11 | Accept request | Status changes to `ACCEPTED` | `[TO BE COMPLETED]` |
| T12 | Reject request | Status changes to `REJECTED` | `[TO BE COMPLETED]` |
| T13 | Remove own skill | `user_skills` row is deleted | `[TO BE COMPLETED]` |
| T14 | Attempt self-request | Validation error is displayed | `[TO BE COMPLETED]` |
| T15 | Stop MySQL and refresh | Visible database error is displayed | `[TO BE COMPLETED]` |

## Test result recording format

For every test marked complete, record:

- Date and tester name
- Input data/account
- Expected result
- Actual result
- Pass/fail decision
- Screenshot filename or SQL output
