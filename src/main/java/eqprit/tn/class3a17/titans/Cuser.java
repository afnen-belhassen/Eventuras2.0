package eqprit.tn.class3a17.titans;
import java.sql.*;
import java.util.ArrayList;

public class Cuser implements Iuser<user> {
    private Connection connection;
    public Cuser() {
        this.connection=MyConnection.getInstance().getCnx();
    }
    @Override
    public void ajouter(user user) throws SQLException {
        String sql = "INSERT INTO users (id,fullname,email,password,phone,role,PMR) VALUES('"+ user.getId() + "','" + user.getFullname() + "','" + user.getEmail() + "','" + user.getPassword() + "','" + user.getPhone() + "','"+ user.getRole() + "','" + user.getPMR()  +"')";
        Statement st = connection.createStatement();
        st.executeUpdate(sql);


    }

    @Override
    public boolean updateUser(user updatedUser) throws SQLException {
        String sql = "UPDATE users SET fullname = ?, password = ?, email = ?, role = ?, PMR = ?, phone = ? WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, updatedUser.getFullname());
            pst.setString(2, updatedUser.getPassword());
            pst.setString(3, updatedUser.getEmail());
            pst.setString(4, updatedUser.getRole());
            pst.setString(5, updatedUser.getPMR());
            pst.setInt(6, updatedUser.getPhone());
            pst.setInt(7, updatedUser.getId());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public void delete(user user) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, user.getId());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User with ID " + user.getId() + " deleted successfully.");
            } else {
                System.out.println("No user found with ID " + user.getId() + ".");
            }
        }
    }

    @Override
    public ArrayList<user> afficherAll() throws SQLException {
        ArrayList<user> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Statement st = connection.createStatement();
        st.executeQuery(sql);
        ResultSet rs = st.getResultSet();
        while (rs.next()) {
            int id = rs.getInt("id");
            String fullname = rs.getString("fullname");
            String password = rs.getString("password");
            String email = rs.getString("email");
            String role = rs.getString("role");
            String PMR = rs.getString("PMR");
           int phone = rs.getInt("phone");;
            user p = new user( phone,fullname,password,email,role,PMR, id);
            users.add(p);
        }
        return users;
    }
}
