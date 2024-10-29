package com.example.csc325_firebase_webview_auth.view;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;
import java.io.FileInputStream;
import java.io.IOException;
import com.example.csc325_firebase_webview_auth.Controller.SplashScreenController;



public class App extends Application {

    public static Stage primaryStage; // Keep this static to access from other classes
    private static Firestore fstore; // Declare Firestore instance
    public static FirebaseAuth fauth; // Declare FirebaseAuth instance

    // Public static getter for Firestore instance
    public static Firestore getFstore() {
        return fstore; // Access the static fstore variable
    }

    // Public getter for fauth
    public static FirebaseAuth getFauth() {
        return fauth;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        App.primaryStage = primaryStage; // Set the static primaryStage variable
        primaryStage.setTitle("CSC325 Webview Auth");

        // Initialize Firebase
        initializeFirebase();
        fstore = FirestoreClient.getFirestore();
        fauth = FirebaseAuth.getInstance();

        // Load and display the splash screen
        showSplashScreen();

        // Ensure the primary stage is closed when the user exits
        primaryStage.setOnCloseRequest(event -> {
            if (primaryStage.isShowing()) {
                primaryStage.close();
            }
        });
    }


    private void showSplashScreen() throws IOException {
        // Load the splash screen FXML
        FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/files/SplashScreen.fxml"));
        AnchorPane splashLayout = splashLoader.load();
        Scene splashScene = new Scene(splashLayout, 500, 300); // Set the size of the splash screen

        Stage splashStage = new Stage();
        splashStage.setScene(splashScene);
        splashStage.initStyle(StageStyle.UNDECORATED); // Optional: remove window decorations
        splashStage.show();

        // Set up the splash screen controller to handle the button action
        SplashScreenController splashController = splashLoader.getController();
        splashController.setMainApp(this, splashStage);
    }

    public void showMainApplicationWindow() {
        try {
            // Load the main application FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/AccessFBView.fxml"));
            AnchorPane mainView = loader.load(); // Load the main view

            // Create the scene with the main view
            Scene scene = new Scene(mainView, 1100, 600); // Set your desired width and height

            // Apply the CSS stylesheet
            String css = getClass().getResource("/css/styles.css").toExternalForm(); // Adjust the path accordingly
            scene.getStylesheets().add(css);

            // Set the scene with the main view
            primaryStage.setScene(scene);
            primaryStage.show(); // Show the main application window
        } catch (IOException e) {
            e.printStackTrace(); // Log any exceptions
        }
    }


    // Create the Menu Bar
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // Create "File" menu
        Menu fileMenu = new Menu("File");
        MenuItem signInItem = new MenuItem("Sign In");
        MenuItem registerItem = new MenuItem("Register");

        // Define actions for menu items
        signInItem.setOnAction(e -> showSignInView());
        registerItem.setOnAction(e -> showRegistrationView());

        // Add menu items to the file menu
        fileMenu.getItems().addAll(signInItem, registerItem);
        // Add the file menu to the menu bar
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    // Show the Sign In view
    public static void showSignInView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/files/SignInView.fxml"));
            BorderPane signInView = loader.load(); // Load the FXML file
            primaryStage.getScene().setRoot(signInView); // Set the root with the loaded view
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show the Register view
    public static void showRegistrationView() {
        try {
            URL resource = App.class.getResource("/files/RegisterView.fxml");
            if (resource == null) {
                System.out.println("RegisterView.fxml not found");
                return; // Exit the method if the resource is not found
            }
            FXMLLoader loader = new FXMLLoader(resource);
            BorderPane registrationView = loader.load(); // Load the FXML file
            primaryStage.getScene().setRoot(registrationView); // Set the root with the loaded view
        } catch (IOException e) {
            e.printStackTrace(); // Log the error if loading fails
        }
    }

    // Initialize Firebase
    private void initializeFirebase() {
        try {
            // Load Firebase options from a service account key JSON file
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/files/key.json"); // Update this path to your key.json location

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://console.firebase.google.com/u/0/project/week7csc325fbjava/firestore/databases/-default-/data/~2FReferences~2F605f117e-e686-416d-a08e-ceb13d3bd658") // Replace with your database URL
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase Initialized");

            // Initialize Firebase Storage and Firebase Auth
            fauth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Add this static method to return the primary stage
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // Optional: If you want a separate method for changing the root
    public static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        BorderPane newRoot = fxmlLoader.load(); // Load the new FXML file
        primaryStage.getScene().setRoot(newRoot); // Set the new root
    }

    public static void main(String[] args) {
        launch(args);
    }
}
