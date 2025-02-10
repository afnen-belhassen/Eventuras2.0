package org.example;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.*;

public class ServiceCategorie implements IService <Categorie>{
    private Connection cnx;

    public ServiceCategorie(){
        cnx = MyConnection.getInstance().getConnection();
    }
    @Override
    public void ajouter(Categorie categorie) throws SQLException{

        String sql = "INSERT INTO categorie (name) VALUES ('" + categorie.getName() + "')";
        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
        System.out.println("Categorie ajoutee avec succes!");

    }

    @Override
    public void update(Categorie categorie) throws SQLException {
        String sql = "UPDATE categorie SET name = ? where category_id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, categorie.getName());
        pst.setInt(2, categorie.getCategory_id());
        pst.executeUpdate();
        System.out.println("Categorie modifiee avec succes!");

    }

    @Override
    public void delete(Categorie categorie) throws SQLException {
        String sql = "DELETE FROM categorie WHERE category_id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, categorie.getCategory_id());
        pst.executeUpdate();
        System.out.println("Categorie supprimee avec succes!");
    }

    @Override
    public ArrayList<Categorie> afficherAll() throws SQLException {
        ArrayList<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie";
        Statement st = cnx.createStatement();
        st.executeQuery(sql);
        ResultSet rs = st.getResultSet();
        while (rs.next()) {
            int id = rs.getInt("category_id");
            String name = rs.getString("name");
            Categorie categorie = new Categorie(id, name);
            categories.add(categorie);
        }
        return categories;

    }
}
