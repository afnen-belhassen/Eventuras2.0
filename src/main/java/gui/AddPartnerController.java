package gui;

import entities.Partner;
import entities.PartnerType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import services.PartnerService;

import java.sql.SQLException;

public class AddPartnerController {

    @FXML
    private TextField nameField;

    @FXML
    private ChoiceBox<PartnerType> typeField;

    @FXML
    private TextField contactField;

    @FXML
    private TextField videoField;

    @FXML
    private Button btnSubmit;

    private Partner currentPartner;

    private final PartnerService partnerService = new PartnerService();

    @FXML
    public void initialize() {
        // Populate ChoiceBox with PartnerType values
        typeField.setItems(FXCollections.observableArrayList(PartnerType.values()));

        // Set button action
        btnSubmit.setOnAction(event -> addPartner());
    }
    @FXML
    private void addPartner() {
        try {
            // Get values from form
            String name = nameField.getText().trim();
            PartnerType type = typeField.getValue();
            String contactInfo = contactField.getText().trim();
            String videoPath = videoField.getText().trim();

            // Validate inputs
            if (name.isEmpty() || type == null || contactInfo.isEmpty() || videoPath.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields.");
                return;
            }

            // Create a new Partner object
            Partner newPartner = new Partner(0, name, type, contactInfo, videoPath);

            // Save to database
            PartnerService pa = new PartnerService();
            partnerService.create(newPartner);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Partner added successfully!");

            // Clear fields after adding
            clearForm();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while saving partner: " + e.getMessage());
        }
    }
    public void setPartner(Partner partner) {
        this.currentPartner = partner;
        nameField.setText(partner.getName());
        typeField.setValue(partner.getType());
        contactField.setText(partner.getContactInfo());
        videoField.setText(partner.getImagePath());
    }



    private void clearForm() {
        nameField.clear();
        typeField.setValue(null);
        contactField.clear();
        videoField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
