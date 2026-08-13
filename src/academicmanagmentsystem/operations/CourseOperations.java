package academicmanagmentsystem.operations;

import academicmanagmentsystem.model.Course;
import java.sql.*;
import java.util.ArrayList;

public class CourseOperations {

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO COURSES (COURSE_ID, COURSE_CODE, COURSE_NAME, CREDIT_HOURS, INSTRUCTOR_ID, SEMESTER, YEAR, MAX_CAPACITY) VALUES (COURSE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCreditHours());

            if (course.getInstructorId() == 0) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, course.getInstructorId());
            }

            pstmt.setString(5, course.getSemester());
            pstmt.setInt(6, course.getYear());
            pstmt.setInt(7, course.getMaxCapacity());

            int rowsInserted = pstmt.executeUpdate();
            pstmt.close();
            return rowsInserted > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: Course code already exists. Please use a different course code.");
            return false;
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE COURSES SET COURSE_CODE=?, COURSE_NAME=?, CREDIT_HOURS=?, INSTRUCTOR_ID=?, SEMESTER=?, YEAR=?, MAX_CAPACITY=? WHERE COURSE_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCreditHours());

            if (course.getInstructorId() == 0) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, course.getInstructorId());
            }

            pstmt.setString(5, course.getSemester());
            pstmt.setInt(6, course.getYear());
            pstmt.setInt(7, course.getMaxCapacity());
            pstmt.setInt(8, course.getCourseId());

            int rowsUpdated = pstmt.executeUpdate();
            pstmt.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM COURSES WHERE COURSE_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);

            int rowsDeleted = pstmt.executeUpdate();
            pstmt.close();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM COURSES WHERE COURSE_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                rs.close();
                pstmt.close();
                return course;
            }

            rs.close();
            pstmt.close();
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM COURSES ORDER BY COURSE_ID";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                courses.add(course);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
        }

        return courses;
    }

    public ArrayList<Course> getCoursesByInstructor(int instructorId) {
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM COURSES WHERE INSTRUCTOR_ID=? ORDER BY COURSE_ID";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, instructorId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                courses.add(course);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
        }

        return courses;
    }

    public ArrayList<Course> getAvailableCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT C.* FROM COURSES C WHERE (SELECT COUNT(*) FROM ENROLLMENTS E WHERE E.COURSE_ID = C.COURSE_ID AND E.STATUS = 'ENROLLED') < C.MAX_CAPACITY ORDER BY C.COURSE_ID";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                courses.add(course);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
        }

        return courses;
    }

    public int getCourseEnrollments(int courseId) {
        String sql = "SELECT COUNT(*) AS ENROLLMENT_COUNT FROM ENROLLMENTS WHERE COURSE_ID=? AND STATUS='ENROLLED'";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("ENROLLMENT_COUNT");
                rs.close();
                pstmt.close();
                return count;
            }

            rs.close();
            pstmt.close();
            return 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private Course createCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("COURSE_ID"));
        course.setCourseCode(rs.getString("COURSE_CODE"));
        course.setCourseName(rs.getString("COURSE_NAME"));
        course.setCreditHours(rs.getInt("CREDIT_HOURS"));
        course.setInstructorId(rs.getInt("INSTRUCTOR_ID"));
        course.setSemester(rs.getString("SEMESTER"));
        course.setYear(rs.getInt("YEAR"));
        course.setMaxCapacity(rs.getInt("MAX_CAPACITY"));
        return course;
    }
}
