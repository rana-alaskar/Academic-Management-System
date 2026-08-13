package academicmanagmentsystem.operations;

import academicmanagmentsystem.model.*;
import java.sql.*;
import java.util.ArrayList;

public class UserOperations {

    private String lastError = "";

    public String getLastError() {
        return lastError;
    }

    public boolean addUser(User user) {
        lastError = "";
        // Don't include CREATED_DATE in INSERT - let database default handle it
        String sql = "INSERT INTO USERS (USER_ID, USERNAME, PASSWORD, ROLE, FIRST_NAME, LAST_NAME, EMAIL, STUDENT_ID, MAJOR, GPA, ENROLLMENT_YEAR, TOTAL_CREDITS, INSTRUCTOR_ID, DEPARTMENT, OFFICE_LOCATION, PHONE_EXTENSION, SPECIALIZATION, ADMIN_ID, ACCESS_LEVEL) VALUES (USER_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, user.getEmail());

            if (user instanceof Student) {
                Student student = (Student) user;
                pstmt.setString(7, student.getStudentId());
                pstmt.setString(8, student.getMajor());
                pstmt.setDouble(9, student.getGpa());
                pstmt.setInt(10, student.getEnrollmentYear());
                pstmt.setInt(11, student.getTotalCredits());
                pstmt.setNull(12, Types.VARCHAR);
                pstmt.setNull(13, Types.VARCHAR);
                pstmt.setNull(14, Types.VARCHAR);
                pstmt.setNull(15, Types.VARCHAR);
                pstmt.setNull(16, Types.VARCHAR);
                pstmt.setNull(17, Types.VARCHAR);
                pstmt.setNull(18, Types.INTEGER);
            } else if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.DOUBLE);
                pstmt.setNull(10, Types.INTEGER);
                pstmt.setNull(11, Types.INTEGER);
                pstmt.setString(12, instructor.getInstructorId());
                pstmt.setString(13, instructor.getDepartment());
                pstmt.setString(14, instructor.getOfficeLocation());
                pstmt.setString(15, instructor.getPhoneExtension());
                pstmt.setString(16, instructor.getSpecialization());
                pstmt.setNull(17, Types.VARCHAR);
                pstmt.setNull(18, Types.INTEGER);
            } else if (user instanceof Admin) {
                Admin admin = (Admin) user;
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.DOUBLE);
                pstmt.setNull(10, Types.INTEGER);
                pstmt.setNull(11, Types.INTEGER);
                pstmt.setNull(12, Types.VARCHAR);
                pstmt.setNull(13, Types.VARCHAR);
                pstmt.setNull(14, Types.VARCHAR);
                pstmt.setNull(15, Types.VARCHAR);
                pstmt.setNull(16, Types.VARCHAR);
                pstmt.setString(17, admin.getAdminId());
                pstmt.setInt(18, admin.getAccessLevel());
            }

            int rowsInserted = pstmt.executeUpdate();
            pstmt.close();
            return rowsInserted > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            String errorMsg = e.getMessage();

            // Extract constraint name from error message (format: SYS_Cnnnnnn)
            String constraintName = extractConstraintName(errorMsg);

            // Map the constraint to a user-friendly message
            String fieldMessage = identifyConstraintField(constraintName, user);

            if (fieldMessage != null) {
                lastError = fieldMessage;
            } else {
                // Fallback: show a generic message with hint to check uniqueness
                lastError = "A unique constraint was violated. Please ensure username, email, and ID are unique.";
            }

            return false;
        } catch (SQLException e) {
            lastError = "Database error: " + e.getMessage();
            return false;
        }
    }

    public boolean updateUser(User user) {
        lastError = "";
        String sql = "UPDATE USERS SET USERNAME=?, PASSWORD=?, FIRST_NAME=?, LAST_NAME=?, EMAIL=?, STUDENT_ID=?, MAJOR=?, GPA=?, ENROLLMENT_YEAR=?, TOTAL_CREDITS=?, INSTRUCTOR_ID=?, DEPARTMENT=?, OFFICE_LOCATION=?, PHONE_EXTENSION=?, SPECIALIZATION=?, ADMIN_ID=?, ACCESS_LEVEL=? WHERE USER_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getEmail());

            if (user instanceof Student) {
                Student student = (Student) user;
                pstmt.setString(6, student.getStudentId());
                pstmt.setString(7, student.getMajor());
                pstmt.setDouble(8, student.getGpa());
                pstmt.setInt(9, student.getEnrollmentYear());
                pstmt.setInt(10, student.getTotalCredits());
                pstmt.setNull(11, Types.VARCHAR);
                pstmt.setNull(12, Types.VARCHAR);
                pstmt.setNull(13, Types.VARCHAR);
                pstmt.setNull(14, Types.VARCHAR);
                pstmt.setNull(15, Types.VARCHAR);
                pstmt.setNull(16, Types.VARCHAR);
                pstmt.setNull(17, Types.INTEGER);
            } else if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.DOUBLE);
                pstmt.setNull(9, Types.INTEGER);
                pstmt.setNull(10, Types.INTEGER);
                pstmt.setString(11, instructor.getInstructorId());
                pstmt.setString(12, instructor.getDepartment());
                pstmt.setString(13, instructor.getOfficeLocation());
                pstmt.setString(14, instructor.getPhoneExtension());
                pstmt.setString(15, instructor.getSpecialization());
                pstmt.setNull(16, Types.VARCHAR);
                pstmt.setNull(17, Types.INTEGER);
            } else if (user instanceof Admin) {
                Admin admin = (Admin) user;
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.DOUBLE);
                pstmt.setNull(9, Types.INTEGER);
                pstmt.setNull(10, Types.INTEGER);
                pstmt.setNull(11, Types.VARCHAR);
                pstmt.setNull(12, Types.VARCHAR);
                pstmt.setNull(13, Types.VARCHAR);
                pstmt.setNull(14, Types.VARCHAR);
                pstmt.setNull(15, Types.VARCHAR);
                pstmt.setString(16, admin.getAdminId());
                pstmt.setInt(17, admin.getAccessLevel());
            }

            pstmt.setInt(18, user.getUserId());

            int rowsUpdated = pstmt.executeUpdate();
            pstmt.close();
            return rowsUpdated > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            String errorMsg = e.getMessage();

            // Extract constraint name from error message
            String constraintName = extractConstraintName(errorMsg);

            // Map the constraint to a user-friendly message
            String fieldMessage = identifyConstraintField(constraintName, user);

            if (fieldMessage != null) {
                lastError = fieldMessage;
            } else {
                // Fallback: show a generic message with hint to check uniqueness
                lastError = "A unique constraint was violated. Please ensure username, email, and ID are unique.";
            }

            return false;
        } catch (SQLException e) {
            lastError = "Database error: " + e.getMessage();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        lastError = "";
        String sql = "DELETE FROM USERS WHERE USER_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            int rowsDeleted = pstmt.executeUpdate();
            pstmt.close();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            lastError = "Database error: " + e.getMessage();
            return false;
        }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = createUserFromResultSet(rs);
                rs.close();
                pstmt.close();
                return user;
            }

            rs.close();
            pstmt.close();
            return null;
        } catch (SQLException e) {
            lastError = "Database error: " + e.getMessage();
            return null;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM USERS WHERE USER_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = createUserFromResultSet(rs);
                rs.close();
                pstmt.close();
                return user;
            }

            rs.close();
            pstmt.close();
            return null;
        } catch (SQLException e) {
            lastError = "Database error: " + e.getMessage();
            return null;
        }
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS ORDER BY USER_ID";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            lastError = "Database error: " + e.getMessage();
        }

        return users;
    }

    public ArrayList<User> searchUsers(String keyword) {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS WHERE UPPER(USERNAME) LIKE UPPER(?) OR UPPER(FIRST_NAME) LIKE UPPER(?) OR UPPER(LAST_NAME) LIKE UPPER(?) OR UPPER(EMAIL) LIKE UPPER(?) ORDER BY USER_ID";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            lastError = "Database error: " + e.getMessage();
        }

        return users;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("USER_ID");
        String username = rs.getString("USERNAME");
        String password = rs.getString("PASSWORD");
        String role = rs.getString("ROLE");
        String firstName = rs.getString("FIRST_NAME");
        String lastName = rs.getString("LAST_NAME");
        String email = rs.getString("EMAIL");
        Date createdDate = rs.getDate("CREATED_DATE");

        if ("STUDENT".equals(role)) {
            Student student = new Student();
            student.setUserId(userId);
            student.setUsername(username);
            student.setPassword(password);
            student.setRole(role);
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setEmail(email);
            student.setCreatedDate(createdDate);
            student.setStudentId(rs.getString("STUDENT_ID"));
            student.setMajor(rs.getString("MAJOR"));
            student.setGpa(rs.getDouble("GPA"));
            student.setEnrollmentYear(rs.getInt("ENROLLMENT_YEAR"));
            student.setTotalCredits(rs.getInt("TOTAL_CREDITS"));
            return student;
        } else if ("INSTRUCTOR".equals(role)) {
            Instructor instructor = new Instructor();
            instructor.setUserId(userId);
            instructor.setUsername(username);
            instructor.setPassword(password);
            instructor.setRole(role);
            instructor.setFirstName(firstName);
            instructor.setLastName(lastName);
            instructor.setEmail(email);
            instructor.setCreatedDate(createdDate);
            instructor.setInstructorId(rs.getString("INSTRUCTOR_ID"));
            instructor.setDepartment(rs.getString("DEPARTMENT"));
            instructor.setOfficeLocation(rs.getString("OFFICE_LOCATION"));
            instructor.setPhoneExtension(rs.getString("PHONE_EXTENSION"));
            instructor.setSpecialization(rs.getString("SPECIALIZATION"));
            return instructor;
        } else if ("ADMIN".equals(role)) {
            Admin admin = new Admin();
            admin.setUserId(userId);
            admin.setUsername(username);
            admin.setPassword(password);
            admin.setRole(role);
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setEmail(email);
            admin.setCreatedDate(createdDate);
            admin.setAdminId(rs.getString("ADMIN_ID"));
            admin.setAccessLevel(rs.getInt("ACCESS_LEVEL"));
            return admin;
        }

        return null;
    }

    /**
     * Extract constraint name from Oracle error message
     * Format: "ORA-00001: unique constraint (SCHEMA.CONSTRAINT_NAME) violated"
     */
    private String extractConstraintName(String errorMessage) {
        try {
            int start = errorMessage.indexOf('(');
            int end = errorMessage.indexOf(')');
            if (start != -1 && end != -1) {
                String fullName = errorMessage.substring(start + 1, end);
                // Extract just the constraint name (after the dot)
                int dotIndex = fullName.lastIndexOf('.');
                return dotIndex != -1 ? fullName.substring(dotIndex + 1) : fullName;
            }
        } catch (Exception e) {
            // If parsing fails, return empty string
        }
        return "";
    }

    /**
     * Identify which field caused the constraint violation by querying the constraint
     */
    private String identifyConstraintField(String constraintName, User user) {
        if (constraintName == null || constraintName.isEmpty()) {
            return null;
        }

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // Query Oracle data dictionary to find which column this constraint is on
            String sql = "SELECT column_name FROM all_cons_columns WHERE constraint_name = ? AND owner = USER";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, constraintName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String columnName = rs.getString("column_name");
                rs.close();
                pstmt.close();

                // Map column name to user-friendly message
                switch (columnName.toUpperCase()) {
                    case "USERNAME":
                        return "Username '" + user.getUsername() + "' already exists. Please choose a different username.";
                    case "EMAIL":
                        return "Email '" + user.getEmail() + "' already exists. Please use a different email address.";
                    case "STUDENT_ID":
                        if (user instanceof Student) {
                            return "Student ID '" + ((Student) user).getStudentId() + "' already exists. Please use a different student ID.";
                        }
                        return "Student ID already exists. Please use a different student ID.";
                    case "INSTRUCTOR_ID":
                        if (user instanceof Instructor) {
                            return "Instructor ID '" + ((Instructor) user).getInstructorId() + "' already exists. Please use a different instructor ID.";
                        }
                        return "Instructor ID already exists. Please use a different instructor ID.";
                    case "ADMIN_ID":
                        if (user instanceof Admin) {
                            return "Admin ID '" + ((Admin) user).getAdminId() + "' already exists. Please use a different admin ID.";
                        }
                        return "Admin ID already exists. Please use a different admin ID.";
                    default:
                        return "The value for " + columnName + " already exists in the database.";
                }
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            // If we can't query the constraint, fall back to null
        }

        return null;
    }
}
