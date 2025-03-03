package gui;

import entities.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.PartnerService;
import services.PartnershipService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class UserControllerPartner {

    private final PartnerService ps = new PartnerService();
    public Button Dashboard;
    public Button save;
    public ChoiceBox TrierPartner;

    @FXML
    private ListView<Partner> partnersList;

    private String currentPartner;
    @FXML
    private Label PartnerLabel;

    @FXML
    private Button Ajouter;

    @FXML
    private ComboBox<ContractType> partnershipTypeComboBox;
    @FXML
    private TextField descriptionField;

    @FXML
    private ImageView myImage;

    @FXML
    private Slider ratingSlider;  // Add the rating slider

    @FXML
    void initialize() {
        try {
            // Fetch data from the database
            List<Partner> partners = ps.readAll();
            ObservableList<Partner> observableList = FXCollections.observableArrayList(partners);
            partnersList.setItems(observableList);

            // Set the available sorting options in the ChoiceBox
            TrierPartner.setItems(FXCollections.observableArrayList("Name", "Rating"));
            TrierPartner.getSelectionModel().select(0);  // Default to sorting by name

            // Add listener for item selection in the ChoiceBox
            TrierPartner.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if (newValue != null) {
                        // Sort the list based on the selected option
                        sortPartners(newValue);
                    }
                }
            });

            // Initialize other components
            partnershipTypeComboBox.setItems(FXCollections.observableArrayList(ContractType.values()));
            partnersList.setCellFactory(listView -> new PartnerCell());

            partnersList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Partner>() {
                @Override
                public void changed(ObservableValue<? extends Partner> observableValue, Partner oldPartner, Partner newPartner) {
                    currentPartner = newPartner != null ? newPartner.getName() : "";
                    PartnerLabel.setText(currentPartner);
                    DisplayImage();
                    updateRatingSlider(newPartner);  // Update the rating slider when a partner is selected
                }
            });

        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    // Save the partnership along with the rating
    public void savePartnership(MouseEvent mouseEvent) {
        String description = descriptionField.getText();
        ContractType selectedType = partnershipTypeComboBox.getValue();
        Partner selectedPartner = partnersList.getSelectionModel().getSelectedItem();

        if (selectedPartner == null) {
            showAlert("No Partner Selected", "Please select a partner from the list.");
            return;
        }

        // Get the rating value from the selected cell's slider
        PartnerCell selectedCell = (PartnerCell) partnersList.getCellFactory().call(partnersList);
        double rating = selectedCell.getRatingValue();

        // Set the rating to the selected partner
        selectedPartner.setRating((int) rating);

        // Create the partnership object and save it
        Partnership partnership = new Partnership(1, selectedPartner.getId(), selectedType, description, false);

        PartnershipService partnershipService = new PartnershipService();
        try {
            partnershipService.create(partnership);
            ps.updateRating(selectedPartner.getId(), (int) rating);  // Update partner rating in the database
            showAlert("Success", "Partnership saved successfully!");
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    // Switch to dashboard
    public void DashboardLog(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/organisateurDashboard.fxml"));
        Parent root = loader.load();

        // Switch to the AfficherEvent scene
        Stage stage = (Stage) Dashboard.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    // Custom ListCell Class to Display Multiple Columns
    static class PartnerCell extends ListCell<Partner> {
        private HBox Hbox;
        private Label NameLabel;
        private Label TypeLabel;
        private Label ContactInfoLabel;
        private Slider ratingSlider;

        public PartnerCell() {
            Hbox = new HBox(20); // 20px spacing between columns
            NameLabel = new Label();
            TypeLabel = new Label();
            ContactInfoLabel = new Label();
            ratingSlider = new Slider(1.0, 5.0, 3.0); // Rating range 1 to 5

            ratingSlider.setShowTickLabels(true);
            ratingSlider.setShowTickMarks(true);
            ratingSlider.setBlockIncrement(0.5);
            ratingSlider.setMajorTickUnit(1.0);
            ratingSlider.setMinorTickCount(1);

            Hbox.getChildren().addAll(NameLabel, TypeLabel, ContactInfoLabel, ratingSlider);
        }

        @Override
        protected void updateItem(Partner partner, boolean empty) {
            super.updateItem(partner, empty);
            if (empty || partner == null) {
                setText(null);
                setGraphic(null);
            } else {
                NameLabel.setText(partner.getName());
                TypeLabel.setText(partner.getType().toString());
                ContactInfoLabel.setText(partner.getContactInfo());
                ratingSlider.setValue(partner.getRating());  // Set rating for each partner

                setGraphic(Hbox);
            }
        }

        public double getRatingValue() {
            return ratingSlider.getValue();
        }
    }

    // Display image of the selected partner
    private void DisplayImage() {
        Partner selectedPartner = partnersList.getSelectionModel().getSelectedItem();

        if (selectedPartner == null) {
            showAlert("No Partner Selected", "Please select a partner from the list.");
            return;
        }

        String imagePath = "/Images/" + selectedPartner.getImagePath(); // Ensure only filename is stored
        System.out.println("Trying to load image from: " + imagePath);

        URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl == null) {
            showAlert("Error", "Image not found at: " + imagePath);
            return;
        }

        Image image = new Image(imageUrl.toString());
        myImage.setImage(image);
    }

    // Update the rating slider when a partner is selected
    private void updateRatingSlider(Partner partner) {
        if (partner != null) {
            ratingSlider.setValue(partner.getRating());  // Assuming Partner has a getRating method
        }
    }

    // Show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Generate contract for selected partner

    @FXML
    private void generateContract() {
        // Ensure a partner is selected
        Partner selectedPartner = partnersList.getSelectionModel().getSelectedItem();
        if (selectedPartner == null) {
            showAlert("No Partner Selected", "Please select a partner from the list.");
            return;
        }

        // Retrieve partner details
        String partnerName = selectedPartner.getName();
        String contactInfo = selectedPartner.getContactInfo(); // This can be any contact info (not necessarily an email)
        String description = descriptionField.getText();

        // Validate description field
        if (description == null || description.isEmpty()) {
            showAlert("Invalid Description", "Please provide a description for the partnership.");
            return;
        }

        // Get the current stage
        Stage stage = (Stage) partnersList.getScene().getWindow();
        if (stage == null) {
            showAlert("Error", "Unable to access the current window. Please try again.");
            return;
        }

        try {
            // Generate the PDF contract
            PDFGenerator.generateContract(partnerName, selectedPartner.getType(), contactInfo, description, stage);

            // Show success message
            showAlert("Success", "Contract for " + partnerName + " generated successfully!");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            showAlert("Error", "Failed to generate the contract: " + e.getMessage());
        }
    }
    private void sortPartners(String sortBy) {
        ObservableList<Partner> partners = partnersList.getItems();

        if ("Name".equals(sortBy)) {
            // Sort by partner name (alphabetical order)
            FXCollections.sort(partners, (p1, p2) -> p1.getName().compareTo(p2.getName()));
        } else if ("Rating".equals(sortBy)) {
            // Sort by partner rating (descending order)
            FXCollections.sort(partners, (p1, p2) -> Integer.compare(p2.getRating(), p1.getRating())); // Descending order
        }

        partnersList.setItems(partners);
    }
}