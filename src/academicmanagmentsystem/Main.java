package academicmanagmentsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the Login screen as the initial view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academicmanagmentsystem/view/Login.fxml"));
            Parent root = loader.load();

            // Create the scene
            Scene scene = new Scene(root, 600, 400);

            // Configure the primary stage
            primaryStage.setTitle("Academic Management System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);

            // Show the application
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Cleanup resources when application closes
        System.out.println("Application closing...");

        // Close database connections if needed
        try {
            academicmanagmentsystem.operations.DatabaseConnection.getInstance().closeConnection();
            System.out.println("Database connection closed successfully");
        } catch (Exception e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Main method - entry point of the application
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Print application startup information
        System.out.println("===========================================");
        System.out.println("  Academic Management System");
        System.out.println("  Version 1.0");
        System.out.println("  Course: SW331 Advanced Programming");
        System.out.println("===========================================");
        System.out.println();

        // Launch the JavaFX application
        launch(args);
    }
}
