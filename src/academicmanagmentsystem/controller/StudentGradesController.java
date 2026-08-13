package academicmanagmentsystem.controller;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class StudentGradesController {

    @FXML
    private Label studentInfoLabel;

    @FXML
    private Label totalCreditsLabel;

    @FXML
    private Label gpaLabel;

    @FXML
    private TableView<GradeDisplay> gradesTable;

    @FXML
    private TableColumn<GradeDisplay, String> courseCodeColumn;

    @FXML
    private TableColumn<GradeDisplay, String> courseNameColumn;

    @FXML
    private TableColumn<GradeDisplay, String> gradeValueColumn;

    @FXML
    private TableColumn<GradeDisplay, Double> gradeNumericColumn;

    @FXML
    private TableColumn<GradeDisplay, String> gradeDateColumn;

    private Student student;
    private CourseOperations courseOps;
    private EnrollmentOperations enrollmentOps;
    private GradeOperations gradeOps;

    public StudentGradesController() {
        courseOps = new CourseOperations();
        enrollmentOps = new EnrollmentOperations();
        gradeOps = new GradeOperations();
    }

    public void setStudent(Student student) {
        this.student = student;
        initialize();
    }

    private void initialize() {
        // Set student info
        studentInfoLabel.setText("Student ID: " + student.getStudentId() + " | Major: " + student.getMajor());

        // Initialize table columns
        courseCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        gradeValueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));
        gradeNumericColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getNumericGrade()).asObject());
        gradeDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGradeDate()));

        // Load grades
        loadGrades();
        updateStatistics();
    }

    private void loadGrades() {
        List<Grade> grades = gradeOps.getGradesByStudent(student.getUserId());
        ObservableList<GradeDisplay> gradeDisplayList = FXCollections.observableArrayList();

        for (Grade grade : grades) {
            // Get course information for this grade
            Course course = courseOps.getCourseById(getCourseIdFromEnrollment(grade.getEnrollmentId()));

            if (course != null) {
                GradeDisplay gd = new GradeDisplay(
                    course.getCourseCode(),
                    course.getCourseName(),
                    grade.getGrade(),
                    grade.getNumericGrade(),
                    grade.getGradeDate() != null ? grade.getGradeDate().toString() : "N/A"
                );
                gradeDisplayList.add(gd);
            }
        }

        gradesTable.setItems(gradeDisplayList);
    }

    private int getCourseIdFromEnrollment(int enrollmentId) {
        List<Enrollment> enrollments = enrollmentOps.getStudentCourses(student.getUserId());

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getEnrollmentId() == enrollmentId) {
                return enrollment.getCourseId();
            }
        }

        return 0;
    }

    private void updateStatistics() {
        totalCreditsLabel.setText(String.valueOf(student.getTotalCredits()));
        double gpa = gradeOps.calculateGPA(student.getUserId());
        gpaLabel.setText(String.format("%.2f", gpa));
    }

    @FXML
    private void refreshGrades() {
        loadGrades();
        updateStatistics();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) gradesTable.getScene().getWindow();
        stage.close();
    }

    // Inner class for grade display
    public static class GradeDisplay {
        private final String courseCode;
        private final String courseName;
        private final String grade;
        private final double numericGrade;
        private final String gradeDate;

        public GradeDisplay(String courseCode, String courseName, String grade, double numericGrade, String gradeDate) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.grade = grade;
            this.numericGrade = numericGrade;
            this.gradeDate = gradeDate;
        }

        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getGrade() { return grade; }
        public double getNumericGrade() { return numericGrade; }
        public String getGradeDate() { return gradeDate; }
    }
}
