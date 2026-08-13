package academicmanagmentsystem.model;

import java.io.Serializable;
import java.util.Date;

public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;

    private int gradeId;
    private int enrollmentId;
    private String grade;
    private double numericGrade;
    private Date gradeDate;

    public Grade() {
        this.gradeDate = new Date();
    }

    public Grade(int gradeId, int enrollmentId, String grade, double numericGrade, Date gradeDate) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.grade = grade;
        this.numericGrade = numericGrade;
        this.gradeDate = gradeDate;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getNumericGrade() {
        return numericGrade;
    }

    public void setNumericGrade(double numericGrade) {
        this.numericGrade = numericGrade;
    }

    public Date getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(Date gradeDate) {
        this.gradeDate = gradeDate;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", enrollmentId=" + enrollmentId +
                ", grade='" + grade + '\'' +
                ", numericGrade=" + numericGrade +
                ", gradeDate=" + gradeDate +
                '}';
    }
}
