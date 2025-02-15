package org.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.entities.Reservation;
import org.example.entities.Ticket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AfficherReservation {
    @FXML
    private TableView<Reservation> tableReservations;
    @FXML
    private TableView<Ticket> tableTickets;
    @FXML
    private TableColumn<Reservation, String> colStatus;
    @FXML
    private TableColumn<Reservation, Double> colPrix;
    @FXML
    private TableColumn<Ticket, String> colTicketCode;
    @FXML
    private TableColumn<Ticket, String> colSeatNumber;

    private Connection connection;

    public void initialize() {
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colTicketCode.setCellValueFactory(new PropertyValueFactory<>("ticketCode"));
        colSeatNumber.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventuras", "root", "");
            loadReservations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();
        ObservableList<Ticket> tickets = FXCollections.observableArrayList();

        String sql = "SELECT r.status, r.prix, r.ticket_id, " +
                "t.ticketCode, t.seatNumber " +
                "FROM reservation r " +
                "LEFT JOIN ticket t ON r.ticket_id = t.ticket_id";



        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String status = rs.getString("status");
                Double prix = rs.getDouble("prix");
                String ticketCode = rs.getString("ticketCode");
                String seatNumber = rs.getString("seatNumber");


                reservations.add(new Reservation(status, prix));
                tickets.add(new Ticket(ticketCode, seatNumber));
            }
            tableReservations.setItems(reservations);
            tableTickets.setItems(tickets);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
