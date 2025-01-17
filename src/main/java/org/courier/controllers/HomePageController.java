package org.courier.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.courier.utils.ContactForm;

public class HomePageController {

    ContactForm contactForm = new ContactForm();

    @FXML
    private Button exitButton;

    @FXML
    private Label userNameLabel;


    @FXML
    private Label statusLabel;

    public void setUserName(String userName) {
        userNameLabel.setText("Hello " + userName + "!");
    }

    private void handleRedirect(ActionEvent event, String location, String title) {
        statusLabel.setText(title + "...");
        System.out.println(title + "...");
        Node source = (Node) event.getSource();
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
        stage.close();

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(location));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 1000, 700);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Handles navigation to the dashboard.
     */
    @FXML
    private void handleDashboardNavigation(ActionEvent event) {
        handleRedirect(event, "/DashboardPage.fxml", "Dashboard");
    }

    /**
     * Handles the action for browsing products (via left navigation or center button).
     */
    @FXML
    private void handleBrowseProducts(ActionEvent event) {
        handleRedirect(event, "/ProductPage.fxml", "Browse Products");
    }

    /**
     * Handles the action for tracking orders (via left navigation or center button).
     */
    @FXML
    private void handleTrackOrders(ActionEvent event) {
        statusLabel.setText("Tracking Orders...");
        System.out.println("Tracking Orders...");
        // Logic for loading the tracking view
    }

    /**
     * Handles the action for viewing feedback and reviews.
     */
    @FXML
    private void handleFeedback(ActionEvent event) {
        statusLabel.setText("Opening Feedback & Reviews...");
        System.out.println("Opening Feedback & Reviews...");
        // Logic for loading feedback view
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
                Stage stage = (Stage) exitButton.getScene().getWindow();
                stage.close();
            }
        });
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        System.out.println("HomePageController initialized.");
    }

    public void handleContactUs() {
        contactForm.showContactForm();
    }
}
