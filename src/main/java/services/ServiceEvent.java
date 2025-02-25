package services;

import entities.Event;
import utils.MyConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;


public class ServiceEvent implements IService<Event> {
    private Connection cnx;

    public ServiceEvent(){
        cnx = MyConnection.getInstance().getConnection();
    }
    @Override
    public void ajouter(Event event) throws SQLException {
        String sql = "INSERT INTO event (title, description, date_event, location, user_id, category_id) " +
                "VALUES ('" + event.getTitle() + "', '" +
                event.getDescription() + "', '" +
                new java.sql.Date(event.getDate_event().getTime()) + "', '" +
                event.getLocation() + "', " +
                event.getUser_id() + ", " +
                event.getCategory_id() + ")";

        // Execute the SQL query
        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
        System.out.println("Event added successfully!");
    }

    @Override
    public void update(Event event) throws SQLException {
        String sql = "UPDATE event SET title = ? where id_event = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, event.getTitle());
        pst.setInt(2, event.getId_event());
        pst.executeUpdate();
    }

    @Override
    public void delete(Event event) throws SQLException {
    String sql = "DELETE FROM event WHERE id = ?";
    PreparedStatement pst = cnx.prepareStatement(sql);
    pst.setInt(1, event.getId_event());
    pst.executeUpdate();
    }

    @Override
    public ArrayList<Event> afficherAll() throws SQLException {
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
            // Retrieve category name based on category_id
            String category_name = "";
            String categoryQuery = "SELECT name FROM categorie WHERE category_id = ?";
            PreparedStatement ps = cnx.prepareStatement(categoryQuery);
            ps.setInt(1, category_id);
            ResultSet rsCategory = ps.executeQuery();

            if (rsCategory.next()) {
                category_name = rsCategory.getString("name");
            }

            Event event = new Event(id,title,description,date_event,location,user_id,category_id,category_name);
         events.add(event);
        }
        return events;
    }
}
