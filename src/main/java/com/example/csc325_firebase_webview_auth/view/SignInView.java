package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.Controller.SignInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInView extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Sign In");

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/SignInView.fxml"));
            BorderPane root = loader.load();

            // Get the controller associated with the FXML
            SignInController controller = loader.getController();

            // Set the primary stage in the controller
            controller.setPrimaryStage(primaryStage);

            // Set the scene and define the preferred dimensions
            Scene scene = new Scene(root, 1100, 600); // Adjust the width and height as needed
            primaryStage.setScene(scene);

            // Set a minimum size if desired
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(300);

            // Show the stage
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
