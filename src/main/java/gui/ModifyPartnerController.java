package gui;

import entities.Partner;
import entities.PartnerType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.PartnerService;

import java.sql.SQLException;

public class ModifyPartnerController {

    @FXML
    private TextField nameField;

    @FXML
    private ChoiceBox<PartnerType> typeField;

    @FXML
    private TextField contactField;

    @FXML
    private TextField videoField;

    @FXML
    private Button btnUpdate;

    @FXML
    private Slider ratingSlider;

    @FXML
    private Label ratingLabel;

    private Partner currentPartner;
    private final PartnerService partnerService = new PartnerService();

    @FXML
    public void initialize() {
        typeField.setItems(FXCollections.observableArrayList(PartnerType.values()));

        // Update rating label dynamically based on slider value
        ratingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ratingLabel.setText(String.format("%.0f", newValue));  // Update the label with the slider value
        });

        btnUpdate.setOnAction(event -> updatePartner());
    }

    public void setPartner(Partner partner) {
        this.currentPartner = partner;
        nameField.setText(partner.getName());
        typeField.setValue(partner.getType());
        contactField.setText(partner.getContactInfo());
        videoField.setText(partner.getImagePath());
        ratingSlider.setValue(partner.getRating());  // Set the initial value of the rating slider
    }

    @FXML
    private void updatePartner() {
        try {
            String name = nameField.getText().trim();
            PartnerType type = typeField.getValue();
            String contactInfo = contactField.getText().trim();
            String imagePath = videoField.getText().trim(); // Change variable name to imagePath
            int rating = (int) ratingSlider.getValue(); // Get rating from the slider

            if (name.isEmpty() || type == null || contactInfo.isEmpty() || imagePath.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields.");
                return;
            }

            currentPartner.setName(name);
            currentPartner.setType(type);
            currentPartner.setContactInfo(contactInfo);
            currentPartner.setImagePath(imagePath); // Set imagePath instead of videoPath
            currentPartner.setRating(rating); // Update rating value

            partnerService.update(currentPartner);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Partner updated successfully!");

            Stage stage = (Stage) btnUpdate.getScene().getWindow();
            stage.close(); // Close the modification window

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while updating partner: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
