package org.courier.utils;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.courier.services.DatabaseUserServices;


public class UserAddress {

    DatabaseUserServices databaseUserServices = new DatabaseUserServices();
    String email = SetNameEmail.getEmail();

    public void DeliveryAddress() {
        // Create a new dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Delivery Address");
        dialog.setHeaderText("Add or Update Your Delivery Address. We will deliver to this address.");


// Set the button types
        ButtonType saveButtonType = new ButtonType("Save Address", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

// Create a grid for the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

// Add address fields
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");

        TextField addressField = new TextField();
        addressField.setPromptText("Street Address");

        TextField cityField = new TextField();
        cityField.setPromptText("City");

        TextField stateField = new TextField();
        stateField.setPromptText("State");

        TextField zipCodeField = new TextField();
        zipCodeField.setPromptText("ZIP Code");

        ComboBox<String> addressTypeComboBox = new ComboBox<>();
        addressTypeComboBox.getItems().addAll("Home", "Work", "Other");
        addressTypeComboBox.setPromptText("Address Type");


        // Add fields to the grid
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Street Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("City:"), 0, 2);
        grid.add(cityField, 1, 2);
        grid.add(new Label("State:"), 0, 3);
        grid.add(stateField, 1, 3);
        grid.add(new Label("ZIP Code:"), 0, 4);
        grid.add(zipCodeField, 1, 4);
        grid.add(new Label("Address Type:"), 0, 5);
        grid.add(addressTypeComboBox, 1, 5);


        dialog.getDialogPane().setContent(grid);

// Enable/Disable save button based on input validation
        final Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        fullNameField.textProperty().addListener((observable, oldValue, newValue) -> validateAddressForm(saveButton, fullNameField, addressField, cityField, stateField, zipCodeField, addressTypeComboBox));

        addressField.textProperty().addListener((observable, oldValue, newValue) -> validateAddressForm(saveButton, fullNameField, addressField, cityField, stateField, zipCodeField, addressTypeComboBox));
        cityField.textProperty().addListener((observable, oldValue, newValue) -> validateAddressForm(saveButton, fullNameField, addressField, cityField, stateField, zipCodeField, addressTypeComboBox));
        stateField.textProperty().addListener((observable, oldValue, newValue) -> validateAddressForm(saveButton, fullNameField, addressField, cityField, stateField, zipCodeField, addressTypeComboBox));
        zipCodeField.textProperty().addListener((observable, oldValue, newValue) -> validateAddressForm(saveButton, fullNameField, addressField, cityField, stateField, zipCodeField, addressTypeComboBox));
        addressTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateAddressForm(saveButton, fullNameField, addressField, cityField, stateField, zipCodeField, addressTypeComboBox));

// Handle form submission
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                int userId = databaseUserServices.getUserId(email);
                String fullName = fullNameField.getText();
                String streetAddress = addressField.getText();
                String city = cityField.getText();
                String state = stateField.getText();
                String zipCode = zipCodeField.getText();
                String addressType = addressTypeComboBox.getValue();

                // Save or update the address in the database
                boolean success = databaseUserServices.saveAddress(userId, fullName, streetAddress, city, state, zipCode, addressType);

                if (success) {
                    showAlert("Success", "Address saved successfully.");
                } else {
                    showAlert("Error", "Failed to save address. Please try again.");
                }
            }
            return null;
        });

        dialog.showAndWait();

    }

    private void validateAddressForm(Button saveButton, TextField fullNameField, TextField addressField, TextField cityField, TextField stateField, TextField zipCodeField, ComboBox<String> addressTypeComboBox) {
        boolean isFormValid = !addressField.getText().isEmpty()
                && !fullNameField.getText().isEmpty()
                && !cityField.getText().isEmpty()
                && !stateField.getText().isEmpty()
                && !zipCodeField.getText().isEmpty()
                && addressTypeComboBox.getValue() != null;

        saveButton.setDisable(!isFormValid);
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
