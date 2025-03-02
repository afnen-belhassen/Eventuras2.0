package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    private static MyConnection connection;
    private Connection cnx;
    private final String URL = "jdbc:mysql://localhost:3306/eventuras";
    private final String USER = "root";
    private final String PASSWORD = "";

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                System.out.println("🔄 Reconnecting to database...");
                cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to reconnect: " + e.getMessage());
        }
        return cnx;
    }

    private MyConnection() {
        String url = "jdbc:mysql://localhost:3306/eventuras";
        String user = "root";
        String password = "";
        try {
            cnx = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion établie !");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public static MyConnection getInstance(){
        if(connection == null){
            connection= new MyConnection();
        }
        return connection;

    }

}
