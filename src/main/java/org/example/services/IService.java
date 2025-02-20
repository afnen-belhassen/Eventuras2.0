package org.example.services;

import org.example.entities.Reservation;
import org.example.entities.Ticket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public interface IService<T> {
    void ajouter(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(String ticketCode) throws SQLException;
    Map<Reservation, Ticket> afficherAll() throws SQLException;

}
