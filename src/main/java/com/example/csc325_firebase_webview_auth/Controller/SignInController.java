package com.example.csc325_firebase_webview_auth.Controller;

import com.google.firebase.auth.FirebaseAuth;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;
import javafx.scene.Parent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.example.csc325_firebase_webview_auth.view.AccessFBView;


public class SignInController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;
    @FXML
    private Button backButton; // Declare the backButton

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Stage primaryStage; // This will hold the primary stage reference

    // No-argument constructor is needed
    public SignInController() {}

    // Setter to pass the primary stage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleSignIn() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            String apiKey = "AIzaSyDmi7dNpVsNcBWuAD9FB5UU2oPPxbDSSAU";  // Replace with your actual API key
            String requestURL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

            // Create JSON payload for the API request
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("email", email);
            jsonPayload.put("password", password);
            jsonPayload.put("returnSecureToken", true);

            // Send HTTP POST request
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the request payload
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonPayload.toString().getBytes("UTF-8"));
            outputStream.close();

            // Get the response from Firebase
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            bufferedReader.close();
            inputStream.close();

            // Parse response to check if sign-in was successful
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("idToken")) {
                // Sign-in successful
                String loggedInEmail = jsonResponse.getString("email"); // Retrieve the logged-in user's email

                // Load the AccessFBView.fxml and get the controller
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/AccessFBView.fxml"));
                Parent root = loader.load();

                // Get the AccessFBView controller from the FXML
                AccessFBView accessFBViewController = loader.getController();

                // Pass the logged-in email to the AccessFBView controller
                accessFBViewController.displayLoggedInUser(loggedInEmail);

                // Create a new scene and apply the CSS stylesheet
                Scene scene = new Scene(root);

                // Load the CSS file
                String css = getClass().getResource("/css/styles.css").toExternalForm(); // Make sure the path is correct
                scene.getStylesheets().add(css); // Apply the CSS to the scene

                // Switch to the AccessFBView scene and set window size
                Stage stage = (Stage) emailField.getScene().getWindow(); // Assuming you want to switch windows
                stage.setScene(scene);
                stage.setWidth(1100); // Set width
                stage.setHeight(600); // Set height
                stage.show();

            } else {
                displayLoginFailed();
            }

        } catch (Exception e) {
            e.printStackTrace();
            displayLoginFailed();
        }
    }




    private void displayLoginSuccessful() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Successful");
        alert.setHeaderText(null);
        alert.setContentText("You have successfully logged in!");
        alert.showAndWait();

        // Navigate to the next scene or main application screen here
        try {
            URL fxmlURL = getClass().getResource("/files/AccessFBView.fxml");
            if (fxmlURL == null) {
                System.err.println("FXML file not found!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            AnchorPane mainScreen = loader.load();


            // Load the CSS file
            String css = getClass().getResource("/css/styles.css").toExternalForm(); // Adjust the path accordingly
            mainScreen.getStylesheets().add(css);

            Stage stage = (Stage) emailField.getScene().getWindow(); // Get the current stage
            stage.setScene(new Scene(mainScreen, 1100, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayLoginFailed() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText("Invalid email or password. Please try again.");
        alert.showAndWait();
    }

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
