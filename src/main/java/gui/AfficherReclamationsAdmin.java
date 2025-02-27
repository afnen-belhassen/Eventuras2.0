package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import entities.Reclamation;
import services.ReclamationService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherReclamationsAdmin implements Initializable {

    @FXML private GridPane reclamationsGrid;
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private VBox detailsPanel;
    @FXML private Label lblUser, lblSubject, lblDescription;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextArea taResponse;
    @FXML private Label lblTotal, lblPending, lblResolved, lblRejected, lblCurrent;

    private final ReclamationService rs = new ReclamationService();
    private List<Reclamation> reclamationsList;
    private Reclamation currentReclamation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbFilterStatus.setItems(FXCollections.observableArrayList("All", "En attente", "En cours", "Rejeté", "Résolu"));
        cbFilterStatus.setValue("All"); // Default filter
        cbStatus.setItems(FXCollections.observableArrayList("En attente", "Résolu", "Rejeté" , "En cours"));

        loadReclamations();
    }

    private void loadReclamations() {
        try {
            reclamationsList = rs.readAll();
            updateGrid(reclamationsList);
            updateDashboardStats();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateGrid(List<Reclamation> list) {
        reclamationsGrid.getChildren().clear();
        int column = 0, row = 0;
        for (Reclamation rec : list) {
            VBox card = createReclamationCard(rec);
            reclamationsGrid.add(card, column, row);
            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createReclamationCard(Reclamation rec) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-radius: 10; -fx-background-color: #f8f9fa;");

        Label lblId = new Label("Ticket #" + rec.getId());
        Label lblStatus = new Label("Status: " + rec.getStatus());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: " + getStatusColor(rec.getStatus()));

        Button btnDetails = new Button("Voir détails");
        btnDetails.setOnAction(event -> showDetails(rec));

        card.getChildren().addAll(lblId, new Text(rec.getSubject()), lblStatus, btnDetails);
        return card;
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "En attente" -> "#0066FFFF";
            case "Résolu" -> "green";
            case "Rejeté" -> "red";
            case "En cours" -> "#F7FF00FF" ;
            default -> "black";
        };
    }

    private void showDetails(Reclamation rec) {
        currentReclamation = rec;
        lblUser.setText("User: " + rec.getId_user());
        lblSubject.setText("Subject: " + rec.getSubject());
        lblDescription.setText("Description: " + rec.getDescription());
        cbStatus.setValue(rec.getStatus());

        detailsPanel.setVisible(true);
    }

    @FXML
    private void applyFilter() {
        String selectedStatus = cbFilterStatus.getValue();
        if (selectedStatus.equals("All")) {
            updateGrid(reclamationsList);
        } else {
            List<Reclamation> filtered = reclamationsList.stream()
                    .filter(rec -> rec.getStatus().equals(selectedStatus))
                    .toList();
            updateGrid(filtered);
        }
    }

    @FXML
    private void handleUpdateStatus() {
        if (currentReclamation == null) return;

        try {
            String newStatus = cbStatus.getValue();
            currentReclamation.setStatus(newStatus);
            rs.update(currentReclamation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Status Updated");
            alert.setContentText("The status has been successfully updated!");
            alert.showAndWait();

            loadReclamations(); // Refresh UI
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendResponse() {
        if (currentReclamation == null) return;

        String responseText = taResponse.getText();
        if (responseText.isEmpty()) return;

        System.out.println("📩 Admin response: " + responseText);
        taResponse.clear(); // Clear input after sending
    }




    @FXML
    private void closeDetailsPanel() {
        detailsPanel.setVisible(false);
    }

    private void updateDashboardStats() {
        long pending = reclamationsList.stream().filter(r -> r.getStatus().equals("En attente")).count();
        long resolved = reclamationsList.stream().filter(r -> r.getStatus().equals("Résolu")).count();
        long rejected = reclamationsList.stream().filter(r -> r.getStatus().equals("Rejeté")).count();
        long EnCours = reclamationsList.stream().filter(r -> r.getStatus().equals("En cours")).count();



        lblTotal.setText("📊 Total: " + reclamationsList.size());
        lblPending.setText("📍 En attente: " + pending);
        lblResolved.setText("✅ Résolu: " + resolved);
        lblRejected.setText("❌ Rejeté: " + rejected);
        lblCurrent.setText("⌛ En cours: " + EnCours);
    }
}