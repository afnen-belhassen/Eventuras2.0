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

    @FXML
    private ListView<Partner> partnersList;

    String currentPartner;
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
            // Fetch data from the database
            List<Partner> partners = ps.readAll();
            ObservableList<Partner> observableList = FXCollections.observableArrayList(partners);
            partnersList.setItems(observableList);

            // Set custom cell factory to display multiple columns
            partnersList.setCellFactory(listView -> new UserControllerPartner.PartnerCell());
            partnersList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Partner>(){

                @Override
                public void changed(ObservableValue<? extends Partner> observableValue, Partner partner, Partner t1) {
                    currentPartner = partnersList.getSelectionModel().getSelectedItem().getName();
                    PartnerLabel.setText(currentPartner);
                }
            });
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void addPartner(MouseEvent mouseEvent) {
        try {
            // Load the FXML for AddPartner
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddPartner.fxml"));
            Parent root = loader.load();

            // Create a new Stage for the AddPartner window
            Stage stage = new Stage();
            stage.setTitle("Add Partner");
            stage.setScene(new Scene(root));
            stage.show();

            // Optionally, close the current stage if you want to prevent going back
            //((Stage) Ajouter.getScene().getWindow()).close(); // Uncomment this if you want to close the current window

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setContentText("Error while loading AddPartner: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void delPartner(MouseEvent mouseEvent) {
        Partner selectedPartner = partnersList.getSelectionModel().getSelectedItem();

        // Check if a partner is selected
        if (selectedPartner == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Error");
            alert.setContentText("Please select a partner to delete.");
            alert.showAndWait();
            return;
        }

        // Confirmation dialog before deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this partner?");
        confirmationAlert.setContentText("Partner: " + selectedPartner.getName());

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete the partner from the database
                    ps.delete(selectedPartner); // Ensure the delete method takes partner ID or Partner object

                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setContentText("Partner deleted successfully!");
                    successAlert.showAndWait();

                    // Refresh the partners list
                    initialize(); // Refresh the list after deletion
                } catch (SQLException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Database Error");
                    errorAlert.setContentText("Error while deleting partner: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }
    public void setPartner(MouseEvent mouseEvent) {
        Partner selectedPartner = partnersList.getSelectionModel().getSelectedItem();

        if (selectedPartner == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a partner to modify.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyPartner.fxml"));
            Parent root = loader.load();

            // Get the controller of the modification window
            ModifyPartnerController modifyController = loader.getController();
            modifyController.setPartner(selectedPartner); // Pass the selected partner to the controller

            Stage stage = new Stage();
            stage.setTitle("Modify Partner");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Error while loading ModifyPartner: " + e.getMessage());
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
            // Fetch the latest data from the database
            List<Partner> partners = ps.readAll();
            ObservableList<Partner> observableList = FXCollections.observableArrayList(partners);
            partnersList.setItems(observableList); // Update the ListView with the new data

            // Optionally clear the selection
            partnersList.getSelectionModel().clearSelection();

            showAlert(Alert.AlertType.INFORMATION, "Refresh Success", "Partner list updated successfully!");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while refreshing partners: " + e.getMessage());
        }
    }


    // Custom ListCell Class to Display Multiple Columns
    static class PartnerCell extends ListCell<Partner> {
        @FXML
        private HBox Hbox;
        @FXML
        private Label NameLabel;
        @FXML
        private Label TypeLabel;
        @FXML
        private Label ContactInfoLAbel;

        public PartnerCell() {
            // Create HBox and Labels manually
            Hbox = new HBox(20); // 20px spacing between columns
            NameLabel = new Label();
            TypeLabel = new Label();
            ContactInfoLAbel = new Label();

            Hbox.getChildren().addAll(NameLabel, TypeLabel, ContactInfoLAbel);
        }

        @Override
        protected void updateItem(Partner partner, boolean empty) {
            super.updateItem(partner, empty);
            if (empty || partner == null) {
                setText(null);
                setGraphic(null);
            } else {
                // Set text values for each column
                NameLabel.setText(partner.getName());
                TypeLabel.setText(partner.getType().toString());
                ContactInfoLAbel.setText(partner.getContactInfo());

                setGraphic(Hbox);
            }
        }


    }

}
