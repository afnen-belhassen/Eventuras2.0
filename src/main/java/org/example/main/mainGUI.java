package org.example.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.layout.AnchorPane;
import java.io.IOException;

import static javafx.application.Application.launch;


public class mainGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReservation.fxml"));
        try{
            Parent root=loader.load();
            Scene scene =new Scene(root);
            primaryStage.setTitle("Afficher Reservation");
            primaryStage.setScene(scene);
            primaryStage.show();

        }catch (IOException e){
            System.out.println("Erreur de l'application");
        }
    }
}