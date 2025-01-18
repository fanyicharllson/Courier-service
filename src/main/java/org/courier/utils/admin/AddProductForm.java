package org.courier.utils.admin;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.courier.services.admin.AdminProductDatabase;


public class AddProductForm {

    AdminProductDatabase adminProductDatabase = new AdminProductDatabase();

    ;

    public void showAddProductForm() {
        // Create a new dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details to add it to the inventory.");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add Product", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create a grid for the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add fields for product details
        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Product Description");

        TextField priceField = new TextField();
        priceField.setPromptText("Product Price");

        TextField stockField = new TextField();
        stockField.setPromptText("Stock Quantity");

        TextField imagePathField = new TextField();
        imagePathField.setPromptText("Image Path (optional)");

        // Add fields to the grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Image Path:"), 0, 4);
        grid.add(imagePathField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable add button based on input validation
        final Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) ->
                validateForm(addButton, nameField, priceField, stockField));
        priceField.textProperty().addListener((observable, oldValue, newValue) ->
                validateForm(addButton, nameField, priceField, stockField));
        stockField.textProperty().addListener((observable, oldValue, newValue) ->
                validateForm(addButton, nameField, priceField, stockField));

        // Handle form submission
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText();
                String description = descriptionField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                String imagePath = imagePathField.getText();

                // Save the product in the database
                boolean success = adminProductDatabase.addProduct(name, description, price, stock, imagePath);

                if (success) {
                    showAlert("Success", "Product added successfully!");
                } else {
                    showAlert("Error", "Failed to add product. Please try again.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void validateForm(Button addButton, TextField nameField, TextField priceField, TextField stockField) {
        boolean isFormValid = !nameField.getText().isEmpty()
                && !priceField.getText().isEmpty()
                && priceField.getText().matches("\\d+(\\.\\d+)?") // Validate price as a number
                && !stockField.getText().isEmpty()
                && stockField.getText().matches("\\d+"); // Validate stock as an integer
        addButton.setDisable(!isFormValid);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
