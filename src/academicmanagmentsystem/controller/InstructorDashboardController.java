package academicmanagmentsystem.controller;

import academicmanagmentsystem.fileoperations.TextFileExporter;
import academicmanagmentsystem.fileoperations.TextFileImporter;
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
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class InstructorDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label infoLabel;

    @FXML
    private TableView<CourseDisplay> coursesTable;

    @FXML
    private TableColumn<CourseDisplay, String> courseCodeColumn;

    @FXML
    private TableColumn<CourseDisplay, String> courseNameColumn;

    @FXML
    private TableColumn<CourseDisplay, Integer> creditHoursColumn;

    @FXML
    private TableColumn<CourseDisplay, String> semesterColumn;

    @FXML
    private TableColumn<CourseDisplay, Integer> enrolledColumn;

    @FXML
    private TableColumn<CourseDisplay, Integer> capacityColumn;

    @FXML
    private TableView<StudentDisplay> studentsTable;

    @FXML
    private TableColumn<StudentDisplay, String> studentIdColumn;

    @FXML
    private TableColumn<StudentDisplay, String> studentNameColumn;

    @FXML
    private TableColumn<StudentDisplay, String> majorColumn;

    @FXML
    private TableColumn<StudentDisplay, String> gradeColumn;

    private Instructor instructor;
    private CourseOperations courseOps;
    private EnrollmentOperations enrollmentOps;
    private UserOperations userOps;
    private GradeOperations gradeOps;
    private TextFileExporter fileExporter;
    private TextFileImporter fileImporter;

    public InstructorDashboardController() {
        courseOps = new CourseOperations();
        enrollmentOps = new EnrollmentOperations();
        userOps = new UserOperations();
        gradeOps = new GradeOperations();
        fileExporter = new TextFileExporter();
        fileImporter = new TextFileImporter();
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
        initializeDashboard();
    }

    private void initializeDashboard() {
        welcomeLabel.setText("Welcome, " + instructor.getFirstName() + " " + instructor.getLastName());
        infoLabel.setText("Department: " + instructor.getDepartment() + " | Specialization: " + instructor.getSpecialization());

        // Initialize course table columns
        courseCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        creditHoursColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCreditHours()).asObject());
        semesterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSemester()));
        enrolledColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEnrolledCount()).asObject());
        capacityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaxCapacity()).asObject());

        // Initialize student table columns
        studentIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentId()));
        studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentName()));
        majorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMajor()));
        gradeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));

        // Add listener to course selection
        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadStudentsForCourse(newSelection.getCourseId());
            }
        });

        loadInstructorCourses();
    }

    private void loadInstructorCourses() {
        List<Course> courses = courseOps.getCoursesByInstructor(instructor.getUserId());
        ObservableList<CourseDisplay> courseDisplayList = FXCollections.observableArrayList();

        for (Course course : courses) {
            int enrolledCount = courseOps.getCourseEnrollments(course.getCourseId());

            CourseDisplay cd = new CourseDisplay(
                course.getCourseId(),
                course.getCourseCode(),
                course.getCourseName(),
                course.getCreditHours(),
                course.getSemester() + " " + course.getYear(),
                enrolledCount,
                course.getMaxCapacity()
            );
            courseDisplayList.add(cd);
        }

        coursesTable.setItems(courseDisplayList);
    }

    private void loadStudentsForCourse(int courseId) {
        List<Enrollment> enrollments = enrollmentOps.getCourseStudents(courseId);
        ObservableList<StudentDisplay> studentDisplayList = FXCollections.observableArrayList();

        for (Enrollment enrollment : enrollments) {
            User user = userOps.getUserById(enrollment.getStudentId());
            if (user instanceof Student) {
                Student student = (Student) user;

                // Get grade for this enrollment
                List<Grade> grades = gradeOps.getGradesByStudent(student.getUserId());
                String gradeValue = "N/A";
                for (Grade grade : grades) {
                    if (grade.getEnrollmentId() == enrollment.getEnrollmentId()) {
                        gradeValue = grade.getGrade();
                        break;
                    }
                }

                StudentDisplay sd = new StudentDisplay(
                    student.getStudentId(),
                    student.getFirstName() + " " + student.getLastName(),
                    student.getMajor(),
                    gradeValue
                );
                studentDisplayList.add(sd);
            }
        }

        studentsTable.setItems(studentDisplayList);
    }

    @FXML
    private void showMyCourses() {
        loadInstructorCourses();
    }

    @FXML
    private void showCreateCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academicmanagmentsystem/view/CourseManagement.fxml"));
            Parent root = loader.load();

            CourseManagementController controller = loader.getController();
            controller.setInstructor(instructor);

            Stage stage = new Stage();
            stage.setTitle("Course Management");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadInstructorCourses()); // Refresh courses when window closes
            stage.show();

        } catch (Exception e) {
            showError("Error opening course management: " + e.getMessage());
        }
    }

    @FXML
    private void showGradeEntry() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academicmanagmentsystem/view/GradeEntry.fxml"));
            Parent root = loader.load();

            GradeEntryController controller = loader.getController();
            controller.setInstructor(instructor);

            Stage stage = new Stage();
            stage.setTitle("Grade Entry");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Error opening grade entry: " + e.getMessage());
        }
    }

    @FXML
    private void showStatistics() {
        CourseDisplay selectedCourse = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showError("Please select a course first");
            return;
        }

        showInfo("Course Statistics for " + selectedCourse.getCourseCode() + ":\n" +
                "Enrolled: " + selectedCourse.getEnrolledCount() + "/" + selectedCourse.getMaxCapacity() + "\n" +
                "Available Seats: " + (selectedCourse.getMaxCapacity() - selectedCourse.getEnrolledCount()));
    }

    @FXML
    private void exportReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Performance Report");
        fileChooser.setInitialFileName("report_" + instructor.getInstructorId() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            boolean success = fileExporter.exportPerformanceReport(instructor.getUserId(), file.getAbsolutePath());
            if (success) {
                showInfo("Report exported successfully to:\n" + file.getAbsolutePath());
            } else {
                showError("Failed to export report");
            }
        }
    }

    @FXML
    private void importGrades() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Grade Sheet");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            int count = fileImporter.importGradeSheet(file.getAbsolutePath());
            if (count > 0) {
                showInfo("Successfully imported " + count + " grades");
                loadInstructorCourses();
            } else {
                String error = fileImporter.getLastError();
                showError("Failed to import grades:\n" + (error.isEmpty() ? "Unknown error" : error));
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
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner classes for display
    public static class CourseDisplay {
        private final int courseId;
        private final String courseCode;
        private final String courseName;
        private final int creditHours;
        private final String semester;
        private final int enrolledCount;
        private final int maxCapacity;

        public CourseDisplay(int courseId, String courseCode, String courseName, int creditHours,
                           String semester, int enrolledCount, int maxCapacity) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.creditHours = creditHours;
            this.semester = semester;
            this.enrolledCount = enrolledCount;
            this.maxCapacity = maxCapacity;
        }

        public int getCourseId() { return courseId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public int getCreditHours() { return creditHours; }
        public String getSemester() { return semester; }
        public int getEnrolledCount() { return enrolledCount; }
        public int getMaxCapacity() { return maxCapacity; }
    }

    public static class StudentDisplay {
        private final String studentId;
        private final String studentName;
        private final String major;
        private final String grade;

        public StudentDisplay(String studentId, String studentName, String major, String grade) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.major = major;
            this.grade = grade;
        }

        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getMajor() { return major; }
        public String getGrade() { return grade; }
    }
}
