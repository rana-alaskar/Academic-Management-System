
# Academic Management System

A Java desktop application for managing students, instructors, courses, enrollments, and grades, built with JavaFX and backed by an Oracle Database.

## Overview

Academic Management System is a role-based desktop application that models the core operations of an academic institution: user authentication, course management, student enrollment, and grade tracking. The application is built with JavaFX and FXML for the user interface, uses JDBC with PreparedStatement for all database access, and was developed as a NetBeans project.

The system implements object-oriented design principles through an abstract `User` base class extended by `Student`, `Instructor`, and `Admin` subclasses, each with role-specific attributes and dashboards.

## Key Features

### Student
- Log in and view a personal dashboard with enrolled courses
- Browse and search available courses, and register for courses
- View grades and see calculated GPA
- Export an academic transcript to a text file

### Instructor
- View a dashboard listing assigned courses
- Create and manage courses (add, update, delete, search)
- Enter, update, and clear student grades for a selected course
- Import grades from a text file
- Export a course performance report to a text file

### Administrator
- View a dashboard with system-wide statistics
- Manage users: add, update, delete, and search Students, Instructors, and Admins
- Manage courses across all instructors (add, update, delete, search)
- Import student data from a text file
- Export an academic summary report to a text file
- View basic system reports

### Shared / System Features
- Login screen with credential validation and role-based redirection to the appropriate dashboard
- CRUD operations for users, courses, enrollments, and grades via JDBC `PreparedStatement`
- Text-based file import/export (student lists, grade sheets, transcripts, reports, summaries)
- Binary file export/import of student, course, enrollment, and grade data using Java Serialization (`ObjectOutputStream` / `ObjectInputStream`)
- Form validation (required-field checks and input parsing) on data-entry screens
- Centralized exception handling around database and file operations, with user-facing error/info dialogs

## Technologies Used

- **Language:** Java
- **UI Framework:** JavaFX with FXML
- **Database:** Oracle Database
- **Connectivity:** JDBC (`oracle.jdbc.driver.OracleDriver`, via the Oracle JDBC driver — not included in the repository)
- **IDE / Build:** NetBeans (Ant-based NetBeans project)
- **Design:** Object-Oriented Programming (inheritance, abstraction, encapsulation)
- **I/O:** Java File I/O and Serialization

## Project Structure

```
AcademicManagmentSystem/
├── database/
│   ├── schema.sql              # Oracle table and sequence definitions
│   └── sample_data.sql         # Sample data for the schema
├── nbproject/                  # NetBeans project configuration
├── src/academicmanagmentsystem/
│   ├── Main.java                # Application entry point (JavaFX)
│   ├── controller/              # FXML controllers (Login, dashboards, management screens)
│   ├── model/                   # Domain classes: User (abstract), Student, Instructor,
│   │                             #   Admin, Course, Enrollment, Grade
│   ├── operations/               # DatabaseConnection + CRUD operations
│   │                             #   (UserOperations, CourseOperations,
│   │                             #    EnrollmentOperations, GradeOperations)
│   ├── fileoperations/           # Text and binary (serialization) import/export
│   └── view/                     # FXML layout files
├── test_files/                  # Sample import files and generated export samples
├── sample_grades_import.txt     # Sample grade import file
└── manifest.mf
```

> Note: The Oracle JDBC driver (`ojdbc8.jar`) is not included in this repository and must be obtained and added to the project's classpath separately (see Installation and Setup below).

## Database

The application uses an Oracle Database with four related tables, defined in `database/schema.sql`:

- **USERS** — stores all accounts (Students, Instructors, Admins) in a single table, differentiated by a `ROLE` column, with role-specific columns (e.g. `STUDENT_ID`/`GPA`, `INSTRUCTOR_ID`/`DEPARTMENT`, `ADMIN_ID`/`ACCESS_LEVEL`)
- **COURSES** — course catalog, linked to an instructor via a foreign key
- **ENROLLMENTS** — links students to courses, with a status (`ENROLLED`, `COMPLETED`, `DROPPED`) and a unique constraint preventing duplicate enrollment
- **GRADES** — one grade per enrollment, with letter and numeric grade values

Auto-incrementing IDs are handled with Oracle sequences (`USER_SEQ`, `COURSE_SEQ`, `ENROLLMENT_SEQ`, `GRADE_SEQ`). `database/sample_data.sql` provides sample records for testing.

## Getting Started / Prerequisites

- Java Development Kit (JDK) compatible with the project's configured source/target level
- Apache NetBeans (recommended, since the project uses NetBeans/Ant project files)
- Oracle Database (a local instance such as Oracle Database Express Edition works)
- The Oracle JDBC driver (`ojdbc8.jar`) — not included in this repository; download it separately (e.g. from Oracle's website or Maven Central) and add it to the project's classpath
- JavaFX SDK, if not already bundled with your JDK distribution

## Installation and Setup

1. **Clone the repository**
   ```
   git clone <repository-url>
   cd AcademicManagmentSystem
   ```

2. **Set up the database**
   - Create/start an Oracle Database instance.
   - Run `database/schema.sql` against your database to create the tables and sequences.
   - Optionally run `database/sample_data.sql` to load sample records.

3. **Configure the database connection**
   - Database connection settings are defined in `src/academicmanagmentsystem/operations/DatabaseConnection.java`.
   - Update the connection URL, username, and password to match your local Oracle setup:
     ```java
     private static final String DB_URL = "jdbc:oracle:thin:@<host>:<port>:<SID>";
     private static final String DB_USER = "<your_db_username>";
     private static final String DB_PASSWORD = "<your_db_password>";
     ```

4. **Open the project in NetBeans**
   - Open the project folder as a NetBeans project (it includes `nbproject/` configuration files).
   - Ensure the JavaFX SDK is configured in NetBeans (via the project's library settings) if it is not bundled with your JDK.
   - Obtain the Oracle JDBC driver (`ojdbc8.jar`) separately, as it is not included in this repository, and add it to the project's classpath in NetBeans (Project Properties → Libraries).

## How to Run the Project

1. Open the project in NetBeans.
2. Verify the database is running and reachable with the credentials configured in `DatabaseConnection.java`.
3. Run the project (`Main.java` is the application entry point).
4. The Login screen (`Login.fxml`) is displayed first. Log in with a user account that exists in the `USERS` table to be routed to the corresponding Student, Instructor, or Admin dashboard.

## Technical Highlights

- **Object-Oriented Design:** An abstract `User` class defines shared account fields and behavior; `Student`, `Instructor`, and `Admin` extend it with role-specific data, demonstrating inheritance and polymorphism (each subclass implements `getDashboardInfo()`).
- **Layered Architecture:** Clear separation between FXML views, controllers (UI logic), operations classes (data access), model classes (domain objects), and file-operation classes (import/export).
- **Parameterized Data Access:** All SQL statements use `PreparedStatement` for user, course, enrollment, and grade operations.
- **Serialization:** Student, course, enrollment, and grade data can be exported to and imported from binary files using Java's `ObjectOutputStream`/`ObjectInputStream`, in addition to plain-text import/export.
- **Exception Handling:** Database and file operations are wrapped in try/catch blocks, with errors surfaced to the user through dialogs rather than causing the application to crash.
- **Form Validation:** Data-entry screens (course and user management, grade entry) validate required fields and numeric input before submitting changes to the database.

## Project Purpose

This project was developed to practice building a complete, role-based Java desktop application using JavaFX, JDBC, and Oracle Database within a NetBeans environment. It served as an exercise in applying object-oriented design, layered architecture, database connectivity, file handling, and basic input validation in a single, cohesive system.
