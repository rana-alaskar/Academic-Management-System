package academicmanagmentsystem.controller;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.UserOperations;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class UserManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<UserDisplay> usersTable;

    @FXML
    private TableColumn<UserDisplay, String> usernameColumn;

    @FXML
    private TableColumn<UserDisplay, String> roleColumn;

    @FXML
    private TableColumn<UserDisplay, String> nameColumn;

    @FXML
    private TableColumn<UserDisplay, String> emailColumn;

    @FXML
    private TableColumn<UserDisplay, String> detailsColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private Label roleFieldLabel;

    @FXML
    private TextField roleSpecificField;

    @FXML
    private Label messageLabel;

    private Admin admin;
    private UserOperations userOps;
    private User selectedUser;

    public UserManagementController() {
        userOps = new UserOperations();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        initialize();
    }

    private void initialize() {
        // Initialize table columns
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        detailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDetails()));

        // Initialize role combo box
        ObservableList<String> roles = FXCollections.observableArrayList("Student", "Instructor", "Admin");
        roleComboBox.setItems(roles);

        // Add listener to table selection
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadUserToForm(newSelection);
            }
        });

        // Load all users
        loadAllUsers();
    }

    private void loadAllUsers() {
        List<User> users = userOps.getAllUsers();
        ObservableList<UserDisplay> userDisplayList = FXCollections.observableArrayList();

        for (User user : users) {
            String role = "";
            String details = "";

            if (user instanceof Student) {
                role = "Student";
                Student s = (Student) user;
                details = "ID: " + s.getStudentId() + " | Major: " + s.getMajor();
            } else if (user instanceof Instructor) {
                role = "Instructor";
                Instructor i = (Instructor) user;
                details = "ID: " + i.getInstructorId() + " | Dept: " + i.getDepartment();
            } else if (user instanceof Admin) {
                role = "Admin";
                Admin a = (Admin) user;
                details = "ID: " + a.getAdminId() + " | Level: " + a.getAccessLevel();
            }

            UserDisplay ud = new UserDisplay(
                user.getUserId(),
                user.getUsername(),
                role,
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                details
            );
            userDisplayList.add(ud);
        }

        usersTable.setItems(userDisplayList);
    }

    private void loadUserToForm(UserDisplay userDisplay) {
        User user = userOps.getUserById(userDisplay.getUserId());
        if (user == null) return;

        selectedUser = user;

        usernameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());

        if (user instanceof Student) {
            roleComboBox.setValue("Student");
            roleFieldLabel.setText("Student ID:");
            roleSpecificField.setPromptText("Enter student ID");
            roleSpecificField.setText(((Student) user).getStudentId());
        } else if (user instanceof Instructor) {
            roleComboBox.setValue("Instructor");
            roleFieldLabel.setText("Instructor ID:");
            roleSpecificField.setPromptText("Enter instructor ID");
            roleSpecificField.setText(((Instructor) user).getInstructorId());
        } else if (user instanceof Admin) {
            roleComboBox.setValue("Admin");
            roleFieldLabel.setText("Admin ID:");
            roleSpecificField.setPromptText("Enter admin ID");
            roleSpecificField.setText(((Admin) user).getAdminId());
        }
    }

    @FXML
    private void handleRoleSelection() {
        String selectedRole = roleComboBox.getValue();
        if (selectedRole == null) return;

        switch (selectedRole) {
            case "Student":
                roleFieldLabel.setText("Student ID:");
                roleSpecificField.setPromptText("Enter 9-digit student ID (e.g., 202400001)");
                break;
            case "Instructor":
                roleFieldLabel.setText("Instructor ID:");
                roleSpecificField.setPromptText("Enter 9-digit instructor ID (e.g., 100000001)");
                break;
            case "Admin":
                roleFieldLabel.setText("Admin ID:");
                roleSpecificField.setPromptText("Enter 9-digit admin ID (e.g., 900000001)");
                break;
        }
    }

    @FXML
    private void handleAddUser() {
        if (!validateForm()) {
            return;
        }

        String role = roleComboBox.getValue();
        User newUser = null;

        switch (role) {
            case "Student":
                Student student = new Student();
                student.setStudentId(roleSpecificField.getText().trim());
                student.setMajor("Computer Science"); // Default
                student.setGpa(0.0);
                student.setEnrollmentYear(2024);
                student.setTotalCredits(0);
                newUser = student;
                break;

            case "Instructor":
                Instructor instructor = new Instructor();
                instructor.setInstructorId(roleSpecificField.getText().trim());
                instructor.setDepartment("Computer Science"); // Default
                instructor.setOfficeLocation("TBA");
                instructor.setPhoneExtension("0000");
                instructor.setSpecialization("General");
                newUser = instructor;
                break;

            case "Admin":
                Admin newAdmin = new Admin();
                newAdmin.setAdminId(roleSpecificField.getText().trim());
                newAdmin.setAccessLevel(1); // Default access level
                newUser = newAdmin;
                break;
        }

        if (newUser != null) {
            newUser.setUsername(usernameField.getText().trim());
            newUser.setPassword(passwordField.getText().trim());
            newUser.setRole(role.toUpperCase()); // Convert to uppercase for database
            newUser.setFirstName(firstNameField.getText().trim());
            newUser.setLastName(lastNameField.getText().trim());
            newUser.setEmail(emailField.getText().trim());

            boolean success = userOps.addUser(newUser);

            if (success) {
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                messageLabel.setText("User added successfully");
                clearForm();
                loadAllUsers();
            } else {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                String error = userOps.getLastError();
                messageLabel.setText(error.isEmpty() ? "Failed to add user" : error);
            }
        }
    }

    @FXML
    private void handleUpdateUser() {
        if (selectedUser == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a user from the table first");
            return;
        }

        if (!validateForm()) {
            return;
        }

        String newRole = roleComboBox.getValue();
        String oldRole = "";

        if (selectedUser instanceof Student) {
            oldRole = "Student";
        } else if (selectedUser instanceof Instructor) {
            oldRole = "Instructor";
        } else if (selectedUser instanceof Admin) {
            oldRole = "Admin";
        }

        // If role changed, delete old user and create new one with different role
        if (!oldRole.equals(newRole)) {
            // First delete the old user
            boolean deleted = userOps.deleteUser(selectedUser.getUserId());
            if (!deleted) {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                messageLabel.setText("Failed to update user role");
                return;
            }

            // Create new user with new role
            User newUser = null;
            switch (newRole) {
                case "Student":
                    Student student = new Student();
                    student.setStudentId(roleSpecificField.getText().trim());
                    student.setMajor("Computer Science");
                    student.setGpa(0.0);
                    student.setEnrollmentYear(2024);
                    student.setTotalCredits(0);
                    newUser = student;
                    break;
                case "Instructor":
                    Instructor instructor = new Instructor();
                    instructor.setInstructorId(roleSpecificField.getText().trim());
                    instructor.setDepartment("Computer Science");
                    instructor.setOfficeLocation("TBA");
                    instructor.setPhoneExtension("0000");
                    instructor.setSpecialization("General");
                    newUser = instructor;
                    break;
                case "Admin":
                    Admin newAdmin = new Admin();
                    newAdmin.setAdminId(roleSpecificField.getText().trim());
                    newAdmin.setAccessLevel(1);
                    newUser = newAdmin;
                    break;
            }

            if (newUser != null) {
                newUser.setUsername(usernameField.getText().trim());
                newUser.setPassword(passwordField.getText().trim());
                newUser.setRole(newRole.toUpperCase()); // Convert to uppercase for database
                newUser.setFirstName(firstNameField.getText().trim());
                newUser.setLastName(lastNameField.getText().trim());
                newUser.setEmail(emailField.getText().trim());

                boolean success = userOps.addUser(newUser);
                if (success) {
                    messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    messageLabel.setText("User role changed successfully");
                    clearForm();
                    loadAllUsers();
                } else {
                    messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    String error = userOps.getLastError();
                    messageLabel.setText(error.isEmpty() ? "Failed to change user role" : error);
                }
            }
        } else {
            // Same role, just update fields
            selectedUser.setUsername(usernameField.getText().trim());
            selectedUser.setPassword(passwordField.getText().trim());
            selectedUser.setFirstName(firstNameField.getText().trim());
            selectedUser.setLastName(lastNameField.getText().trim());
            selectedUser.setEmail(emailField.getText().trim());

            // Update role-specific fields
            if (selectedUser instanceof Student) {
                ((Student) selectedUser).setStudentId(roleSpecificField.getText().trim());
            } else if (selectedUser instanceof Instructor) {
                ((Instructor) selectedUser).setInstructorId(roleSpecificField.getText().trim());
            } else if (selectedUser instanceof Admin) {
                ((Admin) selectedUser).setAdminId(roleSpecificField.getText().trim());
            }

            boolean success = userOps.updateUser(selectedUser);

            if (success) {
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                messageLabel.setText("User updated successfully");
                clearForm();
                loadAllUsers();
            } else {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                String error = userOps.getLastError();
                messageLabel.setText(error.isEmpty() ? "Failed to update user" : error);
            }
        }
    }

    @FXML
    private void handleDeleteUser() {
        if (selectedUser == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Please select a user from the table first");
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user: " + selectedUser.getUsername() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = userOps.deleteUser(selectedUser.getUserId());

                if (success) {
                    messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    messageLabel.setText("User deleted successfully");
                    clearForm();
                    loadAllUsers();
                } else {
                    messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    messageLabel.setText("Failed to delete user");
                }
            }
        });
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadAllUsers();
            return;
        }

        List<User> allUsers = userOps.getAllUsers();
        ObservableList<UserDisplay> filteredList = FXCollections.observableArrayList();

        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(searchTerm) ||
                user.getFirstName().toLowerCase().contains(searchTerm) ||
                user.getLastName().toLowerCase().contains(searchTerm) ||
                user.getEmail().toLowerCase().contains(searchTerm)) {

                String role = "";
                String details = "";

                if (user instanceof Student) {
                    role = "Student";
                    Student s = (Student) user;
                    details = "ID: " + s.getStudentId() + " | Major: " + s.getMajor();
                } else if (user instanceof Instructor) {
                    role = "Instructor";
                    Instructor i = (Instructor) user;
                    details = "ID: " + i.getInstructorId() + " | Dept: " + i.getDepartment();
                } else if (user instanceof Admin) {
                    role = "Admin";
                    Admin a = (Admin) user;
                    details = "ID: " + a.getAdminId() + " | Level: " + a.getAccessLevel();
                }

                UserDisplay ud = new UserDisplay(
                    user.getUserId(),
                    user.getUsername(),
                    role,
                    user.getFirstName() + " " + user.getLastName(),
                    user.getEmail(),
                    details
                );
                filteredList.add(ud);
            }
        }

        usersTable.setItems(filteredList);
    }

    @FXML
    private void refreshUsers() {
        searchField.clear();
        clearForm();
        loadAllUsers();
        messageLabel.setText("");
    }

    @FXML
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        roleSpecificField.clear();
        roleFieldLabel.setText("Role-specific:");
        selectedUser = null;
        messageLabel.setText("");
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private boolean validateForm() {
        if (usernameField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Username is required");
            return false;
        }

        if (passwordField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Password is required");
            return false;
        }

        if (roleComboBox.getValue() == null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Role is required");
            return false;
        }

        if (firstNameField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("First name is required");
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Last name is required");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Email is required");
            return false;
        }

        // Validate email format
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Invalid email format. Please enter a valid email address.");
            return false;
        }

        if (roleSpecificField.getText().trim().isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Role-specific ID is required");
            return false;
        }

        // Validate role-specific ID is exactly 9 digits
        String roleSpecificId = roleSpecificField.getText().trim();
        if (!roleSpecificId.matches("^\\d{9}$")) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Role-specific ID must be exactly 9 digits (no letters)");
            return false;
        }

        return true;
    }

    // Inner class for user display
    public static class UserDisplay {
        private final int userId;
        private final String username;
        private final String role;
        private final String fullName;
        private final String email;
        private final String details;

        public UserDisplay(int userId, String username, String role, String fullName,
                         String email, String details) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.fullName = fullName;
            this.email = email;
            this.details = details;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getDetails() { return details; }
    }
}
