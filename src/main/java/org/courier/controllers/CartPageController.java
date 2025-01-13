package org.courier.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.courier.models.CartItem;
import org.courier.services.DatabaseProductService;
import org.courier.utils.SetNameEmail;

import java.util.List;
import java.util.Objects;

public class CartPageController {

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button btnExit;

    private DatabaseProductService databaseService = new DatabaseProductService();
    String userEmail = SetNameEmail.getEmail();

    @FXML
    public void initialize() {
        loadCartItems();
    }

    private void loadCartItems() {
        cartItemsContainer.getChildren().clear();

        if (databaseService.isCartEmpty()) {
            double totalPrice = 0;

            Label emptyCartLabel = new Label("Your cart is empty.");
            emptyCartLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: #f44336; -fx-alignment: center;");
            cartItemsContainer.getChildren().add(emptyCartLabel);

            totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));

        } else {
            List<CartItem> cartItems = databaseService.getCartItems();

            double totalPrice = 0;

            for (CartItem item : cartItems) {
                HBox cartRow = createCartRow(item);
                cartItemsContainer.getChildren().add(cartRow);
                totalPrice += item.getQuantity() * item.getPrice();
            }

            totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));

        }

    }

    private HBox createCartRow(CartItem item) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 5);");

        // Product Image
        try {
            String resourcePath = Objects.requireNonNull(getClass().getResource(item.getImagePath())).toExternalForm();
            ImageView imageView = new ImageView(new Image(resourcePath));
            imageView.setFitWidth(100);
            imageView.setFitHeight(80);
            imageView.setStyle("-fx-background-radius: 10;");
            row.getChildren().add(imageView);
        } catch (NullPointerException e) {
            System.err.println("Image not found for path: " + item.getImagePath());
        }

        // Product Details
        VBox productDetails = new VBox(5);
        productDetails.setAlignment(Pos.CENTER_LEFT);
        productDetails.setMinWidth(300); // Ensure space for product details
        VBox.setVgrow(productDetails, Priority.ALWAYS); // Make sure VBox grows

        Label productName = new Label("Product Name: " + item.getName());
        productName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #555;");
        productName.setMinWidth(200);
        productName.setPrefWidth(200);  // Add a fixed width for visibility

        Label productPrice = new Label(String.format("Price: $%.2f", item.getPrice()));
        productPrice.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        productPrice.setMinWidth(150);
        productPrice.setPrefWidth(150);  // Add a fixed width for visibility

        Label productQuantity = new Label("Quantity: " + item.getQuantity());
        productQuantity.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        productQuantity.setMinWidth(150);
        productQuantity.setPrefWidth(150);  // Add a fixed width for visibility

//        System.out.println("Product Name Label: " + productName.getText());
//        System.out.println("Product Quantity Label: " + productQuantity.getText());

        productDetails.getChildren().addAll(productName, productPrice, productQuantity);
        HBox.setHgrow(productDetails, Priority.ALWAYS); // Allow it to expand

        // Add product details to row
        row.getChildren().add(productDetails);

        // Action Buttons
        Button removeButton = new Button("Remove");
        styleActionButton(removeButton, "#FF6347");
        removeButton.setOnAction(e -> handleRemoveItem(item));

        Button reduceQuantityButton = new Button("-");
        styleActionButton(reduceQuantityButton, "#FFD700");
        reduceQuantityButton.setOnAction(e -> handleReduceQuantity(item));

        HBox actions = new HBox(10, reduceQuantityButton, removeButton);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(150);

        // Add actions to row
        row.getChildren().add(actions);

        return row;
    }


    // Helper method to style action buttons
    private void styleActionButton(Button button, String color) {
        button.setStyle("-fx-font-size: 12px; -fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", -20%); -fx-text-fill: white; -fx-font-weight: bold;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;"));
    }


    private void handleRemoveItem(CartItem item) {
        if (databaseService.removeFromCart(item.getProductId())) {
            showAlert("Removed item: " + item.getName());
            loadCartItems();
        } else {
            showAlert("Failed to remove item. Try again.");
        }
    }

    private void handleReduceQuantity(CartItem item) {
        if (item.getQuantity() > 1) {
            if (databaseService.reduceQuantityInCart(item.getProductId())) {
                loadCartItems();
            } else {
                showAlert("Failed to reduce quantity. Try again.");
            }
        } else {
            handleRemoveItem(item); // If quantity is 1, remove the item
        }
    }

    @FXML
    private void handlePayNow() {
        // Check if the cart is empty
        if (databaseService.isCartEmpty()) {
            showAlert("Your cart is empty. Add items to the cart before proceeding.");
        } else {
            // Proceed with payment if the cart is not empty
            if (databaseService.saveOrdersFromCart(userEmail)) {
                databaseService.clearCart();
                showAlert("Payment successful! Thank you for your purchase.");
                loadCartItems();  // Reload cart to update UI
            } else {
                showAlert("Payment failed. Try again.");
            }
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Action Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleNavigation(ActionEvent actionEvent, String location) {
        // Hide current window (Sign-Up screen)
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
            e.printStackTrace();
        }

    }

    public void handleHomeNavigation(ActionEvent actionEvent) {
        handleNavigation(actionEvent, "/HomePage.fxml");
    }

    public void handleDashboardNavigation(ActionEvent actionEvent) {
        handleNavigation(actionEvent, "/DashboardPage.fxml");
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

    public void handleProductNavigation(ActionEvent actionEvent) {
        handleNavigation(actionEvent, "/ProductPage.fxml");
    }
}
