package gui;

import entities.Partner;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import services.PartnerService;

import java.sql.SQLException;

public class DelPartnerController {

    @FXML
    private Label partnerNameLabel; // Label to display the name of the partner to delete

    @FXML
    private Button btnDelete; // Button to confirm deletion

    private Partner currentPartner; // The partner to be deleted

    private final PartnerService partnerService = new PartnerService();

    @FXML
    public void initialize() {
        // Initialize the UI as needed
        btnDelete.setOnAction(event -> confirmDelete());
    }

    // Set the current partner to be deleted
    public void setCurrentPartner(Partner partner) {
        this.currentPartner = partner;
        if (partner != null) {
            partnerNameLabel.setText(partner.getName()); // Display partner's name
        }
    }

    // Confirm deletion action
    private void confirmDelete() {
        if (currentPartner == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "No partner selected for deletion.");
            return;
        }

        // Confirmation dialog
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete " + currentPartner.getName() + "?");
        confirmationAlert.setContentText("This action cannot be undone!");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deletePartner(); // Proceed with deletion
            }
        });
    }

    // Delete the partner from the database
    private void deletePartner() {
        try {
            partnerService.delete(currentPartner); // Call the delete method from PartnerService

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Partner deleted successfully!");

            // Optionally, close the window or reset fields
            // Close the window if needed: ((Stage) btnDelete.getScene().getWindow()).close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while deleting partner: " + e.getMessage());
        }
    }

    // Show alert dialog
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
