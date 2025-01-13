package org.courier.services;

import javafx.scene.control.Alert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return false;
    }


}
