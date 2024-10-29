package com.example.csc325_firebase_webview_auth.Controller; // Ensure the package is correct

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.example.csc325_firebase_webview_auth.view.App;

public class SplashScreenController {
    @FXML
    private Button startButton;

    private App mainApp; // Reference to the main application
    private Stage splashStage; // Reference to the splash stage

    public void setMainApp(App mainApp, Stage splashStage) {
        this.mainApp = mainApp;
        this.splashStage = splashStage;
        startButton.setOnAction(event -> {
            splashStage.close(); // Close the splash screen
            mainApp.showMainApplicationWindow(); // Show the main application window
        });
    }
}
