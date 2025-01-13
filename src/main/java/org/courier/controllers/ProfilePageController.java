package org.courier.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.courier.services.DatabaseUserServices;
import org.courier.utils.SetNameEmail;

import java.io.File;
import java.util.Objects;


public class ProfilePageController {

    DatabaseUserServices databaseUserServices = new DatabaseUserServices();


    @FXML
    private ImageView profileImage;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    public void initialize() {
        // Load user data
        usernameLabel.setText(SetNameEmail.getUserName());
        emailLabel.setText(SetNameEmail.getEmail());

        // Fetch and set profile image
        String imagePath = databaseUserServices.fetchImagePathFromDatabase(SetNameEmail.getEmail());
        if (imagePath == null || imagePath.isEmpty()) {
            // Render default template image
            profileImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cameron.png"))));
        } else {
            // Validate and render the image
            File imageFile = new File(imagePath);
            if (imageFile.exists() && imageFile.isFile()) {
                profileImage.setImage(new Image(imageFile.toURI().toString()));
            } else {
                showAlert("Error", "The profile image path is invalid. Using default image.");
                profileImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cameron.png"))));
            }
        }
    }

    @FXML
    private void handleUpdateProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Update profile image in the UI
            profileImage.setImage(new Image(selectedFile.toURI().toString()));

            // Save the new image path to the database
            boolean isUpdated = databaseUserServices.updateImagePathInDatabase(SetNameEmail.getEmail(), selectedFile.getPath());
            if (isUpdated) {
                showAlert("Profile Picture Updated", "Profile picture updated successfully.");
            } else {
                showAlert("Error", "Failed to update the profile picture. Please try again.");
            }
        }
    }

    /**
     * Display an alert to the user.
     *
     * @param message The message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleChangePasswordModal() {
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
                if (databaseUserServices.updatePasswordInDatabase(SetNameEmail.getEmail(), hashedPassword)) {
                    showAlert("Success", "Password updated successfully.");
                } else {
                    showAlert("Error", "Failed to update password. Please try again.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
       handleNavigation(event, "/DashboardPage.fxml");
    }
    public void handleNavigation(ActionEvent actionEvent, String location) {

        Node source = (Node) actionEvent.getSource();
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
        stage.close();

        // Load and show the welcome screen
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(location));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 1000, 700); // Adjust size as needed
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            showAlert("Error", "Failed to navigate to " + location);
            System.err.println("Error loading " + location + ": " + e.getMessage());
        }

    }
}
