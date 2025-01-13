package org.courier.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeController {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnSignUp;

    @FXML
    private Button btnExit;


    @FXML
    private void initialize() {
        // Apply animations to buttons and text field
        applyFadeTransition(btnLogin);
        applyFadeTransition(btnSignUp);
        applyFadeTransition(btnExit);
        applySlideTransition(btnLogin);
        applySlideTransition(btnSignUp);

        // Set button actions
        btnLogin.setOnAction(this::navigateToLoginScreen);

        // Navigate to the Sign-Up screen
        btnSignUp.setOnAction(this::navigateToSignUpScreen);


    }

    @FXML
    private void handleExit() {
        // Display a confirmation dialog before exiting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("You can always come back later.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Close the application
                Stage stage = (Stage) btnExit.getScene().getWindow();
                stage.close();
            }
        });
    }

    // Display an alert with the given title and content
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Apply a fade-in animation to a node
    private void applyFadeTransition(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(2000), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(1);
        fade.play();
    }

    // Apply a slide-in animation to a node
    private void applySlideTransition(Node node) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(2000), node);
        slide.setFromY(50); // Start position slightly off-screen vertically
        slide.setToY(0); // End at the original position
        slide.setCycleCount(1);
        slide.play();
    }

    // Navigate to the Sign-Up screen
    private void navigateToSignUpScreen(javafx.event.ActionEvent event) {
        try {
            // Close the current welcome window
            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

            // Load the Sign-Up screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUpScreen.fxml"));
            Scene signUpScene = new Scene(loader.load(), 800, 700); // Adjust size if necessary

            // Show the Sign-Up screen
            Stage newStage = new Stage();
            newStage.setScene(signUpScene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to navigate to Sign-Up screen.");
        }
    }

    // Navigate to the Login screen
    private void navigateToLoginScreen(javafx.event.ActionEvent event) {
        try {
            // Close the current welcome window
            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

            // Load the Sign-Up screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginScreen.fxml"));
            Scene signUpScene = new Scene(loader.load(), 800, 700); // Adjust size if necessary

            // Show the Sign-Up screen
            Stage newStage = new Stage();
            newStage.setScene(signUpScene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to navigate to Login screen.");
        }
    }
}
