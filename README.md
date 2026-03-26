```markdown
# ReceiptManagementApp

A Java-based receipt management system built as a Software Engineering course project
at the University of Southern Denmark (SDU) during an Erasmus+ exchange semester.

The system implements a multi-role workflow (Submit → Handle → Approve/Reject) with
role-based access control across four user roles. The project emphasises software
engineering best practices with 36 unit tests achieving 99%+ code coverage via JaCoCo.

**Tech Stack:** Java 21, Maven, JUnit Jupiter 5, JaCoCo, Python

---

## System Requirements

- **Java Development Kit (JDK)**: Version 21 or higher
- **Apache Maven**: Version 3.x
- **Python**: Version 3.x (for coverage report filtering)

---

## Project Structure

```
ReceiptManagementApp/
├── src/
│   ├── main/java/project/          # Application source code
│   └── test/java/project/          # Unit tests
├── tools/                           # Utility scripts
│   ├── filter_jacoco.py            # Coverage report filter
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

---

## Building and Running

### Compile the Project
```bash
mvn compile
```

### Run Unit Tests
```bash
mvn test
```

### Run the Application
```bash
mvn exec:java -Dexec.mainClass="project.ReceiptManagementApp"
```

---

## Test Coverage

### Generate JaCoCo Coverage Report
```bash
mvn clean test jacoco:report
```

The HTML report will be generated at: `target/site/jacoco/index.html`

### Generate Filtered Coverage Report
To see only tested classes:
```bash
python3 tools/filter_jacoco.py --jacoco target/site/jacoco/jacoco.xml --srcdir src/test/java/project --out target/site/jacoco/filtered_index.html
```

Open `target/site/jacoco/filtered_index.html` in a browser to view the filtered report.

### Coverage Results
![JaCoCo Coverage](docs/coverage.png)

---

## Default Users

The application comes with pre-configured demo users:

| Username | Password    | Role        |
|----------|-------------|-------------|
| admin    | admin123    | ADMIN       |
| alice    | manager123  | MANAGER     |
| bob      | account123  | ACCOUNTANT  |
| charlie  | sales123    | SALESPERSON |
| diana    | sales123    | SALESPERSON |

---

## Features

- **Role-Based Access Control**: Four distinct roles with different permissions
- **Receipt Workflow**: Submit → Handle → Approve/Reject
- **Dual Document Verification**: Requires both receipt image and bank statement
- **User Management**: Administrators can manage users and reset passwords
- **Comprehensive Testing**: 36 unit tests with 99%+ code coverage

---

## System Workflow

1. **Salesperson** submits a receipt with photo and bank statement
2. **Accountant** reviews and marks the receipt as handled
3. **Manager** approves or rejects the handled receipt
4. **Administrator** has oversight of all users and receipts

---

## Dependencies

All dependencies are managed by Maven and will be downloaded automatically:

- JUnit Jupiter 5.9.3 (Testing)
- JaCoCo 0.8.10 (Code Coverage)

---

## Notes

- The application uses an in-memory data store for simplicity; a persistence layer
  can be added via JPA/Hibernate
- File paths for receipt images and bank statements should be valid on your system
```
