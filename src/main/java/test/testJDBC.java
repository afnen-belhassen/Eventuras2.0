package test;

import entities.user;
import services.userService;

import java.io.IOException;
import java.sql.SQLException;

public class testJDBC {

    public static void main(String[] args) {
        userService rs = new userService();
        user user1 = new user("john_doe", "john@example.com", "securePassword123", "John", "Doe", "1990-05-15", "Male",
                "profile.jpg", "123-456-7890");

        //Reclamation r1 = new Reclamation(6,"Organizer", "Report Korea Thanks");


        try {
            rs.addUser(user1);
            //rs.create(r1);
            //rs.update(r1);
           // System.out.println(rs.readAll());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
