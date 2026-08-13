package academicmanagmentsystem.controller;

import academicmanagmentsystem.fileoperations.TextFileExporter;
import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label infoLabel;

    @FXML
    private Label totalCreditsLabel;

    @FXML
    private Label gpaLabel;

    @FXML
    private TableView<Course> coursesTable;

    @FXML
    private TableColumn<Course, String> courseCodeColumn;

    @FXML
    private TableColumn<Course, String> courseNameColumn;

    @FXML
    private TableColumn<Course, Integer> creditHoursColumn;

    @FXML
    private TableColumn<Course, String> semesterColumn;

    @FXML
    private TableColumn<Course, String> statusColumn;


    private Student student;
    private CourseOperations courseOps;
    private EnrollmentOperations enrollmentOps;
    private GradeOperations gradeOps;
    private TextFileExporter fileExporter;

    public StudentDashboardController() {
        courseOps = new CourseOperations();
        enrollmentOps = new EnrollmentOperations();
        gradeOps = new GradeOperations();
        fileExporter = new TextFileExporter();
    }

    public void setStudent(Student student) {
        this.student = student;
        initializeDashboard();
    }

    private void initializeDashboard() {
        // Set welcome message
        welcomeLabel.setText("Welcome, " + student.getFirstName() + " " + student.getLastName());
        infoLabel.setText("Student ID: " + student.getStudentId() + " | Major: " + student.getMajor() + " | GPA: " + String.format("%.2f", student.getGpa()));

        // Initialize table columns
        courseCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        creditHoursColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCreditHours()).asObject());
        semesterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSemester() + " " + cellData.getValue().getYear()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Active"));

        // Load data
        loadEnrolledCourses();
        updateStatistics();
    }

    private void loadEnrolledCourses() {
        List<Enrollment> enrollments = enrollmentOps.getStudentCourses(student.getUserId());
        ObservableList<Course> courseList = FXCollections.observableArrayList();

        for (Enrollment enrollment : enrollments) {
            Course course = courseOps.getCourseById(enrollment.getCourseId());
            if (course != null) {
                courseList.add(course);
            }
        }

        coursesTable.setItems(courseList);
    }


    private void updateStatistics() {
        totalCreditsLabel.setText(String.valueOf(student.getTotalCredits()));
        double gpa = gradeOps.calculateGPA(student.getUserId());
        gpaLabel.setText(String.format("%.2f", gpa));
    }

    @FXML
    private void showMyCourses() {
        loadEnrolledCourses();
    }

    @FXML
    private void showCourseRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academicmanagmentsystem/view/CourseRegistration.fxml"));
            Parent root = loader.load();

            CourseRegistrationController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Course Registration");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Error opening course registration: " + e.getMessage());
        }
    }

    @FXML
    private void showMyGrades() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academicmanagmentsystem/view/StudentGrades.fxml"));
            Parent root = loader.load();

            StudentGradesController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("My Grades");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Error opening grades view: " + e.getMessage());
        }
    }

    @FXML
    private void exportTranscript() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transcript");
        fileChooser.setInitialFileName("transcript_" + student.getStudentId() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            boolean success = fileExporter.exportTranscript(student.getUserId(), file.getAbsolutePath());
            if (success) {
                showInfo("Transcript exported successfully to:\n" + file.getAbsolutePath());
            } else {
                showError("Failed to export transcript");
            }
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academicmanagmentsystem/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Academic Management System - Login");
            stage.show();

        } catch (Exception e) {
            showError("Error logging out: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
