package academicmanagmentsystem.model;

import java.util.Date;

public class Student extends User {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String major;
    private double gpa;
    private int enrollmentYear;
    private int totalCredits;

    public Student() {
        
    }

    public Student(int userId, String username, String password, String role, String firstName, String lastName, String email, Date createdDate, String studentId, String major, double gpa, int enrollmentYear, int totalCredits) {
        super(userId, username, password, role, firstName, lastName, email, createdDate);
        this.studentId = studentId;
        this.major = major;
        this.gpa = gpa;
        this.enrollmentYear = enrollmentYear;
        this.totalCredits = totalCredits;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    @Override
    public String getDashboardInfo() {
        return "Student Dashboard - " + getFirstName() + " " + getLastName() +
               " | Major: " + major + " | GPA: " + gpa + " | Credits: " + totalCredits;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", major='" + major + '\'' +
                ", gpa=" + gpa +
                ", enrollmentYear=" + enrollmentYear +
                ", totalCredits=" + totalCredits +
                ", " + super.toString() +
                '}';
    }
}
