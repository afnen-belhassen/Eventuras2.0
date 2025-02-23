package gui;

import entities.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import services.ServiceEvent;
import utils.MyConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;

//AFFICHAGE POUR EVENTS DE L'ORGANISATEUR
public class AfficherEvent {
    public Button GoToEvents;
    public Button Collaborations;
    public Button tickets;
    public Button reclam;
    public Button Acceuil;
    public HBox headerBox;
    public Button Supprimer;
    public Button modify;
    ServiceEvent sE=new ServiceEvent();
    private final Connection cnx;
    public AfficherEvent() {
        cnx = MyConnection.getInstance().getConnection();
    }
    @FXML
    private ListView<Event> listView;
    @FXML
    void initialize() {
        try {
            // Create the column headers (titles for each "column")
            Text titleHeader = new Text("Title");
            Text descriptionHeader = new Text("Description");
            Text dateHeader = new Text("Date");
            Text locationHeader = new Text("Location");
            Text priceHeader = new Text("Price");
            Text activitiesHeader = new Text("Activities"); // Add activities header

            // Add the headers to the headerBox (HBox)
            headerBox.getChildren().addAll(titleHeader, descriptionHeader, dateHeader, locationHeader, priceHeader, activitiesHeader);
            headerBox.setSpacing(10);  // Adjust spacing between the column headers

            // Fetch the list of events from the service
            ArrayList<Event> events = sE.afficherAllForOrg(); // Fetch events with activities

            // Convert the List to an ObservableList for ListView binding
            ObservableList<Event> observableEvents = FXCollections.observableList(events);
            listView.setItems(observableEvents);

            // Customize the ListView to display the Event data
            listView.setCellFactory(new Callback<ListView<Event>, ListCell<Event>>() {
                @Override
                public ListCell<Event> call(ListView<Event> param) {
                    return new ListCell<Event>() {
                        @Override
                        protected void updateItem(Event item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setGraphic(null);
                            } else {
                                // Create a new HBox for each row (acting as columns)
                                HBox hBox = new HBox(10);  // 10px spacing between the "columns"

                                // Format date using SimpleDateFormat (optional)
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = sdf.format(item.getDate_event());

                                // Create Text nodes for each field to simulate columns
                                Text titleText = new Text(item.getTitle());
                                Text descriptionText = new Text(item.getDescription());
                                Text dateText = new Text(formattedDate);
                                Text locationText = new Text(item.getLocation());
                                Text priceText = new Text(item.getPrice() + " TND");

                                // Convert the activiteList to a comma-separated string for display
                                String activitiesString = String.join(", ", item.getActivities());
                                Text activitiesText = new Text(activitiesString); // Display activities

                                // Add the text nodes to the HBox (excluding event_id, user_id, and category_id)
                                hBox.getChildren().addAll(titleText, descriptionText, dateText, locationText, priceText, activitiesText);

                                // Set the HBox as the graphic of the ListCell
                                setGraphic(hBox);
                            }
                        }
                    };
                }
            });

        } catch (SQLException e) {
            // Handle any SQL exceptions by showing an alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void deleteEvent(ActionEvent event) {
        // Get the selected event from the ListView
        Event selectedEvent = listView.getSelectionModel().getSelectedItem();

        // Check if an event is selected
        if (selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de sélection");
            alert.setContentText("Veuillez sélectionner un événement à supprimer.");
            alert.showAndWait();
            return;
        }

        // Confirmation dialog before deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet événement ?");
        confirmationAlert.setContentText("Événement: " + selectedEvent.getTitle());

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete the event from the database
                sE.delete(selectedEvent);

                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setContentText("L'événement a été supprimé avec succès !");
                successAlert.showAndWait();

                // Refresh the ListView
                refreshListView();
            } catch (SQLException e) {
                showErrorAlert("Erreur lors de la suppression de l'événement: " + e.getMessage());
            }
        }
    }
    private void refreshListView() {
        try {
            // Fetch the updated list of events from the database
            ArrayList<Event> events = sE.afficherAllForOrg();

            // Convert the List to an ObservableList for ListView binding
            ObservableList<Event> observableEvents = FXCollections.observableList(events);
            listView.setItems(observableEvents);
        } catch (SQLException e) {
            showErrorAlert("Erreur lors du chargement des événements: " + e.getMessage());
        }
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void modifierEvent(ActionEvent event) {
        // Get the selected event from the ListView
        Event selectedEvent = listView.getSelectionModel().getSelectedItem();

        // Check if an event is selected
        if (selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de sélection");
            alert.setContentText("Veuillez sélectionner un événement à modifier.");
            alert.showAndWait();
            return;
        }

        // Create a custom dialog
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'événement");
        dialog.setHeaderText("Modifier les détails de l'événement");

        // Set the button types (OK and Cancel)
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the layout for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create text fields and list view for activities
        TextField titleField = new TextField(selectedEvent.getTitle());
        TextField descriptionField = new TextField(selectedEvent.getDescription());
        TextField priceField = new TextField(String.valueOf(selectedEvent.getPrice()));
        ListView<String> activitiesListView = new ListView<>(FXCollections.observableArrayList(selectedEvent.getActivities()));
        TextField newActivityField = new TextField();
        Button addActivityButton = new Button("Ajouter une activité");

        // Add components to the grid
        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Prix:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Activités:"), 0, 3);
        grid.add(activitiesListView, 1, 3);
        grid.add(newActivityField, 1, 4);
        grid.add(addActivityButton, 2, 4);

        // Add an event handler to the "Ajouter une activité" button
        addActivityButton.setOnAction(e -> {
            String newActivity = newActivityField.getText().trim();
            if (!newActivity.isEmpty()) {
                activitiesListView.getItems().add(newActivity);
                newActivityField.clear();
            }
        });

        // Set the grid to the dialog pane
        dialog.getDialogPane().setContent(grid);

        // Convert the result to an Event object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selectedEvent.setTitle(titleField.getText());
                selectedEvent.setDescription(descriptionField.getText());
                selectedEvent.setPrice(Double.parseDouble(priceField.getText()));
                selectedEvent.setActivities(new ArrayList<>(activitiesListView.getItems()));
                return selectedEvent;
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        Optional<Event> result = dialog.showAndWait();

        // If the user clicked "Enregistrer", update the event in the database and refresh the ListView
        result.ifPresent(updatedEvent -> {
            try {
                sE.update(updatedEvent);
                refreshListView();
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setContentText("L'événement a été modifié avec succès !");
                successAlert.showAndWait();
            } catch (SQLException e) {
                showErrorAlert("Erreur lors de la modification de l'événement: " + e.getMessage());
            }
        });
    }
}
