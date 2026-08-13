package academicmanagmentsystem.controller;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class GradeEntryController {

    @FXML
    private ComboBox<CourseItem> courseComboBox;

    @FXML
    private TableView<StudentGrade> studentsTable;

    @FXML
    private TableColumn<StudentGrade, String> studentIdColumn;

    @FXML
    private TableColumn<StudentGrade, String> studentNameColumn;

    @FXML
    private TableColumn<StudentGrade, String> majorColumn;

    @FXML
    private TableColumn<StudentGrade, String> currentGradeColumn;

    @FXML
    private Label selectedStudentLabel;

    @FXML
    private ComboBox<String> gradeComboBox;

    @FXML
    private Label messageLabel;

    private Instructor instructor;
    private CourseOperations courseOps;
    private EnrollmentOperations enrollmentOps;
    private UserOperations userOps;
    private GradeOperations gradeOps;

    public GradeEntryController() {
        courseOps = new CourseOperations();
        enrollmentOps = new EnrollmentOperations();
        userOps = new UserOperations();
        gradeOps = new GradeOperations();
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
        initialize();
    }

    private void initialize() {
        // Initialize table columns
        studentIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentId()));
        studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentName()));
        majorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMajor()));
        currentGradeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCurrentGrade()));

        // Initialize grade combo box with valid grades
        ObservableList<String> grades = FXCollections.observableArrayList(
            "A", "B+", "B", "C+", "C", "D+", "D", "F"
        );
        gradeComboBox.setItems(grades);

        // Add listener to student selection
        studentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedStudentLabel.setText(newSelection.getStudentName());
                // Pre-select current grade if exists
                if (!newSelection.getCurrentGrade().equals("N/A")) {
                    gradeComboBox.setValue(newSelection.getCurrentGrade());
                }
            }
        });

        // Load instructor's courses
        loadInstructorCourses();
    }

    private void loadInstructorCourses() {
        List<Course> courses = courseOps.getCoursesByInstructor(instructor.getUserId());
        ObservableList<CourseItem> courseItems = FXCollections.observableArrayList();

        for (Course course : courses) {
            courseItems.add(new CourseItem(course.getCourseId(), course.getCourseCode() + " - " + course.getCourseName()));
        }

        courseComboBox.setItems(courseItems);
    }

    @FXML
    private void handleCourseSelection() {
        CourseItem selectedCourse = courseComboBox.getValue();
        if (selectedCourse != null) {
            loadStudentsForCourse(selectedCourse.getCourseId());
            messageLabel.setText("");
        }
    }

    private void loadStudentsForCourse(int courseId) {
        List<Enrollment> enrollments = enrollmentOps.getCourseStudents(courseId);
        ObservableList<StudentGrade> studentGrades = FXCollections.observableArrayList();

        for (Enrollment enrollment : enrollments) {
            User user = userOps.getUserById(enrollment.getStudentId());
            if (user instanceof Student) {
                Student student = (Student) user;

                // Get current grade for this enrollment
                List<Grade> grades = gradeOps.getGradesByStudent(student.getUserId());
                String currentGrade = "N/A";
                int gradeId = -1;

                for (Grade grade : grades) {
                    if (grade.getEnrollmentId() == enrollment.getEnrollmentId()) {
                        currentGrade = grade.getGrade();
                        gradeId = grade.getGradeId();
                        break;
                    }
                }

                StudentGrade sg = new StudentGrade(
                    enrollment.getEnrollmentId(),
                    gradeId,
                    student.getStudentId(),
                    student.getFirstName() + " " + student.getLastName(),
                    student.getMajor(),
                    currentGrade
                );
                studentGrades.add(sg);
            }
        }

        studentsTable.setItems(studentGrades);
    }

    @FXML
    private void handleSaveGrade() {
        StudentGrade selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        String selectedGrade = gradeComboBox.getValue();

        if (selectedStudent == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a student");
            return;
        }

        if (selectedGrade == null || selectedGrade.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a grade");
            return;
        }

        // Check if grade already exists
        if (!selectedStudent.getCurrentGrade().equals("N/A")) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Grade already exists. Use 'Update Grade' button instead.");
            return;
        }

        // Add new grade
        Grade newGrade = new Grade();
        newGrade.setEnrollmentId(selectedStudent.getEnrollmentId());
        newGrade.setGrade(selectedGrade);
        newGrade.setNumericGrade(gradeOps.getNumericGrade(selectedGrade));

        boolean success = gradeOps.addGrade(newGrade);

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            messageLabel.setText("Grade saved successfully");
            refreshStudents();
        } else {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Failed to save grade");
        }
    }

    @FXML
    private void handleUpdateGrade() {
        StudentGrade selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        String selectedGrade = gradeComboBox.getValue();

        if (selectedStudent == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a student");
            return;
        }

        if (selectedGrade == null || selectedGrade.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a grade");
            return;
        }

        // Check if grade exists
        if (selectedStudent.getCurrentGrade().equals("N/A")) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("No existing grade. Use 'Save Grade' button instead.");
            return;
        }

        // Update existing grade
        Grade updatedGrade = new Grade();
        updatedGrade.setGradeId(selectedStudent.getGradeId());
        updatedGrade.setEnrollmentId(selectedStudent.getEnrollmentId());
        updatedGrade.setGrade(selectedGrade);
        updatedGrade.setNumericGrade(gradeOps.getNumericGrade(selectedGrade));

        boolean success = gradeOps.updateGrade(updatedGrade);

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            messageLabel.setText("Grade updated successfully");
            refreshStudents();
        } else {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Failed to update grade");
        }
    }

    @FXML
    private void handleClearGrade() {
        StudentGrade selectedStudent = studentsTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a student");
            return;
        }

        // Check if grade exists
        if (selectedStudent.getCurrentGrade().equals("N/A")) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("No grade exists to clear");
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Clear Grade");
        alert.setHeaderText("Clear Grade for " + selectedStudent.getStudentName());
        alert.setContentText("Are you sure you want to clear the grade (" + selectedStudent.getCurrentGrade() + ")?\nThis will set the grade to N/A.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Delete the grade
                boolean success = gradeOps.deleteGrade(selectedStudent.getGradeId());

                if (success) {
                    messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    messageLabel.setText("Grade cleared successfully");
                    refreshStudents();
                } else {
                    messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    messageLabel.setText("Failed to clear grade");
                }
            }
        });
    }

    @FXML
    private void refreshStudents() {
        CourseItem selectedCourse = courseComboBox.getValue();
        if (selectedCourse != null) {
            loadStudentsForCourse(selectedCourse.getCourseId());
            selectedStudentLabel.setText("None selected");
            gradeComboBox.setValue(null);
            messageLabel.setText("");
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) courseComboBox.getScene().getWindow();
        stage.close();
    }

    // Inner classes
    public static class CourseItem {
        private final int courseId;
        private final String displayName;

        public CourseItem(int courseId, String displayName) {
            this.courseId = courseId;
            this.displayName = displayName;
        }

        public int getCourseId() { return courseId; }
        public String getDisplayName() { return displayName; }

        @Override
        public String toString() { return displayName; }
    }

    public static class StudentGrade {
        private final int enrollmentId;
        private final int gradeId;
        private final String studentId;
        private final String studentName;
        private final String major;
        private final String currentGrade;

        public StudentGrade(int enrollmentId, int gradeId, String studentId, String studentName,
                          String major, String currentGrade) {
            this.enrollmentId = enrollmentId;
            this.gradeId = gradeId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.major = major;
            this.currentGrade = currentGrade;
        }

        public int getEnrollmentId() { return enrollmentId; }
        public int getGradeId() { return gradeId; }
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getMajor() { return major; }
        public String getCurrentGrade() { return currentGrade; }
    }
}
