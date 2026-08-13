package academicmanagmentsystem.controller;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class CourseRegistrationController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<CourseDisplay> coursesTable;

    @FXML
    private TableColumn<CourseDisplay, String> courseCodeColumn;

    @FXML
    private TableColumn<CourseDisplay, String> courseNameColumn;

    @FXML
    private TableColumn<CourseDisplay, Integer> creditHoursColumn;

    @FXML
    private TableColumn<CourseDisplay, String> instructorColumn;

    @FXML
    private TableColumn<CourseDisplay, String> semesterColumn;

    @FXML
    private TableColumn<CourseDisplay, Integer> enrolledColumn;

    @FXML
    private TableColumn<CourseDisplay, Integer> capacityColumn;

    @FXML
    private Label messageLabel;

    private Student student;
    private CourseOperations courseOps;
    private EnrollmentOperations enrollmentOps;
    private UserOperations userOps;

    public CourseRegistrationController() {
        courseOps = new CourseOperations();
        enrollmentOps = new EnrollmentOperations();
        userOps = new UserOperations();
    }

    public void setStudent(Student student) {
        this.student = student;
        initializeTable();
        loadAvailableCourses();
    }

    private void initializeTable() {
        courseCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        creditHoursColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCreditHours()).asObject());
        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInstructorName()));
        semesterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSemester()));
        enrolledColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEnrolledCount()).asObject());
        capacityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaxCapacity()).asObject());
    }

    private void loadAvailableCourses() {
        List<Course> courses = courseOps.getAvailableCourses();
        ObservableList<CourseDisplay> courseDisplayList = FXCollections.observableArrayList();

        for (Course course : courses) {
            // Get instructor name
            User instructor = userOps.getUserById(course.getInstructorId());
            String instructorName = instructor != null ? instructor.getFirstName() + " " + instructor.getLastName() : "TBA";

            // Get enrollment count
            int enrolledCount = courseOps.getCourseEnrollments(course.getCourseId());

            CourseDisplay cd = new CourseDisplay(
                course.getCourseId(),
                course.getCourseCode(),
                course.getCourseName(),
                course.getCreditHours(),
                instructorName,
                course.getSemester() + " " + course.getYear(),
                enrolledCount,
                course.getMaxCapacity()
            );
            courseDisplayList.add(cd);
        }

        coursesTable.setItems(courseDisplayList);
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadAvailableCourses();
            return;
        }

        List<Course> allCourses = courseOps.getAvailableCourses();
        ObservableList<CourseDisplay> filteredList = FXCollections.observableArrayList();

        for (Course course : allCourses) {
            if (course.getCourseCode().toLowerCase().contains(searchTerm) ||
                course.getCourseName().toLowerCase().contains(searchTerm)) {

                User instructor = userOps.getUserById(course.getInstructorId());
                String instructorName = instructor != null ? instructor.getFirstName() + " " + instructor.getLastName() : "TBA";
                int enrolledCount = courseOps.getCourseEnrollments(course.getCourseId());

                CourseDisplay cd = new CourseDisplay(
                    course.getCourseId(),
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCreditHours(),
                    instructorName,
                    course.getSemester() + " " + course.getYear(),
                    enrolledCount,
                    course.getMaxCapacity()
                );
                filteredList.add(cd);
            }
        }

        coursesTable.setItems(filteredList);
    }

    @FXML
    private void refreshCourses() {
        searchField.clear();
        messageLabel.setText("");
        loadAvailableCourses();
    }

    @FXML
    private void handleEnroll() {
        CourseDisplay selectedCourse = coursesTable.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a course to enroll");
            return;
        }

        // Check if already enrolled
        if (enrollmentOps.checkDuplicateEnrollment(student.getUserId(), selectedCourse.getCourseId())) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("You are already enrolled in this course");
            return;
        }

        // Check capacity
        if (!enrollmentOps.checkEnrollmentCapacity(selectedCourse.getCourseId())) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("This course is full");
            return;
        }

        // Enroll student
        boolean success = enrollmentOps.enrollStudent(student.getUserId(), selectedCourse.getCourseId());

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            messageLabel.setText("Successfully enrolled in " + selectedCourse.getCourseCode());
            refreshCourses();
        } else {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Failed to enroll. Please try again.");
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) searchField.getScene().getWindow();
        stage.close();
    }

    // Inner class for course display with enrollment info
    public static class CourseDisplay {
        private final int courseId;
        private final String courseCode;
        private final String courseName;
        private final int creditHours;
        private final String instructorName;
        private final String semester;
        private final int enrolledCount;
        private final int maxCapacity;

        public CourseDisplay(int courseId, String courseCode, String courseName, int creditHours,
                           String instructorName, String semester, int enrolledCount, int maxCapacity) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.creditHours = creditHours;
            this.instructorName = instructorName;
            this.semester = semester;
            this.enrolledCount = enrolledCount;
            this.maxCapacity = maxCapacity;
        }

        public int getCourseId() { return courseId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public int getCreditHours() { return creditHours; }
        public String getInstructorName() { return instructorName; }
        public String getSemester() { return semester; }
        public int getEnrolledCount() { return enrolledCount; }
        public int getMaxCapacity() { return maxCapacity; }
    }
}
