package org.courier.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.courier.models.Product;
import org.courier.services.DatabaseProductService;

import java.util.List;
import java.util.Objects;

public class ProductPageController {

    @FXML
    private TilePane productGrid; // Container for dynamically loaded product cards

    private final DatabaseProductService databaseService = new DatabaseProductService();

    /**
     * Initialize the Products Page Controller
     * This method is automatically called after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        loadProducts();
    }

    @FXML
    private Button btnExit;

    /**
     * Load all products from the database and display them in the product grid.
     */
    private void loadProducts() {
        List<Product> products = databaseService.getAllProducts();

        if (products.isEmpty()) {
            showAlert("Error", "No products available to display!");
        }

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productGrid.getChildren().add(productCard);
        }
    }

    /**
     * Create a visually appealing card for a product.
     *
     * @param product The product to display.
     * @return A VBox containing the product card.
     */
    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);" +
                        "-fx-border-color: steelblue;" +
                        "-fx-border-radius: 15;"
        );
        card.setPrefWidth(250);

        // Product Image
        String resourcePath = Objects.requireNonNull(getClass().getResource(product.getImagePath())).toExternalForm();
        ImageView imageView = new ImageView(new Image(resourcePath));
        System.out.println("Image Path: " + resourcePath);
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setStyle("-fx-background-radius: 25;");
        


        // Product Name
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial Rounded MT Bold", 18));
        nameLabel.setWrapText(true);

        // Product Price
        Label priceLabel = new Label("Price: $" + product.getPrice());
        priceLabel.setFont(Font.font("Roboto", 14));
        priceLabel.setTextFill(Color.DARKGREEN);

        // Likes Count
        Label likesLabel = new Label("Likes: " + product.getLikes());
        likesLabel.setFont(Font.font("Roboto", 12));
        likesLabel.setTextFill(Color.GRAY);

        // Buttons
        Button addToCartButton = new Button("Add to Cart");
        styleButton(addToCartButton, "#0073e6");
        addToCartButton.setOnAction(e -> handleAddToCart(product));

        Button likeButton = new Button("Like ❤️");
        styleButton(likeButton, "#FF4500");
        likeButton.setOnAction(e -> handleLikeProduct(product, likesLabel));

        HBox buttons = new HBox(10, addToCartButton, likeButton);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);

        // Add all components to the card
        card.getChildren().addAll(imageView, nameLabel, priceLabel, likesLabel, buttons);
        card.setAlignment(javafx.geometry.Pos.CENTER);

        return card;
    }

    /**
     * Style buttons with a uniform theme.
     *
     * @param button The button to style.
     * @param color  The background color for the button.
     */
    private void styleButton(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 5 10;"
        );
        button.setOnMouseEntered(e -> button.setStyle("-fx-opacity: 0.8;" + button.getStyle()));
        button.setOnMouseExited(e -> button.setStyle("-fx-opacity: 1.0;" + button.getStyle()));
    }

    /**
     * Handle the "Add to Cart" button click.
     *
     * @param product The product to add to the cart.
     */
    private void handleAddToCart(Product product) {
        if (databaseService.addProductToCart(product.getId())) {
            showAlert("Success", "Added to cart: " + product.getName() + ". Proceed to checkout to view your cart and complete your purchase.");
        } else {
            showAlert("Error","Failed to add to cart. Try again.");
        }
    }

    /**
     * Handle the "Like" button click.
     *
     * @param product    The product to like.
     * @param likesLabel The label displaying the number of likes.
     */
    private void handleLikeProduct(Product product, Label likesLabel) {
        if (databaseService.likeProduct(product.getId())) {
            int updatedLikes = databaseService.getLikes(product.getId());
            likesLabel.setText("Likes: " + updatedLikes);
            showAlert("Success", "You liked " + product.getName());
        } else {
            showAlert("Error", "Failed to like the product. Try again.");
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

    public void handleProceedToCheckout(ActionEvent actionEvent) {
        handleNavigation(actionEvent, "/CartPage.fxml");
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
}
