package org.courier.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.courier.services.DatabaseUserServices;
import org.courier.utils.SetNameEmail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class TrackOrdersController {

    DatabaseUserServices databaseUserServices = new DatabaseUserServices();


    @FXML
    public ListView<String> companyDetailsListView;

    @FXML
    public ListView<String> deliveryDetailsListView;

    @FXML
    public ListView<String> reviewsListView;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label progressLabel;

    @FXML
    private ListView<String> orderTimelineListView;


    private double progress = 0.0;

    // Mock data for order tracking
    private final List<String> orderStages = Arrays.asList(
            "Order Placed",
            "Order Confirmed",
            "Dispatched",
            "In Transit",
            "Out for Delivery",
            "Delivered"
    );

    private int currentStage = 0;

    @FXML
    public void initialize() {
        populateAdditionalTabs();
        progressIndicator.setProgress(0.0);
        progressLabel.setText(orderStages.getFirst()); // Corrected to use `get(0)`
        orderTimelineListView.getItems().add("Tracking started: " + orderStages.getFirst());

        int userId = databaseUserServices.getUserId(SetNameEmail.getEmail());
        showDeliveryDetails(userId, deliveryDetailsListView);
    }

    @FXML
    private void handleShowOrders() {
        resetProgress();
        simulateOrderTracking();
    }

    private void resetProgress() {
        progress = 0.0;
        currentStage = 0;
        progressIndicator.setProgress(0.0);
        progressLabel.setText(orderStages.getFirst());
        orderTimelineListView.getItems().clear();
        orderTimelineListView.getItems().add("Tracking started: " + orderStages.getFirst());
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        handleNavigation(event, "/DashboardPage.fxml");

    }

    private void simulateOrderTracking() {
        resetProgress(); // Reset progress before starting

        // Create a Timeline to update progress periodically
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2), // Delay between updates
                event -> {
                    if (currentStage < orderStages.size()) {
                        progress = (double) (currentStage + 1) / orderStages.size();
                        progressIndicator.setProgress(progress);
                        progressLabel.setText(orderStages.get(currentStage));
                        orderTimelineListView.getItems().add("Updated: " + orderStages.get(currentStage));
                        currentStage++;
                    }
                }
        ));

        timeline.setCycleCount(orderStages.size()); // Number of updates
        timeline.setOnFinished(event -> System.out.println("Order tracking completed."));
        timeline.play();
    }



    public void handleNavigation(ActionEvent actionEvent, String location) {
        // Hide current window (Sign-Up screen)
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

        // Load and show the welcome screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Scene scene = new Scene(loader.load(), 1000, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(String.valueOf(TrackOrdersController.class)).severe("Error loading welcome screen");
        }
    }
    public void showDeliveryDetails(int userId, ListView<String> deliveryDetailsListView) {
        // Fetch user address
        String userAddress = databaseUserServices.getUserAddress(userId);

        // Clear existing items and add the fetched details
        deliveryDetailsListView.getItems().clear();
        deliveryDetailsListView.getItems().addAll(
                userAddress
        );
    }
    private void populateAdditionalTabs() {
        companyDetailsListView.getItems().addAll("Company Name: XYZ Ltd.", "Contact: support@xyz.com");
        reviewsListView.getItems().addAll("Great service!", "Delivery was on time.", "Very professional.");
    }


}
