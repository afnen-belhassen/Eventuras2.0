package org.example.gui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;



public class MainGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/posts.fxml"));
        try{
            Parent root=loader.load();
            Scene scene =new Scene(root);
            primaryStage.setTitle("Eventuras");
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch (IOException e){
            System.out.println("Erreur de l'application"+e);
        }
    }
}