package org.courier.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.courier.services.DatabaseUserServices;
import org.courier.utils.SetNameEmail;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {

    DatabaseUserServices databaseUserServices = new DatabaseUserServices();

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private Button btnSignUp;

    @FXML
    private Button btnReturn;

    @FXML
    private Hyperlink linkSignIn;

    @FXML
    private void initialize() {
        // Apply fade transition to fields and buttons
        applyFadeTransition(txtEmail);
        applyFadeTransition(txtUsername);
        applyFadeTransition(txtPassword);
        applyFadeTransition(txtConfirmPassword);
        applyFadeTransition(btnSignUp);
        applyFadeTransition(btnReturn);

        // Set the sign-up button action
        btnSignUp.setOnAction(event -> signUpUser());

        // Set the return button action
        btnReturn.setOnAction(this::returnToWelcomeScreen);

        // Handle "Already have an account? Sign In" hyperlink
        linkSignIn.setOnAction(event -> handleRedirect(event, "/LoginScreen.fxml", "Login"));
    }

    // Display an alert message
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Apply a fade transition to nodes
    private void applyFadeTransition(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(2000), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(1);
        fade.play();
    }

    // Handle sign-up logic
    private void signUpUser() {
        String email = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        // Check if all fields are filled
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Sign Up", "Please fill all fields.");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert("Sign Up", "Passwords do not match.");
            return;
        }

        // Save user data to the SQLite database
        if (saveUserToDatabase(email, username, databaseUserServices.hashPassword(password))) {
            showAlert("Sign Up", "Sign Up successful!");
            SetNameEmail setName = new SetNameEmail(username, email);
            setName.setUserName(username);
            setName.setEmail(email);
            String userName = SetNameEmail.getUserName();

            redirectToHomePage(userName);
        } else {
            showAlert("Sign Up", "An error occurred. Please try again.");
        }
    }

    // Save user details to the SQLite database
    private boolean saveUserToDatabase(String email, String username, String hashedPassword) {
        // Get the absolute path of the SQLite database
        File dbFile = new File("C:/Users/NTECH/OneDrive/Desktop/CREATED DATABASES/courier.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        // SQL query to insert new user
        String sql = "INSERT INTO users(username, email, password) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Return true if the user was inserted successfully

        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if an error occurs
        }
    }



    private void returnToWelcomeScreen(javafx.event.ActionEvent event) {
        // Hide current window (Sign-Up screen)
        Node source = (Node) event.getSource();
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
        stage.close();

        // Load and show the welcome screen
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/WelcomeScreen.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 900, 700); // Adjust size as needed
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // Redirect to the dashboard
    private void redirectToHomePage(String userName) {
        try {
            // Load the Dashboard FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/HomePage.fxml"));
            javafx.scene.Parent root = loader.load();

            HomePageController homePageController = loader.getController();
            homePageController.setUserName(userName);

            // Get the current stage
            javafx.stage.Stage stage = (javafx.stage.Stage) txtEmail.getScene().getWindow();

            // Set the new scene (dashboard)
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the dashboard.");
        }
    }

    private void handleRedirect(ActionEvent event, String fxmlPath, String pageTitle) {
        Node source = (Node) event.getSource();
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
        stage.close();

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 1000, 700);
            stage.setScene(scene);
            stage.setTitle(pageTitle);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

}
