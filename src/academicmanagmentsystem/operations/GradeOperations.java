package academicmanagmentsystem.operations;

import academicmanagmentsystem.model.Grade;
import java.sql.*;
import java.util.ArrayList;

public class GradeOperations {

    public boolean addGrade(Grade grade) {
        String sql = "INSERT INTO GRADES (GRADE_ID, ENROLLMENT_ID, GRADE, NUMERIC_GRADE, GRADE_DATE) VALUES (GRADE_SEQ.NEXTVAL, ?, ?, ?, SYSDATE)";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, grade.getEnrollmentId());
            pstmt.setString(2, grade.getGrade());
            pstmt.setDouble(3, grade.getNumericGrade());

            int rowsInserted = pstmt.executeUpdate();
            pstmt.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateGrade(Grade grade) {
        String sql = "UPDATE GRADES SET GRADE=?, NUMERIC_GRADE=?, GRADE_DATE=SYSDATE WHERE GRADE_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, grade.getGrade());
            pstmt.setDouble(2, grade.getNumericGrade());
            pstmt.setInt(3, grade.getGradeId());

            int rowsUpdated = pstmt.executeUpdate();
            pstmt.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteGrade(int gradeId) {
        String sql = "DELETE FROM GRADES WHERE GRADE_ID=?";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gradeId);

            int rowsDeleted = pstmt.executeUpdate();
            pstmt.close();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<Grade> getGradesByStudent(int studentId) {
        ArrayList<Grade> grades = new ArrayList<>();
        String sql = "SELECT G.* FROM GRADES G JOIN ENROLLMENTS E ON G.ENROLLMENT_ID = E.ENROLLMENT_ID WHERE E.STUDENT_ID=? ORDER BY G.GRADE_DATE DESC";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Grade grade = createGradeFromResultSet(rs);
                grades.add(grade);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
        }

        return grades;
    }

    public ArrayList<Grade> getGradesByCourse(int courseId) {
        ArrayList<Grade> grades = new ArrayList<>();
        String sql = "SELECT G.* FROM GRADES G JOIN ENROLLMENTS E ON G.ENROLLMENT_ID = E.ENROLLMENT_ID WHERE E.COURSE_ID=? ORDER BY E.STUDENT_ID";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Grade grade = createGradeFromResultSet(rs);
                grades.add(grade);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
        }

        return grades;
    }

    public double calculateGPA(int studentId) {
        String sql = "SELECT AVG(G.NUMERIC_GRADE) AS GPA FROM GRADES G JOIN ENROLLMENTS E ON G.ENROLLMENT_ID = E.ENROLLMENT_ID WHERE E.STUDENT_ID=? AND E.STATUS='COMPLETED'";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double gpa = rs.getDouble("GPA");
                rs.close();
                pstmt.close();
                return Math.round(gpa * 100.0) / 100.0;
            }

            rs.close();
            pstmt.close();
            return 0.0;
        } catch (SQLException e) {
            return 0.0;
        }
    }

    public double getNumericGrade(String letterGrade) {
        switch (letterGrade) {
            case "A":
                return 4.00;
            case "B+":
                return 3.33;
            case "B":
                return 3.00;
            case "C+":
                return 2.33;
            case "C":
                return 2.00;
            case "D+":
                return 1.33;
            case "D":
                return 1.00;
            case "F":
                return 0.00;
            default:
                return 0.00;
        }
    }

    private Grade createGradeFromResultSet(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setGradeId(rs.getInt("GRADE_ID"));
        grade.setEnrollmentId(rs.getInt("ENROLLMENT_ID"));
        grade.setGrade(rs.getString("GRADE"));
        grade.setNumericGrade(rs.getDouble("NUMERIC_GRADE"));
        grade.setGradeDate(rs.getDate("GRADE_DATE"));
        return grade;
    }
}
