package gui;

import entities.Partner;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.PartnerService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminControllerPartner {

    private final PartnerService ps = new PartnerService();
    public Button Actualiser;
    public Button Dashboard;

    @FXML
    private ListView<Partner> partnersList;

    private String currentPartner;

    @FXML
    private Label PartnerLabel;

    @FXML
    private Button Ajouter;
    @FXML
    private Button Supprimer;
    @FXML
    private Button Modifier;

    @FXML
    void initialize() {
        try {
            // Récupérer les données depuis la base de données
            List<Partner> partners = ps.readAll();
            ObservableList<Partner> observableList = FXCollections.observableArrayList(partners);
            partnersList.setItems(observableList);

            // Définir une cellule personnalisée pour afficher plusieurs colonnes
            partnersList.setCellFactory(listView -> new PartnerCell());

            // Adding listener for selection changes
            partnersList.getSelectionModel().selectedItemProperty().addListener((observableValue, partner, t1) -> {
                if (partnersList.getSelectionModel().getSelectedItem() != null) {
                    currentPartner = partnersList.getSelectionModel().getSelectedItem().getName();
                    PartnerLabel.setText(currentPartner);
                }
            });

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    public void addPartner(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddPartner.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Partenaire");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Erreur lors du chargement de la fenêtre d'ajout : " + e.getMessage());
        }
    }

    public void delPartner(MouseEvent mouseEvent) {
        Partner selectedPartner = partnersList.getSelectionModel().getSelectedItem();

        if (selectedPartner == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur de sélection", "Veuillez sélectionner un partenaire à supprimer.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce partenaire ?");
        confirmationAlert.setContentText("Partenaire : " + selectedPartner.getName());

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ps.delete(selectedPartner);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Partenaire supprimé avec succès !");
                    initialize(); // Rafraîchir la liste après suppression
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    public void setPartner(MouseEvent mouseEvent) {
        Partner selectedPartner = partnersList.getSelectionModel().getSelectedItem();

        if (selectedPartner == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur de sélection", "Veuillez sélectionner un partenaire à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyPartner.fxml"));
            Parent root = loader.load();

            ModifyPartnerController modifyController = loader.getController();
            modifyController.setPartner(selectedPartner);

            Stage stage = new Stage();
            stage.setTitle("Modifier le Partenaire");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Erreur lors du chargement de la fenêtre de modification : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void Refresh(MouseEvent mouseEvent) {
        try {
            List<Partner> partners = ps.readAll();
            ObservableList<Partner> observableList = FXCollections.observableArrayList(partners);
            partnersList.setItems(observableList);
            partnersList.getSelectionModel().clearSelection();

            showAlert(Alert.AlertType.INFORMATION, "Mise à jour réussie", "Liste des partenaires actualisée avec succès !");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Erreur lors de l'actualisation : " + e.getMessage());
        }
    }

    public void DashboardReturn(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminDashboard.fxml"));
        Parent root = loader.load();

        // Switch to the AfficherEvent scene
        Stage stage = (Stage) Dashboard.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    // Classe personnalisée pour afficher plusieurs colonnes
    static class PartnerCell extends ListCell<Partner> {
        @FXML
        private HBox Hbox;
        @FXML
        private Label NameLabel;
        @FXML
        private Label TypeLabel;
        @FXML
        private Label ContactInfoLabel;
        @FXML
        private Slider ratingSlider;

        public PartnerCell() {
            Hbox = new HBox(20); // Espacement de 20px entre les colonnes
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

        public double getRating() {
            return ratingSlider.getValue();
        }
    }
}
