package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class MyConnection {
    private static MyConnection connection;
    private Connection cnx;

    public Connection getCnx() {
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