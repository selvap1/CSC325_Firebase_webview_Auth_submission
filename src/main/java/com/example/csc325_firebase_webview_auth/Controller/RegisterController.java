package com.example.csc325_firebase_webview_auth.Controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView; // Ensure you have this import
import com.google.firebase.cloud.StorageClient;
import com.google.cloud.storage.Blob; // For Google Cloud Storage Blob
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class RegisterController {

    @FXML
    private TextField usernameField;  // Collect a username
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button uploadButton;

    @FXML
    private BorderPane rootPane;

    @FXML
    private ImageView profileImageView; // To display the selected image

    @FXML
    private TextField imagePathField; // Field where user provides image path or selected image

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void onUploadButtonClick() {
        String imagePath = imagePathField.getText(); // Get image path from a text field or file chooser
        String destinationPath = "users/images/" + UUID.randomUUID().toString() + ".jpg"; // Unique path for Firebase storage

        try {
            uploadImageToFirebase(imagePath, destinationPath);
            System.out.println("Image uploaded successfully.");
        } catch (Exception e) {
            System.out.println("Error uploading image: " + e.getMessage());
        }
    }

    public void uploadImageToFirebase(String imagePath, String destinationPath) throws Exception {
        // Uploading to Firebase
        StorageClient storageClient = StorageClient.getInstance();
        File imageFile = new File(imagePath);
        byte[] imageBytes = Files.readAllBytes(Paths.get(imageFile.getPath()));
        Blob blob = storageClient.bucket().create(destinationPath, imageBytes, "image/jpeg");
        System.out.println("Image uploaded to: " + blob.getMediaLink());
    }

    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "All fields must be filled out.");
            return;
        }

        // Create a request to create a user
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        try {
            // Register the user with Firebase
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            // Show success message
            showAlert("Registration Successful", "User " + userRecord.getEmail() + " registered successfully.");

            // Optionally clear fields or navigate to another view
            clearFields();

        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            showAlert("Registration Failed", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        emailField.clear();
        passwordField.clear();
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
