package academicmanagmentsystem.controller;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.CourseOperations;
import academicmanagmentsystem.operations.UserOperations;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCourseManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<CourseDisplay> coursesTable;

    @FXML
    private TableColumn<CourseDisplay, String> courseCodeColumn;

    @FXML
    private TableColumn<CourseDisplay, String> courseNameColumn;

    @FXML
    private TableColumn<CourseDisplay, String> instructorColumn;

    @FXML
    private TableColumn<CourseDisplay, String> semesterColumn;

    @FXML
    private TextField courseCodeField;

    @FXML
    private TextField courseNameField;

    @FXML
    private TextField creditHoursField;

    @FXML
    private ComboBox<String> instructorComboBox;

    @FXML
    private ComboBox<String> semesterComboBox;

    @FXML
    private TextField yearField;

    @FXML
    private TextField maxCapacityField;

    @FXML
    private Label messageLabel;

    private Admin admin;
    private CourseOperations courseOps;
    private UserOperations userOps;
    private Course selectedCourse;
    private Map<String, Integer> instructorMap; // Maps display name to instructor ID

    public AdminCourseManagementController() {
        courseOps = new CourseOperations();
        userOps = new UserOperations();
        instructorMap = new HashMap<>();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        initialize();
    }

    private void initialize() {
        // Initialize table columns
        courseCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInstructorName()));
        semesterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSemester()));

        // Initialize semester combo box
        ObservableList<String> semesters = FXCollections.observableArrayList("Fall", "Spring", "Summer");
        semesterComboBox.setItems(semesters);

        // Initialize instructor combo box
        loadInstructors();

        // Add listener to table selection
        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadCourseToForm(newSelection);
            }
        });

        // Load all courses
        loadAllCourses();
    }

    private void loadInstructors() {
        List<User> allUsers = userOps.getAllUsers();
        ObservableList<String> instructorList = FXCollections.observableArrayList();

        for (User user : allUsers) {
            if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                String displayName = instructor.getFirstName() + " " + instructor.getLastName() +
                                   " (" + instructor.getInstructorId() + ")";
                instructorList.add(displayName);
                instructorMap.put(displayName, instructor.getUserId());
            }
        }

        instructorComboBox.setItems(instructorList);
    }

    private void loadAllCourses() {
        List<Course> courses = courseOps.getAllCourses();
        ObservableList<CourseDisplay> courseDisplayList = FXCollections.observableArrayList();

        for (Course course : courses) {
            // Get instructor name
            User user = userOps.getUserById(course.getInstructorId());
            String instructorName = "Unknown";
            if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                instructorName = instructor.getFirstName() + " " + instructor.getLastName();
            }

            CourseDisplay cd = new CourseDisplay(
                course.getCourseId(),
                course.getCourseCode(),
                course.getCourseName(),
                instructorName,
                course.getSemester() + " " + course.getYear()
            );
            courseDisplayList.add(cd);
        }

        coursesTable.setItems(courseDisplayList);
    }

    private void loadCourseToForm(CourseDisplay courseDisplay) {
        Course course = courseOps.getCourseById(courseDisplay.getCourseId());
        if (course == null) return;

        selectedCourse = course;

        courseCodeField.setText(course.getCourseCode());
        courseNameField.setText(course.getCourseName());
        creditHoursField.setText(String.valueOf(course.getCreditHours()));
        semesterComboBox.setValue(course.getSemester());
        yearField.setText(String.valueOf(course.getYear()));
        maxCapacityField.setText(String.valueOf(course.getMaxCapacity()));

        // Set instructor combo box
        for (Map.Entry<String, Integer> entry : instructorMap.entrySet()) {
            if (entry.getValue() == course.getInstructorId()) {
                instructorComboBox.setValue(entry.getKey());
                break;
            }
        }
    }

    @FXML
    private void handleAddCourse() {
        if (!validateForm()) {
            return;
        }

        try {
            String selectedInstructor = instructorComboBox.getValue();
            Integer instructorId = instructorMap.get(selectedInstructor);

            if (instructorId == null) {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                messageLabel.setText("Please select a valid instructor");
                return;
            }

            Course newCourse = new Course();
            newCourse.setCourseCode(courseCodeField.getText().trim().toUpperCase());
            newCourse.setCourseName(courseNameField.getText().trim());
            newCourse.setCreditHours(Integer.parseInt(creditHoursField.getText().trim()));
            newCourse.setInstructorId(instructorId);
            newCourse.setSemester(semesterComboBox.getValue());
            newCourse.setYear(Integer.parseInt(yearField.getText().trim()));
            newCourse.setMaxCapacity(Integer.parseInt(maxCapacityField.getText().trim()));

            boolean success = courseOps.addCourse(newCourse);

            if (success) {
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                messageLabel.setText("Course added successfully");
                clearForm();
                loadAllCourses();
            } else {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                messageLabel.setText("Failed to add course. Course code may already exist.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Invalid number format in numeric fields");
        }
    }

    @FXML
    private void handleUpdateCourse() {
        if (selectedCourse == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a course from the table first");
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            String selectedInstructor = instructorComboBox.getValue();
            Integer instructorId = instructorMap.get(selectedInstructor);

            if (instructorId == null) {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                messageLabel.setText("Please select a valid instructor");
                return;
            }

            selectedCourse.setCourseCode(courseCodeField.getText().trim().toUpperCase());
            selectedCourse.setCourseName(courseNameField.getText().trim());
            selectedCourse.setCreditHours(Integer.parseInt(creditHoursField.getText().trim()));
            selectedCourse.setInstructorId(instructorId);
            selectedCourse.setSemester(semesterComboBox.getValue());
            selectedCourse.setYear(Integer.parseInt(yearField.getText().trim()));
            selectedCourse.setMaxCapacity(Integer.parseInt(maxCapacityField.getText().trim()));

            boolean success = courseOps.updateCourse(selectedCourse);

            if (success) {
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                messageLabel.setText("Course updated successfully");
                clearForm();
                loadAllCourses();
            } else {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                messageLabel.setText("Failed to update course");
            }
        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Invalid number format in numeric fields");
        }
    }

    @FXML
    private void handleDeleteCourse() {
        if (selectedCourse == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a course from the table first");
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Course");
        alert.setContentText("Are you sure you want to delete course: " + selectedCourse.getCourseCode() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = courseOps.deleteCourse(selectedCourse.getCourseId());

                if (success) {
                    messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    messageLabel.setText("Course deleted successfully");
                    clearForm();
                    loadAllCourses();
                } else {
                    messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    messageLabel.setText("Failed to delete course. It may have enrollments.");
                }
            }
        });
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadAllCourses();
            return;
        }

        List<Course> allCourses = courseOps.getAllCourses();
        ObservableList<CourseDisplay> filteredList = FXCollections.observableArrayList();

        for (Course course : allCourses) {
            if (course.getCourseCode().toLowerCase().contains(searchTerm) ||
                course.getCourseName().toLowerCase().contains(searchTerm)) {

                // Get instructor name
                User user = userOps.getUserById(course.getInstructorId());
                String instructorName = "Unknown";
                if (user instanceof Instructor) {
                    Instructor instructor = (Instructor) user;
                    instructorName = instructor.getFirstName() + " " + instructor.getLastName();
                }

                CourseDisplay cd = new CourseDisplay(
                    course.getCourseId(),
                    course.getCourseCode(),
                    course.getCourseName(),
                    instructorName,
                    course.getSemester() + " " + course.getYear()
                );
                filteredList.add(cd);
            }
        }

        coursesTable.setItems(filteredList);
    }

    @FXML
    private void refreshCourses() {
        searchField.clear();
        clearForm();
        loadAllCourses();
        messageLabel.setText("");
    }

    @FXML
    private void clearForm() {
        courseCodeField.clear();
        courseNameField.clear();
        creditHoursField.clear();
        instructorComboBox.setValue(null);
        semesterComboBox.setValue(null);
        yearField.clear();
        maxCapacityField.clear();
        selectedCourse = null;
        messageLabel.setText("");
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) courseCodeField.getScene().getWindow();
        stage.close();
    }

    private boolean validateForm() {
        // Validate Course Code
        if (courseCodeField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Course code is required");
            return false;
        }

        String courseCode = courseCodeField.getText().trim();
        if (!courseCode.matches("^[A-Z]{2,4}\\d{3}$")) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Invalid course code format. Use format: CS101, MATH201, etc.");
            return false;
        }

        // Validate Course Name
        if (courseNameField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Course name is required");
            return false;
        }

        if (courseNameField.getText().trim().length() < 3) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Course name must be at least 3 characters long");
            return false;
        }

        // Validate Credit Hours
        if (creditHoursField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Credit hours is required");
            return false;
        }

        try {
            int creditHours = Integer.parseInt(creditHoursField.getText().trim());
            if (creditHours < 1 || creditHours > 6) {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                messageLabel.setText("Credit hours must be between 1 and 6");
                return false;
            }
        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Credit hours must be a valid number");
            return false;
        }

        // Validate Instructor
        if (instructorComboBox.getValue() == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Instructor is required");
            return false;
        }

        // Validate Semester
        if (semesterComboBox.getValue() == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Semester is required");
            return false;
        }

        // Validate Year
        if (yearField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Year is required");
            return false;
        }

        try {
            int year = Integer.parseInt(yearField.getText().trim());
            if (year < 2025 || year > 2030) {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                messageLabel.setText("Year must be between 2025 and 2030");
                return false;
            }
        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Year must be a valid 4-digit number");
            return false;
        }

        // Validate Max Capacity
        if (maxCapacityField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Max capacity is required");
            return false;
        }

        try {
            int maxCapacity = Integer.parseInt(maxCapacityField.getText().trim());
            if (maxCapacity < 5 || maxCapacity > 200) {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                messageLabel.setText("Max capacity must be between 5 and 200");
                return false;
            }
        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Max capacity must be a valid number");
            return false;
        }

        return true;
    }

    // Inner class for course display
    public static class CourseDisplay {
        private final int courseId;
        private final String courseCode;
        private final String courseName;
        private final String instructorName;
        private final String semester;

        public CourseDisplay(int courseId, String courseCode, String courseName, String instructorName, String semester) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.instructorName = instructorName;
            this.semester = semester;
        }

        public int getCourseId() { return courseId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getInstructorName() { return instructorName; }
        public String getSemester() { return semester; }
    }
}
