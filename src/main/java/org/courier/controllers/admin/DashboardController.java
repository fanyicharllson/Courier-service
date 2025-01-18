package org.courier.controllers.admin;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.courier.controllers.CartPageController;
import org.courier.utils.admin.AddProductForm;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardController {

    AddProductForm addProductForm = new AddProductForm();


    public void handleAddProductNavigation() {
        addProductForm.showAddProductForm();
    }

    public void handleNavigationToHome(ActionEvent event) {
        handleNavigation(event, "/HomePage.fxml");
    }

    public void handleLogout(ActionEvent event) {
        handleNavigation(event, "/LoginScreen.fxml");
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
            Logger.getLogger(CartPageController.class.getName()).log(Level.SEVERE, "Error loading page", e);
        }

    }

    public void handleOrdersNavigation(ActionEvent event) {
    }

    public void handleEditProductNavigation(ActionEvent event) {
    }

    public void handleDeleteProductNavigation(ActionEvent event) {
    }

    public void handleViewUsersNavigation(ActionEvent event) {
    }
}
