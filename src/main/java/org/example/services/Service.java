package org.example.services;

import org.example.MyConnection;
import org.example.entities.Reservation;
import org.example.entities.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service implements IService<Reservation> {
    private Connection connection;
    public Service() {
        this.connection= MyConnection.getInstance().getCnx();
    }
    @Override
    public void ajouter(Reservation reservation) throws SQLException {



        Ticket ticket = new Ticket("TICKET-" + System.currentTimeMillis(), "A" + (int)(Math.random() * 100));
        List<Reservation> reservations = new ArrayList<>();

        Reservation reservationWithTicket = new Reservation(
                reservation.getEvent_Id(),
                reservation.getUser_Id(),
                reservation.getStatus(),
                reservation.getNbPlaces(),
                reservation.getPrix(),
                ticket
        );

        reservations.add(reservationWithTicket);

        String sqlTicket = "INSERT INTO ticket (ticketCode, seatNumber) VALUES (?, ?)";
        String sqlReservation = "INSERT INTO reservation (event_Id,user_Id,status,NbPlaces,prix,ticket_id) VALUES (?,?,?,?, ?,?)";

        try (PreparedStatement pstTicket = connection.prepareStatement(sqlTicket, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstReservation = connection.prepareStatement(sqlReservation, Statement.RETURN_GENERATED_KEYS)) {


            pstTicket.setString(1, reservationWithTicket.getTicket().getTicketCode());
            pstTicket.setString(2, reservationWithTicket.getTicket().getSeatNumber());
            pstTicket.executeUpdate();


            ResultSet rsTicket = pstTicket.getGeneratedKeys();
            if (rsTicket.next()) {
                int ticketId = rsTicket.getInt(1);
                reservationWithTicket.getTicket().setTicketId(ticketId);
            }

            pstReservation.setInt(1,reservationWithTicket.getEvent_Id());
            pstReservation.setInt(2,reservationWithTicket.getUser_Id());
            pstReservation.setString(3, reservationWithTicket.getStatus());
            pstReservation.setInt(4,reservationWithTicket.getNbPlaces());
            pstReservation.setDouble(5, reservationWithTicket.getPrix());
            pstReservation.setInt(6, reservationWithTicket.getTicket().getTicketId());
            pstReservation.executeUpdate();
        }
    }

    public void update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET prix = ? WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setDouble(1, reservation.getPrix());
            pst.setInt(2, reservation.getId());
            pst.executeUpdate();
        }
    }

    public void delete(String ticketcode) throws SQLException {
        String deleteReservationSql = "DELETE FROM reservation WHERE ticket_id = (SELECT ticket_id FROM ticket WHERE ticketCode = ? )";
        String deleteTicketSql = "DELETE FROM ticket WHERE ticketCode = ?";

        try (PreparedStatement pstReservation = connection.prepareStatement(deleteReservationSql);
             PreparedStatement pstTicket = connection.prepareStatement(deleteTicketSql)) {

            pstReservation.setString(1, ticketcode);
            pstReservation.executeUpdate();

            // Suppression du ticket lui-même
            pstTicket.setString(1, ticketcode);
            pstTicket.executeUpdate();
        }
    }





    public Map<Reservation, Ticket> afficherAll() throws SQLException {
        Map<Reservation, Ticket> reservations = new HashMap<>();
        String sql ="SELECT r.status,r.NbPlaces, r.prix, r.ticket_id, " +
                "t.ticketCode, t.seatNumber " +
                "FROM reservation r " +
                "LEFT JOIN ticket t ON r.ticket_id = t.ticket_id";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String status = rs.getString("status");
                int NbPlaces = rs.getInt("NbPlaces");
                double prix = rs.getDouble("prix");
                int ticketId = rs.getInt("ticket_id");
                String ticketCode = rs.getString("ticketCode");
                String seatNumber = rs.getString("seatNumber");

                Reservation reservation = new Reservation(status,NbPlaces, prix);
                Ticket ticket = null;

                if (ticketId > 0) {
                    ticket = new Ticket(ticketCode, seatNumber);
                    reservation.setTicket(ticket);
                }

                reservations.put(reservation, ticket);
            }
        }
        return reservations;
    }


    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                         rs.getString("status"),
                 rs.getInt("NbPlaces"),
                 rs.getDouble("prix"),
                 rs.getInt("ticket_id")

                );
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des réservations : " + e.getMessage());
        }

        return reservations;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM ticket";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getString("ticketCode"),
                        rs.getString("seatNumber")

                );
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des réservations : " + e.getMessage());
        }

        return tickets;
    }
    }

