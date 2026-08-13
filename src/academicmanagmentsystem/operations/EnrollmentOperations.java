package academicmanagmentsystem.operations;

import academicmanagmentsystem.model.Enrollment;
import java.sql.*;
import java.util.ArrayList;

public class EnrollmentOperations {

    public boolean enrollStudent(int studentId, int courseId) {
        if (checkDuplicateEnrollment(studentId, courseId)) {
            return false;
        }

        if (!checkEnrollmentCapacity(courseId)) {
            return false;
        }

        String sql = "INSERT INTO ENROLLMENTS (ENROLLMENT_ID, STUDENT_ID, COURSE_ID, ENROLLMENT_DATE, STATUS) VALUES (ENROLLMENT_SEQ.NEXTVAL, ?, ?, SYSDATE, 'ENROLLED')";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);

            int rowsInserted = pstmt.executeUpdate();
            pstmt.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean dropEnrollment(int enrollmentId) {
        String sql = "UPDATE ENROLLMENTS SET STATUS='DROPPED' WHERE ENROLLMENT_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, enrollmentId);

            int rowsUpdated = pstmt.executeUpdate();
            pstmt.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<Enrollment> getStudentCourses(int studentId) {
        ArrayList<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM ENROLLMENTS WHERE STUDENT_ID=? ORDER BY ENROLLMENT_DATE DESC";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Enrollment enrollment = createEnrollmentFromResultSet(rs);
                enrollments.add(enrollment);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
        }

        return enrollments;
    }

    public ArrayList<Enrollment> getCourseStudents(int courseId) {
        ArrayList<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM ENROLLMENTS WHERE COURSE_ID=? ORDER BY STUDENT_ID";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Enrollment enrollment = createEnrollmentFromResultSet(rs);
                enrollments.add(enrollment);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
        }

        return enrollments;
    }

    public boolean checkEnrollmentCapacity(int courseId) {
        String sql = "SELECT COUNT(*) AS ENROLLED_COUNT, MAX_CAPACITY FROM ENROLLMENTS E JOIN COURSES C ON E.COURSE_ID = C.COURSE_ID WHERE E.COURSE_ID=? AND E.STATUS='ENROLLED' GROUP BY MAX_CAPACITY";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int enrolledCount = rs.getInt("ENROLLED_COUNT");
                int maxCapacity = rs.getInt("MAX_CAPACITY");
                rs.close();
                pstmt.close();
                return enrolledCount < maxCapacity;
            }

            rs.close();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean checkDuplicateEnrollment(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) AS COUNT FROM ENROLLMENTS WHERE STUDENT_ID=? AND COURSE_ID=? AND STATUS IN ('ENROLLED', 'COMPLETED')";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("COUNT");
                rs.close();
                pstmt.close();
                return count > 0;
            }

            rs.close();
            pstmt.close();
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    private Enrollment createEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("ENROLLMENT_ID"));
        enrollment.setStudentId(rs.getInt("STUDENT_ID"));
        enrollment.setCourseId(rs.getInt("COURSE_ID"));
        enrollment.setEnrollmentDate(rs.getDate("ENROLLMENT_DATE"));
        enrollment.setStatus(rs.getString("STATUS"));
        return enrollment;
    }
}
