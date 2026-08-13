package academicmanagmentsystem.controller;

import academicmanagmentsystem.fileoperations.TextFileExporter;
import academicmanagmentsystem.fileoperations.TextFileImporter;
import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label infoLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label totalStudentsLabel;

    @FXML
    private Label totalInstructorsLabel;

    @FXML
    private Label totalAdminsLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label totalEnrollmentsLabel;

    private Admin admin;
    private UserOperations userOps;
    private CourseOperations courseOps;
    private EnrollmentOperations enrollmentOps;
    private TextFileExporter fileExporter;
    private TextFileImporter fileImporter;

    public AdminDashboardController() {
        userOps = new UserOperations();
        courseOps = new CourseOperations();
        enrollmentOps = new EnrollmentOperations();
        fileExporter = new TextFileExporter();
        fileImporter = new TextFileImporter();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        initializeDashboard();
    }

    private void initializeDashboard() {
        welcomeLabel.setText("Welcome, " + admin.getFirstName() + " " + admin.getLastName());
        infoLabel.setText("Admin ID: " + admin.getAdminId() + " | Access Level: " + admin.getAccessLevel());

        loadStatistics();
    }

    private void loadStatistics() {
        List<User> allUsers = userOps.getAllUsers();

        int totalUsers = allUsers.size();
        int students = 0;
        int instructors = 0;
        int admins = 0;

        for (User user : allUsers) {
            if (user instanceof Student) {
                students++;
            } else if (user instanceof Instructor) {
                instructors++;
            } else if (user instanceof Admin) {
                admins++;
            }
        }

        totalUsersLabel.setText(String.valueOf(totalUsers));
        totalStudentsLabel.setText(String.valueOf(students));
        totalInstructorsLabel.setText(String.valueOf(instructors));
        totalAdminsLabel.setText(String.valueOf(admins));

        // Get total courses
        List<Course> courses = courseOps.getAllCourses();
        totalCoursesLabel.setText(String.valueOf(courses.size()));

        // Count total enrollments
        int totalEnrollments = 0;
        for (Course course : courses) {
            totalEnrollments += courseOps.getCourseEnrollments(course.getCourseId());
        }
        totalEnrollmentsLabel.setText(String.valueOf(totalEnrollments));
    }

    @FXML
    private void showUserManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academicmanagmentsystem/view/UserManagement.fxml"));
            Parent root = loader.load();

            UserManagementController controller = loader.getController();
            controller.setAdmin(admin);

            Stage stage = new Stage();
            stage.setTitle("User Management");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Error opening user management: " + e.getMessage());
        }
    }

    @FXML
    private void showCourseManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academicmanagmentsystem/view/AdminCourseManagement.fxml"));
            Parent root = loader.load();

            AdminCourseManagementController controller = loader.getController();
            controller.setAdmin(admin);

            Stage stage = new Stage();
            stage.setTitle("Course Management");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadStatistics()); // Refresh statistics when window closes
            stage.show();

        } catch (Exception e) {
            showError("Error opening course management: " + e.getMessage());
        }
    }

    @FXML
    private void showReports() {
        generateReport();
    }

    @FXML
    private void showImportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Student List");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            int count = fileImporter.importStudentList(file.getAbsolutePath());
            if (count > 0) {
                showInfo("Successfully imported " + count + " students");
                loadStatistics();
            } else {
                String error = fileImporter.getLastError();
                showError("Failed to import students:\n" + (error.isEmpty() ? "Unknown error" : error));
            }
        }
    }

    @FXML
    private void exportSummary() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Academic Summary");
        fileChooser.setInitialFileName("academic_summary.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            boolean success = fileExporter.exportAcademicSummary(file.getAbsolutePath());
            if (success) {
                showInfo("Academic summary exported successfully to:\n" + file.getAbsolutePath());
            } else {
                showError("Failed to export academic summary");
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

    @FXML
    private void generateReport() {
        loadStatistics();

        // Open file chooser to save the report
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save System Report");
        fileChooser.setInitialFileName("system_report_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                StringBuilder report = new StringBuilder();
                report.append("ACADEMIC MANAGEMENT SYSTEM - DETAILED REPORT\n");
                report.append("===========================================\n\n");

                // User Statistics
                report.append("USER STATISTICS:\n");
                report.append("----------------\n");
                report.append("Total Users: ").append(totalUsersLabel.getText()).append("\n");
                report.append("  - Students: ").append(totalStudentsLabel.getText()).append("\n");
                report.append("  - Instructors: ").append(totalInstructorsLabel.getText()).append("\n");
                report.append("  - Administrators: ").append(totalAdminsLabel.getText()).append("\n\n");

                // Course Statistics
                report.append("COURSE STATISTICS:\n");
                report.append("------------------\n");
                report.append("Total Courses: ").append(totalCoursesLabel.getText()).append("\n");
                report.append("Total Active Enrollments: ").append(totalEnrollmentsLabel.getText()).append("\n\n");

                // Detailed Course List
                List<Course> courses = courseOps.getAllCourses();
                report.append("COURSE DETAILS:\n");
                report.append("---------------\n");
                for (Course course : courses) {
                    int enrollments = courseOps.getCourseEnrollments(course.getCourseId());
                    report.append(String.format("%-10s | %-40s | %d/%d enrolled\n",
                        course.getCourseCode(),
                        course.getCourseName(),
                        enrollments,
                        course.getMaxCapacity()));
                }

                report.append("\n");
                report.append("Report Generated: ").append(new java.util.Date().toString()).append("\n");

                // Write to file
                java.nio.file.Files.write(file.toPath(), report.toString().getBytes());

                showInfo("Report successfully saved to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showError("Failed to save report: " + e.getMessage());
            }
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
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
