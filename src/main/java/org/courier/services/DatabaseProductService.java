package org.courier.services;

import javafx.scene.control.Alert;
import org.courier.models.CartItem;
import org.courier.models.Product;
import org.courier.utils.SetNameEmail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProductService {
    private static final String DB_URL = "jdbc:sqlite:C:/Users/NTECH/OneDrive/Desktop/CREATED DATABASES/courier.db";

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("imagePath"),
                        rs.getInt("likes"),
                        rs.getInt("quantity")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean likeProduct(int productId) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("UPDATE products SET likes = likes + 1 WHERE product_id = ?")) {
            stmt.setInt(1, productId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLikes(int productId) {
        int likes = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT likes FROM products WHERE product_id = ?")) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                likes = rs.getInt("likes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likes;
    }

    /**
     * Add a product to the cart in the database.
     *
     * @param productId The ID of the product to add to the cart.
     * @return true if the product was successfully added, false otherwise.
     */
    public boolean addProductToCart(int productId) {
        String checkQuery = "SELECT quantity FROM cart WHERE product_id = ?";
        String updateQuery = "UPDATE cart SET quantity = quantity + 1 WHERE product_id = ?";
        String insertQuery = "INSERT INTO cart (product_id, quantity, user_email) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            // Check if the product is already in the cart
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, productId);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Product exists, update quantity
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setInt(1, productId);
                            int rowsUpdated = updateStatement.executeUpdate();
                            return rowsUpdated > 0;
                        }
                    } else {
                        // Product doesn't exist, insert new entry
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                            insertStatement.setInt(1, productId);
                            insertStatement.setInt(2, 1); // Default quantity as 1
                            insertStatement.setString(3, SetNameEmail.getEmail());
                            int rowsInserted = insertStatement.executeUpdate();
                            return rowsInserted > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding product to cart: " + e.getMessage());
            return false;
        }
    }

    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        String query = "SELECT c.product_id, p.name, p.price, c.quantity, p.imagePath FROM cart c JOIN products p ON c.product_id = p.product_id";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new CartItem(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("imagePath")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void clearCart() {
        String query = "DELETE FROM cart";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public boolean saveOrdersFromCart(String userEmail) {
        String insertOrderQuery = """
                INSERT INTO orders (product_id, quantity, user_email)
                SELECT product_id, quantity, user_email
                FROM cart
                WHERE user_email = ?
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(insertOrderQuery)) {

            // Set the userEmail parameter to filter for the logged-in user's cart
            stmt.setString(1, userEmail);


            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Remove a product completely from the cart.
     *
     * @param productId The ID of the product to remove.
     * @return true if the product was successfully removed, false otherwise.
     */
    public boolean removeFromCart(int productId) {
        String query = "DELETE FROM cart WHERE product_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing product from cart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reduce the quantity of a product in the cart by 1.
     *
     * @param productId The ID of the product whose quantity is to be reduced.
     * @return true if the quantity was successfully reduced, false otherwise.
     */
    public boolean reduceQuantityInCart(int productId) {
        String query = "UPDATE cart SET quantity = quantity - 1 WHERE product_id = ? AND quantity > 1";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error reducing quantity in cart: " + e.getMessage());
            return false;
        }
    }

    /*
    Check if the cart is empty

    * */
    public boolean isCartEmpty() {
        String query = "SELECT COUNT(*) FROM cart";  // Count the number of items in the cart

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;  // If count is 0, the cart is empty
            }
        } catch (SQLException e) {
            System.err.println("Error checking if cart is empty: " + e.getMessage());
        }

        return true;  // Default to empty if there's an error
    }

    // Method to check if a user has any orders in the database
    public boolean hasOrders(String email) {
        String query = "SELECT COUNT(*) FROM orders WHERE user_email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int orderCount = resultSet.getInt(1);
                return orderCount > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to check orders. Please try again later.");
        }
        return false;
    }

    // Method to show an alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
