package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.Controller.RegisterController;
import com.example.csc325_firebase_webview_auth.Controller.SignInController;
import com.example.csc325_firebase_webview_auth.Controller.SignOutController;
import com.example.csc325_firebase_webview_auth.model.Person;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.example.csc325_firebase_webview_auth.view.AccessFBView;


import java.util.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AccessFBView {

    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField imageURLField;
    @FXML
    private ImageView profileImage;

    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;

    @FXML
    private TableView<Person> tableView;

    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> majorColumn;
    @FXML
    private TableColumn<Person, Integer> ageColumn;

    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Stage primaryStage;

    private Stack<String[]> undoStack = new Stack<>();
    private Stack<String[]> redoStack = new Stack<>();

    @FXML
    void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        majorColumn.setCellValueFactory(cellData -> cellData.getValue().majorProperty());
        ageColumn.setCellValueFactory(cellData -> cellData.getValue().ageProperty().asObject());
        tableView.setItems(listOfUsers);

        // Set preferred widths for each column
        nameColumn.setPrefWidth(200); // Width for "Name" column
        majorColumn.setPrefWidth(200); // Width for "Major" column
        ageColumn.setPrefWidth(100);   // Width for "Age" column

        // Optionally, you can set min/max widths if needed
        nameColumn.setMinWidth(150);
        majorColumn.setMinWidth(150);
        ageColumn.setMinWidth(50);

        // Set column resize policy to remove extra space columns
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add selection listener to TableView
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedPerson) -> {
            if (selectedPerson != null) {
                // Populate fields for editing and update the image view
                populateFieldsForEditing(selectedPerson);
                displayImage(selectedPerson.getImageURL());
            }
        });
    }

    // Populate the input fields with data from the selected record for editing
    private void populateFieldsForEditing(Person person) {
        nameField.setText(person.getName());
        majorField.setText(person.getMajor());
        ageField.setText(String.valueOf(person.getAge()));
        imageURLField.setText(person.getImageURL());
    }


    // Add a method to save edits to the selected record
    @FXML
    private void editRecord() {
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            // Update the selected person's details in the UI and Firebase
            selectedPerson.setName(nameField.getText());
            selectedPerson.setMajor(majorField.getText());
            selectedPerson.setAge(Integer.parseInt(ageField.getText()));
            selectedPerson.setImageURL(imageURLField.getText());

            // Refresh TableView to show changes
            tableView.refresh();

            // Update Firebase with the new data
            DocumentReference docRef = App.getFstore().collection("References").document(selectedPerson.getId());
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("Name", selectedPerson.getName());
            updatedData.put("Major", selectedPerson.getMajor());
            updatedData.put("Age", selectedPerson.getAge());
            updatedData.put("ImageURL", selectedPerson.getImageURL());

            // Push the updated data to Firebase and handle the response directly
            ApiFuture<WriteResult> future = docRef.update(updatedData);
            future.addListener(() -> {
                try {
                    // Ensure Firebase operation was successful
                    future.get(); // This will block and throw if there was an error
                    Platform.runLater(() -> {
                        showAlert("Edit Record", "Record updated successfully!", Alert.AlertType.INFORMATION);
                        clearFormFields();
                        displayImage(selectedPerson.getImageURL()); // Update image after editing
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showAlert("Edit Record", "Failed to update record.", Alert.AlertType.ERROR));
                    e.printStackTrace();
                }
            }, Runnable::run); // Execute directly on the current thread, so no extra Executor is needed
        } else {
            showAlert("Edit Record", "Please select a record to edit.", Alert.AlertType.WARNING);
        }
    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
    }

    @FXML
    private void readRecord(ActionEvent event) {
        readFirebase();
    }

    @FXML
    public void regRecord() {
        App.showRegistrationView();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("/files/WebContainer.fxml");
    }

    private void addPerson(Person person) {
        listOfUsers.add(person);  // Add the person to the observable list
        tableView.setItems(listOfUsers);  // Update the TableView with the new list
    }

    private void addData() {
        // Store current values for undo (including imageURL)
        String[] currentValues = {nameField.getText(), majorField.getText(), ageField.getText(), imageURLField.getText()};
        undoStack.push(currentValues); // Push current values onto the stack

        // Create a unique document reference in Firebase
        DocumentReference docRef = App.getFstore()
                .collection("References")
                .document(UUID.randomUUID().toString());

        // Prepare the data for Firebase document
        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        String imageURL = imageURLField.getText();  // Get the image URL from the field
        data.put("ImageURL", imageURL);  // Store image URL in Firebase document

        // Send data to Firebase
        ApiFuture<WriteResult> result = docRef.set(data);

        // Collect data from the input fields
        String name = nameField.getText();
        String major = majorField.getText();
        int age = Integer.parseInt(ageField.getText());
        String docId = docRef.getId();

        // Create a new Person object with all four fields (name, major, age, imageURL)
        Person newPerson = new Person(docId, name, major, age, imageURL);

        // Add the new Person object to the list or table
        addPerson(newPerson);

        // Display the image in the ImageView using the imageURL
        displayImage(imageURL);

        // Show a success alert
        showAlert("Data Entry", "Data added successfully!", Alert.AlertType.INFORMATION);

        // Clear the form fields after adding the record
        clearFormFields();
    }



    private void displayImage(String imageURL) {
        if (imageURL != null && !imageURL.isEmpty()) {
            Image image = new Image(imageURL);  // Load the image from the URL
            profileImage.setImage(image);  // Set the ImageView to display the image
        } else {
            profileImage.setImage(null);  // Clear the ImageView if no imageURL is provided
        }
        // Ensure the TableView does not resize
        tableView.setPrefHeight(200.0);
    }


    private void readFirebase() {
        ApiFuture<QuerySnapshot> future = App.getFstore().collection("References").get();
        List<QueryDocumentSnapshot> documents;

        try {
            documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                listOfUsers.clear();
                for (QueryDocumentSnapshot document : documents) {
                    Person person = new Person(
                            document.getId(),
                            document.getString("Name"),
                            document.getString("Major"),
                            document.getLong("Age").intValue(),
                            document.getString("ImageURL"));
                    listOfUsers.add(person);
                }
                tableView.setItems(listOfUsers);
            } else {
                showAlert("Data Retrieval", "No data found.", Alert.AlertType.WARNING);
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to retrieve data from Firebase.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showSignInPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/SignInView.fxml"));
            BorderPane signInView = loader.load();

            SignInController signInController = loader.getController();
            signInController.setPrimaryStage(App.primaryStage);

            Scene signInScene = new Scene(signInView, 1100, 600);
            signInScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            App.primaryStage.setScene(signInScene);
            App.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegisterMenuAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/RegisterView.fxml"));
            BorderPane registrationView = loader.load();

            RegisterController registerController = loader.getController();
            registerController.setPrimaryStage(App.primaryStage);

            Scene registrationScene = new Scene(registrationView, 1100, 600);
            registrationScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            App.primaryStage.setScene(registrationScene);
            App.primaryStage.setResizable(false);
            App.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the registration view.", Alert.AlertType.ERROR);
        }
    }

    // Menu actions
    @FXML
    public void handleSignInMenuAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/SignInView.fxml"));
            BorderPane signInView = loader.load();

            SignInController signInController = loader.getController();
            signInController.setPrimaryStage(App.primaryStage);

            Scene signInScene = new Scene(signInView, 1100, 600);
            signInScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            App.primaryStage.setScene(signInScene);
            App.primaryStage.setResizable(false);
            App.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the sign-in view.", Alert.AlertType.ERROR);
        }
    }

    public void handleOpenMenuAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            readFile(selectedFile);
        }
    }

    private void readFile(File file) {
        tableView.getItems().clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String id = UUID.randomUUID().toString(); // Generate a unique id

                // Create a new Person with the generated id and other fields
                Person person = new Person(
                        id,
                        fields[0], // name
                        fields[1], // major
                        Integer.parseInt(fields[2]), // age
                        fields.length > 3 ? fields[3] : "" // imageURL
                );
                tableView.getItems().add(person);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to read the file.", Alert.AlertType.ERROR);
        }
    }


    public void handleSaveMenuAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            saveCSVFile(file);
        }
    }

    private void saveCSVFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Name,Major,Age\n");
            for (Person person : listOfUsers) {
                writer.append(person.getName()).append(",")
                        .append(person.getMajor()).append(",")
                        .append(String.valueOf(person.getAge())).append("\n");
            }
            writer.flush();
            showAlert("Success", "File saved successfully.", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save the file.", Alert.AlertType.ERROR);
        }
    }

    private void addInputListeners() {
        // Add listeners to track changes in each field
        nameField.textProperty().addListener((observable, oldValue, newValue) -> trackChange());
        majorField.textProperty().addListener((observable, oldValue, newValue) -> trackChange());
        ageField.textProperty().addListener((observable, oldValue, newValue) -> trackChange());
    }

    private void trackChange() {
        // Push current field values onto the undo stack before changing
        String[] currentValues = {nameField.getText(), majorField.getText(), ageField.getText()};
        undoStack.push(currentValues);

        // Clear the redo stack when a new change is made
        redoStack.clear();
    }

    @FXML
    public void handleUndoMenuAction() {
        if (!undoStack.isEmpty()) {
            // Get current values before clearing
            String[] currentValues = {nameField.getText(), majorField.getText(), ageField.getText()};

            // Clear the fields
            nameField.clear();
            majorField.clear();
            ageField.clear();

            // Push the current values onto the redo stack
            redoStack.push(currentValues);
        }
    }

    @FXML
    public void handleRedoAction() {
        if (!redoStack.isEmpty()) {
            // Restore the last cleared values from the redo stack
            String[] lastClearedValues = redoStack.pop();

            // Restore the text fields
            nameField.setText(lastClearedValues[0]);
            majorField.setText(lastClearedValues[1]);
            ageField.setText(lastClearedValues[2]);
        }
    }

    // Method to clear all the form fields
    public void clearFormFields() {
        nameField.clear();
        majorField.clear();
        ageField.clear();
        imageURLField.clear();
    }
    @FXML
    private Label loggedInLabel;

    // This method will be called to display the logged-in user's email
    public void displayLoggedInUser(String email) {
        loggedInLabel.setText("Logged in as " + email);
        loggedInLabel.setVisible(true); // Make the label visible when a user is logged in
        if (email != null && !email.isEmpty()) {
            loggedInLabel.setText(email); // or any other UI updates
            loggedInLabel.setVisible(true);
        } else {
            loggedInLabel.setVisible(false); // Hide if there's no email
        }
    }


    @FXML
    private MenuItem signOutMenuItem;



    @FXML
    private void handleSignOutMenuAction() {
        // Clear any logged-in info
        if (loggedInLabel != null) {
            loggedInLabel.setVisible(false);
            loggedInLabel.setText(""); // Clear any logged-in info
        }

        try {
            // Load the AccessFBView FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/AccessFBView.fxml"));
            Parent root = loader.load(); // Load the FXML

            // Get the controller for the loaded FXML
            AccessFBView accessFBViewController = loader.getController();
            // Set the primary stage in the controller (if necessary)
            accessFBViewController.setPrimaryStage(App.primaryStage);

            // Create a new scene with the loaded root
            Scene scene = new Scene(root, 1300, 600); // Set your desired width and height

            // Apply the CSS stylesheet if needed
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Set the scene with the loaded view
            App.primaryStage.setScene(scene);
            App.primaryStage.show(); // Show the main application window
        } catch (IOException e) {
            e.printStackTrace(); // Log any exceptions
            showAlert("Error", "Failed to load the AccessFBView.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteRecord() {
        // Get the selected person from the TableView
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();

        if (selectedPerson != null) {
            // Remove the selected item from the TableView
            listOfUsers.remove(selectedPerson);

            // Optionally, delete from Firebase
            deleteRecordFromFirebase(selectedPerson);

            showAlert("Delete Record", "Record deleted successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Delete Record", "No record selected for deletion.", Alert.AlertType.WARNING);
        }
    }

    private void deleteRecordFromFirebase(Person person) {
        // Assuming you have a unique ID field in Person (e.g., `person.getId()`)
        if (person.getId() != null && !person.getId().isEmpty()) {
            // Reference the document using the ID from the person object
            DocumentReference docRef = App.getFstore().collection("References").document(person.getId());

            // Delete the document in Firebase
            docRef.delete().addListener(() -> System.out.println("Record deleted from Firebase."),
                    Runnable::run);
        }
    }














    // This method will be triggered when the Exit menu item is clicked
    public void handleExitMenuAction() {
        Platform.exit();  // Gracefully exits the application
    }
}
