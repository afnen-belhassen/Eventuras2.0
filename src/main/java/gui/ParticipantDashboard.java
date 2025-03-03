package gui;
import javafx.animation.*;
import entities.Event;
import entities.user;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Session;

import java.io.IOException;
import java.net.URL;
import java.security.cert.PolicyNode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ParticipantDashboard {

    public Button GoToEvents1;
    public Button Collaborations;
    public Button tickets;
    public Button Acceuil;
    public Button reclam;
    public Text scrolling_text;
    public Pane news_pane;
    private Scene scene;
    user CurrentUser = Session.getInstance().getCurrentUser();

  void initialize(){
      animateScrollingText();
  }
    public void showEvents1(ActionEvent event) throws IOException {  // Load the AfficherEvent interface
        // Load the AfficherEvent interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEventHOME.fxml"));
        Parent root = loader.load();

        AfficherEventHOME afficherEventController = loader.getController();
        afficherEventController.showAllEvents(); // Call the method to display all events

        // Switch to the AfficherEvent scene
        Stage stage = (Stage) GoToEvents1.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
    }

    public void showAcceuil1(ActionEvent event) throws IOException {
        // Load the AfficherEvent interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEventHOME.fxml"));
        Parent root = loader.load();

        AfficherEventHOME afficherEventController = loader.getController();
        afficherEventController.showLastThreeEvents(); // Call the method to display last 3 events

        // Switch to the AfficherEvent scene
        Stage stage = (Stage) Acceuil.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
    }

    private void animateScrollingText() {
        String newsText = "° Welcome back!: " + CurrentUser.getFirstname() + " " + CurrentUser.getLastname() + " hetheka houwa °";
        scrolling_text.setText(newsText);
        Rectangle clip = new Rectangle(1027, 60);
        news_pane.setClip(clip);
        news_pane.layout();
        double textWidth = scrolling_text.getLayoutBounds().getWidth();
        int animationDurationMillis = (int) (textWidth * 20);
        TranslateTransition transitionOut = new TranslateTransition(Duration.millis(animationDurationMillis), scrolling_text);
        transitionOut.setByX(-textWidth);
        TranslateTransition transitionIn = new TranslateTransition(Duration.ZERO, scrolling_text);
        transitionIn.setByX(1027);
        SequentialTransition sequentialTransition = new SequentialTransition(transitionOut, transitionIn);
        sequentialTransition.setCycleCount(SequentialTransition.INDEFINITE);
        sequentialTransition.play();
    }

    public void ParticipantDashboard1(MouseEvent mouseEvent) throws IOException {
        // Load the new scene (ParticipPartner.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipPartner.fxml"));
        Parent root = loader.load();

        // Create a new stage for the new scene
        Stage newStage = new Stage();
        Scene newScene = new Scene(root);
        newStage.setScene(newScene);

        // Set the title for the new window (optional)
        newStage.setTitle("Collaborations");

        // Show the new stage (window)
        newStage.show();

        // Add a close request handler to return to the previous scene (ParticipantDashboard.fxml)
        newStage.setOnCloseRequest(event -> {
            // When the new stage is closed, you can add logic to handle the closure if needed
            System.out.println("Collaborations window closed");
        });
    }

}



