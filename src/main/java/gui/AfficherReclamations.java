package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import entities.Reclamation;
import services.ReclamationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class AfficherReclamations {

    private final ReclamationService rs = new ReclamationService();

    @FXML
    private GridPane reclamationsGrid; // GridPane to hold all cards

    @FXML
    void initialize() {
        refreshReclamationsDisplay();
    }


    private VBox createReclamationCard(Reclamation rec) {
        VBox card = new VBox(5);
        card.getStyleClass().add("card");
        card.setPrefWidth(200);

        Label ticketNumber = new Label("Ticket #" + rec.getId());
        ticketNumber.getStyleClass().add("ticket-number");

        Label ticketDate = new Label("Créé à: " + rec.getCreated_at());
        ticketDate.getStyleClass().add("ticket-date");

        Label ticketDescription = new Label("Description: " + rec.getDescription());
        ticketDescription.getStyleClass().add("ticket-description");

        // Action Buttons (hidden by default)
        HBox actionButtons = new HBox(10);
        Button editButton = new Button("Modifier");
        Button detailsButton = new Button("Détails");

        editButton.getStyleClass().add("modifier-btn");
        detailsButton.getStyleClass().add("details-btn");

        actionButtons.getChildren().addAll(editButton, detailsButton);
        actionButtons.setOpacity(0);

        // Show the buttons on hover
        card.setOnMouseEntered(event -> actionButtons.setOpacity(1));
        card.setOnMouseExited(event -> actionButtons.setOpacity(0));

        // "Modifier" leads to the details page
        editButton.setOnAction(event -> goToEditPage(event, rec));
        // Alternatively, "Détails" can do the same or different

        card.getChildren().addAll(ticketNumber, ticketDate, ticketDescription, actionButtons);
        return card;
    }

    public void goToAjouterPage(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));
            Parent root = loader.load();

            // Get the controller
            AjouterReclamation controller = loader.getController();
            // Pass reference of this controller so we can refresh after adding
            controller.setAfficherReclamationsController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setContentText("Error while loading add reclamation: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void goToEditPage(javafx.event.ActionEvent event, Reclamation rec) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsReclamation.fxml"));
            Parent root = loader.load();

            DetailsReclamation controller = loader.getController();
            controller.setReclamationData(rec);
            controller.setAfficherReclamationsController(this); // 🔹 Pass controller reference

            Stage stage = new Stage();
            stage.setTitle("Modifier Reclamation");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setContentText("Error while loading DetailsReclamation: " + e.getMessage());
            alert.showAndWait();
        }
    }
    public void refreshReclamationsDisplay() {
        try {
            List<Reclamation> reclamations = rs.readAll(); // Fetch updated list
            reclamationsGrid.getChildren().clear(); // Clear previous items

            int column = 0;
            int row = 0;

            for (Reclamation rec : reclamations) {
                VBox card = createReclamationCard(rec);
                reclamationsGrid.add(card, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors du chargement des réclamations: " + e.getMessage());
            alert.showAndWait();
        }
    }


    public void refreshReclamationsDisplayAfterDelete() {
        Platform.runLater(() -> { // Ensure UI update runs on JavaFX thread
            try {
                List<Reclamation> reclamations = rs.readAll(); // Fetch updated list
                reclamationsGrid.getChildren().clear(); // Clear previous items

                int column = 0;
                int row = 0;

                for (Reclamation rec : reclamations) {
                    VBox card = createReclamationCard(rec);
                    reclamationsGrid.add(card, column, row);

                    column++;
                    if (column == 3) {
                        column = 0;
                        row++;
                    }
                }

                System.out.println("✅ Reclamations successfully refreshed after delete!");

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("Erreur lors du chargement des réclamations: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }


    public void goToAjouterReservation(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamationsAdmin.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


}

