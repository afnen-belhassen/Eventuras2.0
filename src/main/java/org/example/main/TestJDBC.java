package org.example.main;

import org.example.models.Reclamation;
import org.example.services.ReclamationService;

import java.sql.SQLException;

public class TestJDBC {

    public static void main(String[] args) {
        ReclamationService rs = new ReclamationService();
        //create Reclamation r1 = new Reclamation(3,12, "19/20/2025","organizer", "report korea thanks");
        //Reclamation r1 = new Reclamation(6,"Organizer", "Report Korea Thanks");


        try {
            //rs.create(r1);
            //rs.update(r1);
            System.out.println(rs.readAll());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
}
