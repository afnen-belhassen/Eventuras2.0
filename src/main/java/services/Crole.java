package services;

import entities.Role;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Crole{
    private Connection connection;

    public Crole() {
        this.connection = MyConnection.getInstance().getCnx();
    }


    public void ajouter(Role role) throws SQLException {
        String sql = "INSERT INTO role (role_id,role_name) VALUES('"+ role.getRoleId() + "','"  + role.getRoleName()  +"')";
        Statement st = connection.createStatement();
        st.executeUpdate(sql);
    }



    public boolean updateRole(Role updatedRole) throws SQLException {
        String sql = "UPDATE Role SET role_name = ? WHERE role_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, updatedRole.getRoleName());
            pst.setInt(2, updatedRole.getRoleId());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        }
    }



    public void delete(Role role) throws SQLException {
        String sql = "DELETE FROM role WHERE role_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, role.getRoleId());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User with ID " + role.getRoleId() + " deleted successfully.");
            } else {
                System.out.println("No user found with ID " + role.getRoleId() + ".");
            }
        }
    }



    public ArrayList<Role> afficherAll() throws SQLException {
        ArrayList<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM role WHERE role_name != 'Admin'"; // Exclude the 'Admin' role
        Statement st = connection.createStatement();
        st.executeQuery(sql);
        ResultSet rs = st.getResultSet();
        while (rs.next()) {
            int id = rs.getInt("role_id");
            String fullname = rs.getString("role_name");
            Role p = new Role(id, fullname);
            roles.add(p);
        }
        return roles;
    }

    //////////////////////////////////////////////

    public boolean updateUser(Role updatedUser) throws SQLException {
        // This method is not applicable for Crole, so it can throw an UnsupportedOperationException
        throw new UnsupportedOperationException("Update user is not supported in Crole.");
    }
///////////////////////////////////////////////

    public int getRoleIdByName(String roleName) throws SQLException {
        String query = "SELECT role_id FROM role WHERE role_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roleName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("role_id");
            } else {
                throw new NoSuchElementException("Role with name '" + roleName + "' not found.");
            }
        }
    }

}
