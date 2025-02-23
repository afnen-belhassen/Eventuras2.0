package gui;

import entities.Event;
import entities.Participation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import services.ServiceParticipation;

import java.util.ArrayList;
import java.util.List;

public class AjouterParticipation {

    public Button confirmer;
    public VBox activityContainer;

    public void confirmButton(ActionEvent event) {
        if (selectedActivities.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune activité sélectionnée");
            alert.setHeaderText("Veuillez sélectionner au moins une activité.");
            alert.showAndWait();
        } else {
            // Save the selected activities to the participation table
            saveParticipation();
        }
    }
    private Event event;
    private List<String> selectedActivities = new ArrayList<>(); // Stores selected activities

    public void setEvent(Event event) {
        this.event = event;
        loadActivities(); // Load activities for the selected event
    }

    private void loadActivities() {
        // Retrieve the list of activities from the event
        List<String> activities = event.getActivities();

        // Add a CheckBox for each activity
        for (String activity : activities) {
            CheckBox checkBox = new CheckBox(activity);
            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()) {
                    selectedActivities.add(activity); // Add to selected activities
                } else {
                    selectedActivities.remove(activity); // Remove from selected activities
                }
            });
            activityContainer.getChildren().add(checkBox);
        }
    }

    private void saveParticipation() {
        // Convert the list of selected activities to a comma-separated string
        String selectedActivitiesString = String.join(", ", selectedActivities);

        // Fetch the current user ID (replace 1 with the actual logged-in user ID)
        int userId = getCurrentUserId();

        // Create a Participation object
        Participation participation = new Participation();
        participation.setEvent_id(event.getId_event()); // Set the event ID
        participation.setUser_id(userId); // Set the user ID
        participation.setActivities(selectedActivitiesString); // Set the selected activities
        participation.setStatus("En cours"); // Set the status to "En cours"

        // Save the participation to the database
        ServiceParticipation serviceParticipation = new ServiceParticipation();
        try {
            serviceParticipation.ajouter(participation); // Pass the Participation object
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Votre participation a été enregistrée avec succès !");
            alert.showAndWait();

            // Close the activity selection window
            activityContainer.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'enregistrement de la participation.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    private int getCurrentUserId() {
        return 1;
    }

}

