package org.example.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.example.entities.Reservation;
import org.example.entities.Ticket;
import org.example.services.Service;

import java.sql.SQLException;
import java.util.List;

public class AfficherReservation {

    @FXML
    private ListView<Reservation> Reservation;

    @FXML
    private ListView<Ticket> Ticket;

    @FXML
    private TextField code;

    @FXML
    private Button delete;

    private Service service = new Service();

    public void initialize() {
        try {
            loadReservations();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadReservations() throws SQLException {
        List<Reservation> reservations = service.getAllReservations();
        List<Ticket> tickets = service.getAllTickets();
        Reservation.getItems().setAll(reservations);
        Ticket.getItems().setAll(tickets);
    }

    @FXML
    public void delete(ActionEvent actionEvent) {
        String codeToDelete = this.code.getText();
        if (codeToDelete.isEmpty()) {
            showErrorAlert("Erreur", "Veuillez entrer un code de réservation valide.");
            return;
        }

        try {
            service.delete(codeToDelete);
            loadReservations();
        } catch (SQLException e) {
            showErrorAlert("Erreur lors de la suppression", e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
