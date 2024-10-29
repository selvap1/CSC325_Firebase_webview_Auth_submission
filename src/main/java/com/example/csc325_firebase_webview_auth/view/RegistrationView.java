package com.example.csc325_firebase_webview_auth.view;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class RegistrationView {

    @FXML
    private TextField usernameField; // This should be for email
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    // FirebaseAuth instance
    private FirebaseAuth firebaseAuth = App.getFauth();

    @FXML
    private void handleRegister() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email and Password cannot be empty.");
            return;
        }

        try {
            // Create user in Firebase
            UserRecord userRecord = firebaseAuth.createUser(new CreateRequest()
                    .setEmail(email)  // Assuming username is an email
                    .setPassword(password));

            // Show success alert
            showAlert("Registration Successful", "You have been registered successfully!");

            // Optionally navigate back to sign-in or main view here
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    // Method to show an alert dialog
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(content);

        // Show the alert and wait for it to be closed
        alert.showAndWait();

        // After the alert is closed, navigate back to AccessFBView
        navigateToAccessFBView();
    }

    // Method to navigate back to AccessFBView
    private void navigateToAccessFBView() {
        try {
            // Load AccessFBView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/AccessFBView.fxml"));
            AnchorPane accessFBView = loader.load();

            // Create a new scene with the loaded AnchorPane
            Scene newScene = new Scene(accessFBView, 1100, 600); // Set desired dimensions
            newScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            // Get the current stage
            Stage stage = (Stage) usernameField.getScene().getWindow(); // Use usernameField to get the stage

            // Set the new scene to the current stage
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button backButton; // Declare the backButton

    // Back button action to navigate to AccessFBView
    @FXML
    public void handleBackButtonAction() {
        try {
            // Load AccessFBView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/AccessFBView.fxml"));
            AnchorPane accessFBView = loader.load();

            // Create a new scene with the loaded AnchorPane and set dimensions
            Scene newScene = new Scene(accessFBView, 1100, 600); // Set width and height here

            // Add the stylesheet to the new scene
            newScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Set the new scene to the current stage
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
