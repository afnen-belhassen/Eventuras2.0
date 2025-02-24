
package org.example.test;

import org.example.MyConnection;
import org.example.entities.Reservation;
import org.example.services.Service;
import org.example.entities.Ticket;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        MyConnection mc = MyConnection.getInstance();
        System.out.println(mc);
        Ticket ticket = new Ticket("TICKET-" + System.currentTimeMillis(), "A" + (int) (Math.random() * 100));
        Reservation r = new Reservation("en cours",3,12.5, ticket);
        Service s = new Service();
        try{
            System.out.println(s.afficherAll());
        }
        catch(Exception e){
            System.out.println(e);
        }

    }
}
