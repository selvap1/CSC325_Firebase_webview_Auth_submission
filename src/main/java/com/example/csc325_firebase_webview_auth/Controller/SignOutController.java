package com.example.csc325_firebase_webview_auth.Controller;

import com.example.csc325_firebase_webview_auth.view.AccessFBView;
import com.example.csc325_firebase_webview_auth.view.App;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class SignOutController {

    @FXML
    private Label loggedInLabel;

    @FXML
    private MenuItem signOutMenuItem;

    private Stage primaryStage;

    // Setter to pass the primary stage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

        public void handleSignOut() {
            if (loggedInLabel != null) {
                loggedInLabel.setVisible(false);
                loggedInLabel.setText(""); // Clear logged-in info
            }
            // Additional sign-out logic, if needed
        }




    // Method to show alerts
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
