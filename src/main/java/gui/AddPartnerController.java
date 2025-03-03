package gui;

import entities.Partner;
import entities.PartnerType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.PartnerService;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddPartnerController {

    @FXML
    private Slider ratingSlider; // Updated to Slider

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

    @FXML
    private Button btnBrowse;

    private final PartnerService partnerService = new PartnerService();
    private static final Logger LOGGER = Logger.getLogger(AddPartnerController.class.getName());

    @FXML
    public void initialize() {
        // Populate the choice box with enum values
        typeField.setItems(FXCollections.observableArrayList(PartnerType.values()));

        // Initialize rating slider (1 to 5)
        ratingSlider.setMin(1);
        ratingSlider.setMax(5);
        ratingSlider.setValue(3);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setMinorTickCount(0);
        ratingSlider.setSnapToTicks(true);

        // Set the button actions
        btnSubmit.setOnAction(event -> addPartner());
        btnBrowse.setOnAction(event -> openFileChooser());
    }

    @FXML
    private void addPartner() {
        try {
            LOGGER.info("Starting addPartner method...");

            String name = nameField.getText().trim();
            LOGGER.info("Name: " + name);

            PartnerType type = typeField.getValue();
            LOGGER.info("Type: " + type);

            String contactInfo = contactField.getText().trim();
            LOGGER.info("Contact Info: " + contactInfo);

            String imagePath = videoField.getText().trim();
            LOGGER.info("Image Path: " + imagePath);

            int rating = (int) ratingSlider.getValue(); // Retrieve rating from slider
            LOGGER.info("Rating: " + rating);

            if (name.isEmpty() || type == null || contactInfo.isEmpty() || imagePath.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Erreur de saisie", "Veuillez remplir tous les champs.");
                return;
            }

            if (!name.matches("^[a-zA-ZÀ-ÿ\\s-]+$")) {
                showAlert(Alert.AlertType.WARNING, "Erreur de saisie", "Le nom ne doit contenir que des lettres.");
                return;
            }

            if (!isValidEmail(contactInfo) && !isValidPhoneNumber(contactInfo)) {
                showAlert(Alert.AlertType.WARNING, "Erreur de saisie", "Le contact doit être une adresse e-mail valide ou un numéro de téléphone.");
                return;
            }

            if (!isValidImagePath(imagePath)) {
                showAlert(Alert.AlertType.WARNING, "Erreur de saisie", "Le fichier doit être au format .jpeg, .png ou .jpg.");
                return;
            }

            Partner newPartner = new Partner(0, name, type, contactInfo, imagePath, rating);
            LOGGER.info("Partner object created: " + newPartner);

            partnerService.create(newPartner);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Partenaire ajouté avec succès !");

            clearForm();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'enregistrement du partenaire", e);
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Une erreur est survenue lors de l'enregistrement. Détails : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^(\\+\\d{1,3}\\s?)?\\d{8,15}$");
    }

    private boolean isValidImagePath(String path) {
        return path.toLowerCase().matches(".*\\.(jpeg|png|jpg)$");
    }

    private void clearForm() {
        nameField.clear();
        typeField.setValue(null);
        contactField.clear();
        videoField.clear();
        ratingSlider.setValue(3); // Reset slider to default value
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images (*.jpeg, *.png, *.jpg)", "*.jpeg", "*.png", "*.jpg")
        );

        Stage stage = (Stage) btnBrowse.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            videoField.setText(file.getAbsolutePath());
        }
    }
}
