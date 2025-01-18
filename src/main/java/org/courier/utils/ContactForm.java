package org.courier.utils;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.courier.services.DatabaseUserServices;

public class ContactForm {

    DatabaseUserServices databaseUserServices = new DatabaseUserServices();
    String email = SetNameEmail.getEmail();
    String username = SetNameEmail.getUserName();

    public void showContactForm() {
        // Create a new dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Contact Us");
        dialog.setHeaderText("Send us a message. We'll get back to you as soon as possible.");

        // Set the button types
        ButtonType sendButtonType = new ButtonType("Send Message", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

        // Create a grid for the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add fields for name, email, and message
        TextField nameField = new TextField();
        nameField.setText(username); // Set default username
        nameField.setPromptText("Your Name");

        TextField emailField = new TextField();
        emailField.setText(email); // Set default email
        emailField.setPromptText("Your Email");

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Your Message");
        messageArea.setPrefRowCount(5);

        // Add fields to the grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Message:"), 0, 2);
        grid.add(messageArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable send button based on input validation
        final Button sendButton = (Button) dialog.getDialogPane().lookupButton(sendButtonType);
        sendButton.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) ->
                validateContactForm(sendButton, nameField, emailField, messageArea));
        emailField.textProperty().addListener((observable, oldValue, newValue) ->
                validateContactForm(sendButton, nameField, emailField, messageArea));
        messageArea.textProperty().addListener((observable, oldValue, newValue) ->
                validateContactForm(sendButton, nameField, emailField, messageArea));

        // Handle form submission
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                String name = nameField.getText();
                String userEmail = emailField.getText();
                String message = messageArea.getText();

                // Save the message in the database
                boolean success = databaseUserServices.saveMessage(name, userEmail, message);

                if (success) {
                    showAlert("Success", "Message sent successfully. Thank you for reaching out!");
                } else {
                    showAlert("Error", "Failed to send message. Please try again.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void validateContactForm(Button sendButton, TextField nameField, TextField emailField, TextArea messageArea) {
        boolean isFormValid = !nameField.getText().isEmpty()
                && !emailField.getText().isEmpty()
                && emailField.getText().contains("@") // Basic email validation
                && !messageArea.getText().isEmpty();
        sendButton.setDisable(!isFormValid);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
