package org.courier.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import org.courier.utils.SetNameEmail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrackOrdersController {

    private static final String DB_PATH = "C:/Users/NTECH/OneDrive/Desktop/CREATED DATABASES/courier.db"; // Update path

    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ListView<String> trackOrdersListView;
    @FXML
    private ListView<String> companyDetailsListView;
    @FXML
    private ListView<String> hubRatesListView;
    @FXML
    private ListView<String> deliveryDetailsListView;
    @FXML
    private ListView<String> reviewsListView;

    @FXML
    private void initialize() {
        String userEmail = SetNameEmail.getEmail();
//        loadOrderStatus(userEmail);
    }

    @FXML
    private void handleShowOrders(ActionEvent event) {
        String userEmail = SetNameEmail.getEmail();
        loadOrderStatus(userEmail);
    }


    private void loadOrderStatus(String userEmail) {
        showProgress(true);
        Task<Void> task = createTask(() -> fetchOrders(userEmail), "Failed to load orders.");
        executeTask(task);
    }

    private void fetchOrders(String userEmail) {
        String sql = """
                SELECT o.order_id, p.name AS product_name, o.quantity, o.status, p.product_id
                FROM orders o
                JOIN products p ON o.product_id = p.product_id
                WHERE o.user_email = ?
                """;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                Platform.runLater(() -> trackOrdersListView.getItems().clear());
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    int productId = rs.getInt("product_id");
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    String status = rs.getString("status");

                    String orderInfo = "Order ID: " + orderId +
                            "\nProduct: " + productName +
                            "\nQuantity: " + quantity +
                            "\nStatus: " + status;
                    Platform.runLater(() -> trackOrdersListView.getItems().add(orderInfo));

                    // Load additional details related to this order
                    loadCompanyDetailsForProduct(productId);
                    loadDeliveryDetailsForOrder(orderId);
                    loadHubRatesForOrder(orderId);
                    loadReviewsForOrder(orderId);
                }
                Platform.runLater(() -> {
                    if (trackOrdersListView.getItems().isEmpty()) {
                        trackOrdersListView.getItems().add("No orders found.");
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Database error while fetching orders."));
        }
    }

    private void loadCompanyDetailsForProduct(int productId) {
        String sql = "SELECT c.name, c.address, c.contact FROM Company_Details c " +
                "JOIN products p ON p.product_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                Platform.runLater(() -> companyDetailsListView.getItems().clear());
                while (rs.next()) {
                    String companyDetails = "Name: " + rs.getString("name") +
                            "\nAddress: " + rs.getString("address") +
                            "\nContact: " + rs.getString("contact");
                    Platform.runLater(() -> companyDetailsListView.getItems().add(companyDetails));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to load company details."));
        }
    }

    private void loadDeliveryDetailsForOrder(int orderId) {
        String sql = "SELECT delivery_status, delivery_date, hub_id FROM Delivery_Details WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                Platform.runLater(() -> deliveryDetailsListView.getItems().clear());
                while (rs.next()) {
                    String deliveryDetails = "Status: " + rs.getString("delivery_status") +
                            "\nDate: " + rs.getString("delivery_date") +
                            "\nHub ID: " + rs.getInt("hub_id");
                    Platform.runLater(() -> deliveryDetailsListView.getItems().add(deliveryDetails));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to load delivery details."));
        }
    }

    private void loadHubRatesForOrder(int orderId) {
        String sql = "SELECT h.location, h.rate_per_km FROM Hub_Rates h " +
                "JOIN Delivery_Details d ON d.hub_id = h.hub_id WHERE d.order_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                Platform.runLater(() -> hubRatesListView.getItems().clear());
                while (rs.next()) {
                    String hubRate = "Location: " + rs.getString("location") +
                            "\nRate per km: " + rs.getDouble("rate_per_km");
                    Platform.runLater(() -> hubRatesListView.getItems().add(hubRate));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to load hub rates."));
        }
    }

    private void loadReviewsForOrder(int orderId) {
        String sql = "SELECT r.rating, r.comments, u.username FROM Reviews r " +
                "JOIN users u ON r.user_email = u.email WHERE r.order_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                Platform.runLater(() -> reviewsListView.getItems().clear());
                while (rs.next()) {
                    String review = "User: " + rs.getString("username") +
                            "\nRating: " + rs.getInt("rating") +
                            "\nComments: " + rs.getString("comments");
                    Platform.runLater(() -> reviewsListView.getItems().add(review));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to load reviews."));
        }
    }

    private Task<Void> createTask(Runnable action, String errorMessage) {
        return new Task<>() {
            @Override
            protected Void call() {
                try {
                    action.run();
                } catch (Exception e) {
                    Platform.runLater(() -> showAlert("Error", errorMessage));
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private void executeTask(Task<Void> task) {
        task.setOnSucceeded(event -> showProgress(false));
        task.setOnFailed(event -> showProgress(false));
        new Thread(task).start();
    }

    private void showProgress(boolean visible) {
        if (visible) {
            progressBar.setVisible(true);
            progressIndicator.setVisible(true);

            // Add a delay before hiding the progress indicator
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Delay for 2 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressIndicator.setVisible(false);
                });
            }).start();
        } else {
            progressBar.setVisible(false);
            progressIndicator.setVisible(false);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleBackToDashboard(ActionEvent event) {
        navigateTo(event, "/DashboardPage.fxml");
    }

    private void navigateTo(ActionEvent event, String location) {
        Node source = (Node) event.getSource();
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
        stage.close();

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(location));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 1000, 700);
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
