package org.courier.services;

import javafx.scene.control.Alert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUserServices {

    private static final String DB_URL = "jdbc:sqlite:C:/Users/NTECH/OneDrive/Desktop/CREATED DATABASES/courier.db";

    // Hash the password using SHA-256
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    // Update password in the database
    public boolean updatePasswordInDatabase(String email, String hashedPassword) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET password = ? WHERE email = ?")) {
            statement.setString(1, hashedPassword);
            statement.setString(2, email);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to update password in the database.", e);
            showAlert("Database Error", "Failed to update password in the database.");
            return false;
        }
    }

    public String fetchUserName(String email) {
//        String url = DB_URL;
        String sql = "SELECT username FROM users WHERE email = ?";
        String userName = null;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userName = rs.getString("username");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to fetch user name from the database.", e);
        }

        return userName;
    }
    // Validate user credentials in the database
    public boolean validateUser(String email, String password) {
//        String url = "jdbc:sqlite:" + DB_URL;

        String sql = "SELECT password FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");
                    String hashedEnteredPassword = hashPassword(password);


                    return hashPassword(password).equals(storedHashedPassword);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to validate user credentials in the database.", e);
        }
        return false;
    }

    public String fetchImagePathFromDatabase(String email) {
        String imagePath = null;
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement("SELECT image_path FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                imagePath = resultSet.getString("image_path");

            }
        } catch (Exception e) {
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to fetch profile image from the database.", e);
            showAlert("Database Error", "Failed to fetch profile image from the database.");
        }
        return imagePath;
    }


    public boolean updateImagePathInDatabase(String email, String image_path) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET image_path = ? WHERE email = ?")) {
            statement.setString(1, image_path);
            statement.setString(2, email);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to update profile image path in the database.", e);
            showAlert("Database Error", "Failed to update profile image path in the database.");
            return false;
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

    public boolean doesEmailExist(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to check if email exists in the database.", e);
        }
        return false;
    }

    public boolean saveAddress(int userId, String fullName, String streetAddress, String city, String state, String zipCode, String addressType) {
        String checkQuery = "SELECT COUNT(*) FROM User_Addresses WHERE user_id = ?";
        String insertQuery = "INSERT INTO User_Addresses (user_id, full_name, street_address, city, state, zip_code, address_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE User_Addresses SET full_name = ?, street_address = ?, city = ?, state = ?, zip_code = ?, address_type = ? " +
                "WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Check if the address already exists for the user
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                // Update existing address
                updateStmt.setString(1, fullName);
                updateStmt.setString(2, streetAddress);
                updateStmt.setString(3, city);
                updateStmt.setString(4, state);
                updateStmt.setString(5, zipCode);
                updateStmt.setString(6, addressType);
                updateStmt.setInt(7, userId);

                int rowsUpdated = updateStmt.executeUpdate();
                return rowsUpdated > 0;
            } else {
                // Insert new address
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, fullName);
                insertStmt.setString(3, streetAddress);
                insertStmt.setString(4, city);
                insertStmt.setString(5, state);
                insertStmt.setString(6, zipCode);
                insertStmt.setString(7, addressType);

                int rowsInserted = insertStmt.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to save address", e);
            return false;
        }
    }
    public int getUserId(String email) {
        String query = "SELECT user_id FROM users WHERE email = ?";
        int userId = -1;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to fetch user id from the database.");
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to fetch user id from the database.", e);
        }
        return userId;
    }


    public String getUserAddress(int userId) {
        String query = "SELECT full_name, street_address, city, state, zip_code, address_type " +
                "FROM User_Addresses WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Combine the address details into a single string or format them as needed
                String fullName = rs.getString("full_name");
                String streetAddress = rs.getString("street_address");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String zipCode = rs.getString("zip_code");
                String addressType = rs.getString("address_type");

                return String.format("Name: %s\nAddress: %s, %s, %s - %s\nType: %s",
                        fullName, streetAddress, city, state, zipCode, addressType);
            } else {
                return "No address found. Please go to dashboard to add your delivery address.";
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to fetch user address", e);
            return "Error retrieving address.";
        }
    }

    public boolean saveMessage(String name, String email, String message) {
        String query = "INSERT INTO ContactMessages (name, email, message, created_at) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, message);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save message");
            Logger.getLogger(DatabaseUserServices.class.getName()).log(Level.SEVERE, "Failed to save message", e);
            return false;
        }
    }



}
