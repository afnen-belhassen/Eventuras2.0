package gui.GestionEvents;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class OrganisateurDashboard {


    public Button GoToEvents;
    public Button Collaborations;
    public Button tickets;
    public Button Acceuil;
    public Button reclam;
    public Button create;
    public Button forum;

    public void showAcceuil(ActionEvent event) throws IOException {
        // Load the AfficherEvent interface
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AfficherEventHOME.fxml"));
        Parent root = loader.load();


        // Switch to the AfficherEvent scene
        Stage stage = (Stage) Acceuil.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        AfficherEventHOME afficherEventController = loader.getController();
        afficherEventController.showLastThreeEvents(); // Call the method to display last 3 events
    }

    public void showEvents2(ActionEvent event) throws IOException {
        // Load the AfficherEvent interface
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AfficherEventHOME.fxml"));
        Parent root = loader.load();


        // Switch to the AfficherEvent scene
        Stage stage = (Stage) GoToEvents.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        AfficherEventHOME afficherEventController = loader.getController();
        afficherEventController.showAllEvents(); // Call the method to display all events

    }

    public void createEvent(ActionEvent event) throws IOException {
        // Load the AfficherEvent interface
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AjouterEvent.fxml"));
        Parent root = loader.load();


        // Switch to the AfficherEvent scene
        Stage stage = (Stage) create.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

    }

    public void goToForum(ActionEvent event) throws IOException {
        // Load the AfficherEvent interface
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("posts.fxml"));
        Parent root = loader.load();


        // Switch to the AfficherEvent scene
        Stage stage = (Stage) forum.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

    }
}
