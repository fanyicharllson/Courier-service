package org.courier.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.courier.utils.SetNameEmail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersPageController {

    @FXML
    private Label ordersLabel;

    @FXML
    private ListView<String> ordersListView; // A list view to display orders

    private final String userEmail = SetNameEmail.getEmail();
    private final String userName = SetNameEmail.getUserName();

    @FXML
    private void initialize() {
        ordersLabel.setText("Hey " + userName + "!" + " ,here are your recent orders");
        loadUserOrders(userEmail);
    }

    private static final String DB_PATH = "C:/Users/NTECH/OneDrive/Desktop/CREATED DATABASES/courier.db";

    private void loadUserOrders(String userEmail) {
        String url = "jdbc:sqlite:" + DB_PATH;
        String sql = """
        SELECT o.order_id, o.quantity, o.order_date, p.name AS product_name, p.price AS product_price\s
        FROM orders o
        JOIN products p ON o.product_id = p.product_id
        WHERE o.user_email = ?
       \s""";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userEmail); // Pass the logged-in user's email

            try (ResultSet rs = pstmt.executeQuery()) {
                ordersListView.getItems().clear(); // Clear previous orders, if any
                while (rs.next()) {

                    int quantity = rs.getInt("quantity");
                    double productPrice = rs.getDouble("product_price");
                    double totalPrice = quantity * productPrice;


                    String order = "Order ID: " + rs.getInt("order_id") +
                            "\nProduct: " + rs.getString("product_name") +
                            "\nQuantity: " + rs.getInt("quantity") +
                            "\nPrice per item: " + String.format("%.2f", productPrice) +
                            "\nTotal price: " + String.format("%.2f", totalPrice) +
                            "\nDate: " + rs.getString("order_date");
                    ordersListView.getItems().add(order); // Add order to the list view
                }
                if (ordersListView.getItems().isEmpty()) {
                    ordersListView.getItems().add("No orders have been made yet.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ordersListView.getItems().add("Error loading orders.");
        }
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


    public void handleBackToDashboard(ActionEvent event) {
        handleNavigation(event, "/DashboardPage.fxml");
    }

    public void handleTrackOrder(ActionEvent event) {
        System.out.println("Track Order");
    }
}
