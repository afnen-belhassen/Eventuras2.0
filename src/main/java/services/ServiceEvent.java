package services;

import entities.Event;
import utils.MyConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ServiceEvent implements IService<Event> {
    private Connection cnx;

    public ServiceEvent(){
        cnx = MyConnection.getInstance().getConnection();
    }
    @Override
    public void ajouter(Event event) throws SQLException {
        // Convert the list of activities to a comma-separated string
        String activitiesString = String.join(", ", event.getActivities());
        if (event.getDate_event() == null) {
            throw new IllegalArgumentException("La date de l'événement ne peut pas être null !");
        }

        String sql = "INSERT INTO event (title, description, date_event, location,user_id, category_id,image,price,activities) VALUES (?, ?, ?, ?, ?,?,?,?,?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, event.getTitle());
            pst.setString(2, event.getDescription());
            pst.setDate(3, new java.sql.Date(event.getDate_event().getTime()));
            pst.setString(4, event.getLocation());
            pst.setInt(5, event.getUser_id());
            pst.setInt(6, event.getCategory_id());
            pst.setString(7, event.getImage());
            if (event.getPrice() == null) {
                pst.setNull(8, java.sql.Types.DOUBLE); // Handle null price
            } else {
                pst.setDouble(8, event.getPrice());
            }
            pst.setString(9,activitiesString);
            pst.executeUpdate();
            System.out.println("Event ajoute avec succes!");
        }
    }

    @Override
    public void update(Event event) throws SQLException {
        String sql = "UPDATE event SET title = ?, description = ?, price = ?, activities = ? WHERE id_event = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, event.getTitle());
            pst.setString(2, event.getDescription());
            pst.setDouble(3, event.getPrice());
            pst.setString(4, String.join(", ", event.getActivities()));
            pst.setInt(5, event.getId_event());
            pst.executeUpdate();
            System.out.println("Event modifie avec succes!");
        }
    }
    @Override
    public void delete(Event event) throws SQLException {
        String sql = "DELETE FROM event WHERE id_event = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, event.getId_event());
        pst.executeUpdate();
        System.out.println("Event supprime avec succes!");
    }

    public ArrayList<Event> afficherAllForOrg() throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";
        Statement st = cnx.createStatement();
        st.executeQuery(sql);
        ResultSet rs = st.getResultSet();

        while (rs.next()) {
            int id = rs.getInt("id_event");
            String title = rs.getString("title");
            String description = rs.getString("description");
            Date date_event = rs.getDate("date_event");
            String location = rs.getString("location");
            int user_id = rs.getInt("user_id");
            int category_id = rs.getInt("category_id");
            double price = rs.getDouble("price");
            String activitiesString = rs.getString("activities"); // Retrieve the activities string

            // Convert the activities string to a List<String>
            List<String> activiteList = new ArrayList<>();
            if (activitiesString != null && !activitiesString.isEmpty()) {
                String[] activitiesArray = activitiesString.split(", ");
                for (String activity : activitiesArray) {
                    activiteList.add(activity.trim()); // Trim any extra spaces
                }
            }

            // Retrieve category name based on category_id
            String category_name = "";
            String categoryQuery = "SELECT name FROM categorie WHERE category_id = ?";
            PreparedStatement ps = cnx.prepareStatement(categoryQuery);
            ps.setInt(1, category_id);
            ResultSet rsCategory = ps.executeQuery();

            if (rsCategory.next()) {
                category_name = rsCategory.getString("name");
            }

            // Create the Event object with activiteList
            Event event = new Event(id, title, description, date_event, location, user_id, category_id, category_name, price, activiteList);
            events.add(event);
        }

        return events;
    }
    public Event getEventById(int eventId) throws SQLException {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Event event = new Event();
                event.setId_event(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setDate_event(rs.getDate("date_event"));
                event.setLocation(rs.getString("location"));
                event.setUser_id(rs.getInt("user_id"));
                event.setCategory_id(rs.getInt("category_id"));
                event.setImage(rs.getString("image"));
                event.setPrice(rs.getDouble("price"));

                // Split the comma-separated string into a list of activities
                String activitiesString = rs.getString("activities");
                if (activitiesString != null && !activitiesString.isEmpty()) {
                    List<String> activities = List.of(activitiesString.split(", "));
                    event.setActivities(activities);
                }

                return event;
            }
        }
        return null;
    }



    public ArrayList<Event> afficherAll() throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";
        Statement st = cnx.createStatement();
        st.executeQuery(sql);
        ResultSet rs = st.getResultSet();

        while (rs.next()) {
            // Retrieve event details
            int id_event = rs.getInt("id_event");
            String title = rs.getString("title");
            String description = rs.getString("description");
            Date date_event = rs.getDate("date_event");
            String location = rs.getString("location");
            int user_id = rs.getInt("user_id");
            int category_id = rs.getInt("category_id");
            String image = rs.getString("image");
            Double price = rs.getDouble("price");
            String activitiesString = rs.getString("activities"); // Retrieve the comma-separated activities string

            // Retrieve category name based on category_id
            String category_name = "";
            String categoryQuery = "SELECT name FROM categorie WHERE category_id = ?";
            PreparedStatement psCategory = cnx.prepareStatement(categoryQuery);
            psCategory.setInt(1, category_id);
            ResultSet rsCategory = psCategory.executeQuery();

            if (rsCategory.next()) {
                category_name = rsCategory.getString("name");
            }

            // Split the activities string into a list
            List<String> activities = new ArrayList<>();
            if (activitiesString != null && !activitiesString.isEmpty()) {
                String[] activitiesArray = activitiesString.split(", ");
                for (String activity : activitiesArray) {
                    activities.add(activity.trim()); // Trim any extra spaces
                }
            }

            // Create Event object with activities
            Event event = new Event(id_event, title, description, date_event, location, user_id, category_id, category_name, image, price);
            event.setActivities(activities); // Set the activities list

            events.add(event);
        }

        return events;
    }
    /*Pour afficher seulement les 3 derniers events dans home*/
    public ArrayList<Event> afficherLastEve() throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        String sql ="Select * from event order by creation_date desc limit 3";
        Statement st = cnx.createStatement();
        st.executeQuery(sql);
        ResultSet rs = st.getResultSet();
        while (rs.next()) {
            String title = rs.getString("title");
            String description = rs.getString("description");
            Double price = rs.getDouble("price");
            String image = rs.getString("image");
            Event event = new Event(title,description,price,image);
            events.add(event);
        }
        return events;
    }
}
