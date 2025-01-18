package org.courier.services.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminProductDatabase {
    private static final String DB_URL = "jdbc:sqlite:C:/Users/NTECH/OneDrive/Desktop/CREATED DATABASES/courier.db";

    public boolean addProduct(String name, String description, double price, int stock, String imagePath) {
        String sql = "INSERT INTO products (name, description, price, stock, imagePath) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setInt(4, stock);
            stmt.setString(5, imagePath);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
