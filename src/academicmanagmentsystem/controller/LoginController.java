package academicmanagmentsystem.controller;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.UserOperations;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private UserOperations userOps;

    public LoginController() {
        userOps = new UserOperations();
    }

    @FXML
    private void handleLogin() {
        // Clear previous error message
        errorLabel.setText("");

        // Get input values
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        // Authenticate user
        User user = userOps.authenticateUser(username, password);

        if (user == null) {
            errorLabel.setText("Invalid username or password");
            return;
        }

        // Authentication successful - redirect to appropriate dashboard
        try {
            String fxmlFile = "";

            if (user instanceof Student) {
                fxmlFile = "/academicmanagmentsystem/view/StudentDashboard.fxml";
            } else if (user instanceof Instructor) {
                fxmlFile = "/academicmanagmentsystem/view/InstructorDashboard.fxml";
            } else if (user instanceof Admin) {
                fxmlFile = "/academicmanagmentsystem/view/AdminDashboard.fxml";
            }

            // Load the appropriate dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Pass user data to the dashboard controller
            if (user instanceof Student) {
                StudentDashboardController controller = loader.getController();
                controller.setStudent((Student) user);
            } else if (user instanceof Instructor) {
                InstructorDashboardController controller = loader.getController();
                controller.setInstructor((Instructor) user);
            } else if (user instanceof Admin) {
                AdminDashboardController controller = loader.getController();
                controller.setAdmin((Admin) user);
            }

            // Get current stage and set new scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Academic Management System - Dashboard");
            stage.show();

        } catch (Exception e) {
            errorLabel.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
