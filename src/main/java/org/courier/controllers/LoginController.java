package org.courier.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.courier.services.DatabaseUserServices;
import org.courier.utils.SetNameEmail;

public class LoginController {

    private final DatabaseUserServices databaseUserServices = new DatabaseUserServices();

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnReturn;

    @FXML
    private Hyperlink handleChangePassword;

    @FXML
    private Hyperlink linkSignUp;


    @FXML
    private void initialize() {
        // Set login button action
        btnLogin.setOnAction(event -> loginUser());

        // Set return button action
        btnReturn.setOnAction(this::returnToWelcomeScreen);

        // Handle "Don't have an account" hyperlink
        linkSignUp.setOnAction(event -> handleRedirect(event, "/SignUpScreen.fxml", "Sign Up"));

        // Handle "Forgot Password" hyperlink
        handleChangePassword.setOnAction(event -> handleChangePasswordModal());
    }

    @FXML
    private void handleChangePasswordModal() {
        String email = txtEmail.getText().trim();


        if (email.isEmpty()) {
            showAlert("Update Password", "Please enter your email to update password.");
            return;
        }
        if (!databaseUserServices.doesEmailExist(email)) {
            showAlert("Validation Error", "This email is not registered in our system.");
            return;
        }

        // Create a new dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update Your Password");

        // Set the button types
        ButtonType confirmButtonType = new ButtonType("Update Password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Create a grid for the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add password fields
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        grid.add(new Label("New Password:"), 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable confirm button based on input validation
        final Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(newPasswordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty());
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(newPasswordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty());
        });

        // Handle form submission
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                // Validate passwords
                if (!newPassword.equals(confirmPassword)) {
                    showAlert("Validation Error", "Passwords do not match.");
                    return null;
                }
                if (newPassword.length() < 8) {
                    showAlert("Validation Error", "Password must be at least 8 characters long.");
                    return null;
                }

                // Hash and update the password
                String hashedPassword = databaseUserServices.hashPassword(newPassword);
                if (databaseUserServices.updatePasswordInDatabase(email, hashedPassword)) {
                    showAlert("Success", "Password updated successfully.");
                } else {
                    showAlert("Error", "Failed to update password. Please try again.");
                }
            }
            return null;
        });

        dialog.showAndWait();
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



    // Handle login logic
    private void loginUser() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Login", "Please enter both email and password.");
            return;
        }

        //check if it's an admin(testing purposes)
        if (email.equals("charlse@gmail.com") && password.equals("admin")) {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/admin/dashboard.fxml"));
                javafx.scene.Parent root = loader.load();

                // Get the current stage
                javafx.stage.Stage stage = (javafx.stage.Stage) txtEmail.getScene().getWindow();

                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (java.io.IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to load the HomePage.");
            }

            return;

        }

        // Check credentials in the database
        if (databaseUserServices.validateUser(email, password)) {
            String userName = databaseUserServices.fetchUserName(email);
            if (userName != null) {
                showAlert("Login", "Login successful!");

                //Setting the username in the utils/SetName class to use anywhere
                SetNameEmail setName = new SetNameEmail(userName, email);
                setName.setUserName(userName);
                setName.setEmail(email);
                String username = SetNameEmail.getUserName();

                redirectToHomPage(username);

            } else {
                showAlert("Error", "Unable to retrieve user details.");
            }
        } else {
            showAlert("Login", "Invalid email or password. Please try again.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void returnToWelcomeScreen(javafx.event.ActionEvent event) {
        handleRedirect(event, "/WelcomeScreen.fxml", "Welcome");
    }

    private void redirectToHomPage(String userName) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/HomePage.fxml"));
            javafx.scene.Parent root = loader.load();

            HomePageController homePageController = loader.getController();
            homePageController.setUserName(userName);

            // Get the current stage
            javafx.stage.Stage stage = (javafx.stage.Stage) txtEmail.getScene().getWindow();

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the HomePage.");
        }
    }
}
