package org.courier.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.courier.services.DatabaseProductService;
import org.courier.services.DatabaseUserServices;
import org.courier.utils.SetNameEmail;
import org.courier.utils.UserAddress;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardController {

    DatabaseUserServices databaseUserServices = new DatabaseUserServices();
    DatabaseProductService databaseProductService = new DatabaseProductService();
    UserAddress userAddress = new UserAddress();
    private final String email = SetNameEmail.getEmail();


    @FXML
    private ImageView profileImage;

    @FXML
    private Label profileName;

    @FXML
    private HBox profileBox;

    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {
        getUserName();
        // Fetch and set profile image
        String imagePath = databaseUserServices.fetchImagePathFromDatabase(SetNameEmail.getEmail()); // Assuming userId is available
        if (imagePath == null || imagePath.isEmpty()) {
            showAlert("Error", "The profile image path is empty. Using default image.");
            // Render default template image
            profileImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/avatar.png"))));
              profileName.setText(SetNameEmail.getUserName());
        } else {
            // Validate and render the image
            File imageFile = new File(imagePath);
            if (imageFile.exists() && imageFile.isFile()) {
                profileImage.setImage(new Image(imageFile.toURI().toString()));
                profileName.setText(SetNameEmail.getUserName());
            } else {
                showAlert("Error", "The profile image path is invalid. Using default image.");
                profileImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/avatar.png"))));
                profileName.setText(SetNameEmail.getUserName());
            }
        }

        // Add a click event to the profile section
        profileBox.setOnMouseClicked(this::handleProfileNavigation);
    }

    private void handleProfileNavigation(MouseEvent mouseEvent) {
        handleRedirectMouse(mouseEvent, "/ProfilePage.fxml", "Profile");
    }

    public void getUserName() {
        String userName = SetNameEmail.getUserName();
        setUserName(userName);
    }

    public void setUserName(String userName) {
        welcomeLabel.setText("Hello, " + userName + "!" + " Welcome to Your Dashboard");
    }

    /**
     * Handles navigation to the track orders page.
     */
    @FXML
    private void handleTrackOrdersNavigation(ActionEvent event) {
        if (databaseProductService.hasOrders(email)) {
            handleRedirect(event, "/TrackOrderPage.fxml", "Dashboard");
        } else {
            showAlert("No Orders Found", "You have not placed any orders yet. Please make an order first.");
        }
    }

    /**
     * Handles navigation to the orders page.
     */
    @FXML
    private void handleOrdersNavigation(ActionEvent event) {
        handleRedirect(event, "/OrdersPage.fxml", "Orders");
    }

    /**
     * Handles navigation to the profile page.
     */
    @FXML
    private void handleProfileNavigation(ActionEvent event) {
        handleRedirect(event, "/ProfilePage.fxml", "Profile");
    }



    /**
     * Handles navigation back to home
    */
    @FXML
    private void handleNavigationToHome(ActionEvent event) {
        handleRedirect(event, "/HomePage.fxml", "Home Page");
    }

    /**
     * Handles logout action.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to log out?");
        alert.showAndWait().ifPresent(response -> {
            // Redirect to login page
            handleRedirect(event, "/LoginScreen.fxml", "Login");
        });
    }

    /**
     * Handles page redirection.
     */
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
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, "Error loading FXML file", e);
        }
    }

    private void handleRedirectMouse(MouseEvent event, String fxmlPath, String pageTitle) {
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
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, "Error redirecting to page", e);
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


    public void handleUpdateDeliveryAddress() {
        userAddress.DeliveryAddress();
    }
}
