package org.example.entities;

import java.util.Date;




public class Reservation {
    private int id;
    private int event_Id;
    private int user_Id;
    private String status;
    private Double prix;



    private Ticket ticket;

    public Reservation() {}

    public Reservation(int event_Id, int user_Id, String status, Double prix, Ticket ticket) {
        this.event_Id = 01;
        this.user_Id = 01;
        this.status = status;
        this.prix = prix;
        this.ticket = ticket;
    }

    public Reservation(String status, Double prix, Ticket ticket) {
        this.status = status;
        this.prix = prix;
        this.ticket = ticket;
    }

    public Reservation(String status, Double prix) {
        this.status = status;
        this.prix = prix;
    }

    public Reservation(int event_Id, int user_Id, String status, Double prix) {
        this.event_Id = 01;
        this.user_Id = 01;
        this.status = status;
        this.prix = prix;
    }



    public String getStatus() {
        return status;
    }

    public void setDate(String status) {
        this.status = status;
    }

    public int getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(int user_Id) {
        this.user_Id = user_Id;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvent_Id() {
        return event_Id;
    }

    public void setEvent_Id(int event_Id) {
        this.event_Id = event_Id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        if (ticket != null) {
            ticket.setReservation(this);
        }
    }


    @Override
    public String toString() {
        return "Reservation{" +
                ", Etat=" + status +
                ", prix=" + prix +
                ", ticket=" + (ticket != null ? ticket.getTicketCode() : "Aucun ticket") +
                '}';
    }
}
