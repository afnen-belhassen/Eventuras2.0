package gui;

import entities.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.ServiceEvent;
import utils.MyConnection;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

//AFFICHAGE POUR PARTICIPANT ET ORGANISATEUR
public class AfficherEventHOME  {


    public Button GoToEvents;
    public Button Collaborations;
    public Button tickets;
    public Button Acceuil;
    public Button reclam;
    public ScrollPane scrollablePane;
    public VBox eventContainer;
    public HBox sliderBox;
    private final Connection cnx;
    public AfficherEventHOME() {
        cnx = MyConnection.getInstance().getConnection();
    }
    public final ServiceEvent sE=new ServiceEvent();
    public void showLastThreeEvents() {
        // Hide the ScrollPane and show the Slider with the last 3 events
        scrollablePane.setVisible(false);
        sliderBox.setVisible(true);
        loadLastThreeEvents();
    }
    public void showAllEvents() {
        // Hide the Slider and show the ScrollPane with all events
        sliderBox.setVisible(false);
        scrollablePane.setVisible(true);
        eventContainer.setVisible(true);
        loadAllEvents();

    }
    //Acceuil
    private int currentIndex = 0; // To track the current event index

    private void loadLastThreeEvents() {
        try {
            ArrayList<Event> lastThreeEvents = sE.afficherLastEve();
            ObservableList<Event> observableList = FXCollections.observableList(lastThreeEvents);
            sliderBox.getChildren().clear();

            HBox eventContainer = new HBox(20);  // Container for the event cards
            eventContainer.setAlignment(Pos.CENTER);

            for (Event event : observableList) {
                HBox eventBox = createEventCard(event);
                eventContainer.getChildren().add(eventBox);
            }

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(eventContainer);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setPrefViewportWidth(350); // Adjust based on layout

            // Navigation buttons
            Button prevButton = new Button("◀");
            Button nextButton = new Button("▶");

            prevButton.setOnAction(e -> scrollPane.setHvalue(Math.max(0, scrollPane.getHvalue() - 0.5)));
            nextButton.setOnAction(e -> scrollPane.setHvalue(Math.min(1, scrollPane.getHvalue() + 0.5)));

            HBox navigationBox = new HBox(10, prevButton, scrollPane, nextButton);
            navigationBox.setAlignment(Pos.CENTER);

            sliderBox.getChildren().add(navigationBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createEventCard(Event event) {
        // Create the main container for the event card
        HBox eventBox = new HBox(20); // Increased spacing between elements
        eventBox.setStyle(
                "-fx-border-color: #cccccc; " + // Light gray border
                        "-fx-border-radius: 10px; " + // Rounded corners
                        "-fx-padding: 20px; " + // Increased padding
                        "-fx-background-color: #f9f9f9; " + // Light background color
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);" // Subtle shadow
        );
        eventBox.setAlignment(Pos.CENTER_LEFT);

        // Set preferred size for the HBox (event card)
        eventBox.setPrefSize(900, 350); // Increase width to 900px and height to 350px

        // Load the event image
        String imagePath = event.getImage() != null ? event.getImage().trim() : "";
        ImageView eventImage = new ImageView();

        if (!imagePath.isEmpty()) {
            try {
                String fileName = new File(imagePath).getName();
                URL imageUrl = getClass().getResource("/ImagesEvents/" + fileName);
                if (imageUrl != null) {
                    System.out.println("Loading image from: " + imageUrl.toExternalForm());
                    eventImage.setImage(new Image(imageUrl.toExternalForm()));
                } else {
                    System.out.println("Image not found: " + imagePath);
                    eventImage.setImage(new Image(getClass().getResource("/ImagesEvents/default-image.png").toExternalForm()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                eventImage.setImage(new Image(getClass().getResource("/ImagesEvents/default-image.png").toExternalForm()));
            }
        } else {
            eventImage.setImage(new Image(getClass().getResource("/ImagesEvents/default-image.png").toExternalForm()));
        }

        // Set image size
        eventImage.setFitWidth(600); // Increase image width to 600px
        eventImage.setFitHeight(300); // Increase image height to 300px
        eventImage.setPreserveRatio(true); // Maintain aspect ratio

        // Create a container for the text (title and price)
        VBox textContainer = new VBox(10); // Increased spacing between text elements
        textContainer.setAlignment(Pos.CENTER_LEFT);
        textContainer.setPrefWidth(250); // Set a preferred width for the text container

        // Style the title
        Text titleText = new Text(event.getTitle());
        titleText.setStyle(
                "-fx-font-size: 24px; " + // Larger font size
                        "-fx-font-weight: bold; " + // Bold text
                        "-fx-fill: #333333;" // Dark gray color
        );

        // Style the price
        Text priceText = new Text(event.getPrice() + " TND");
        priceText.setStyle(
                "-fx-font-size: 20px; " + // Larger font size
                        "-fx-fill: #555555;" // Medium gray color
        );

        // Add text elements to the container
        textContainer.getChildren().addAll(titleText, priceText);

        // Add a "Participer" button
        Button participerButton = new Button("Participer");
        participerButton.setStyle(
                "-fx-background-color: #4CAF50; " + // Green background
                        "-fx-text-fill: white; " + // White text
                        "-fx-font-size: 16px; " + // Larger font size
                        "-fx-padding: 10px 20px;" // Padding
        );

        // Set the button action
        participerButton.setOnAction(e -> {
            try {
                // Load the AjouterParticipation FXML file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterParticipation.fxml"));
                Parent root = loader.load();

                // Get the AjouterParticipation controller
                AjouterParticipation controller = loader.getController();

                // Pass the selected event to the AjouterParticipation controller
                controller.setEvent(event);

                // Create a new stage for the AjouterParticipation interface
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Ajouter Participation");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de l'ouverture de l'interface de participation.");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        // Add image, text container, and button to the event card
        eventBox.getChildren().addAll(eventImage, textContainer, participerButton);

        return eventBox;
    }

    private void loadAllEvents() {
        try {
            ArrayList<Event> events = sE.afficherAll(); // Fetch all events from the service
            System.out.println("Number of events fetched: " + events.size()); // Debug: Check the number of events

            ObservableList<Event> observableList = FXCollections.observableList(events);
            eventContainer.getChildren().clear(); // Clear the container before adding new cards

            for (Event event : observableList) {
                System.out.println("Adding event: " + event.getTitle()); // Debug: Check each event being added

                VBox eventCard = new VBox(15);
                eventCard.setStyle("-fx-border-color: gray; -fx-border-radius: 10px; -fx-padding: 10px;");

                // Load event image
                String imagePath = event.getImage() != null ? event.getImage().trim() : "";
                ImageView eventImage = new ImageView();

                if (!imagePath.isEmpty()) {
                    try {
                        // Extract filename from the path
                        String fileName = new File(imagePath).getName(); // This will extract "1717788736216.jpeg"
                        System.out.println("Extracted filename: " + fileName); // Debug: Check the extracted filename

                        // Load image from the resources folder
                        URL imageUrl = getClass().getResource("/ImagesEvents/" + fileName);
                        if (imageUrl != null) {
                            System.out.println("Loading image from: " + imageUrl.toExternalForm()); // Debug: Check the image URL
                            eventImage.setImage(new Image(imageUrl.toExternalForm()));
                        } else {
                            System.out.println("Image not found: " + fileName); // Debug: Image not found
                            eventImage.setImage(new Image(getClass().getResource("/ImagesEvents/default-image.png").toExternalForm()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        eventImage.setImage(new Image(getClass().getResource("/ImagesEvents/default-image.png").toExternalForm()));
                    }
                } else {
                    System.out.println("Image path is empty, using default image."); // Debug: Image path is empty
                    eventImage.setImage(new Image(getClass().getResource("/ImagesEvents/default-image.png").toExternalForm()));
                }
                eventImage.setFitWidth(200);
                eventImage.setFitHeight(150);
                eventImage.setPreserveRatio(true);

                // Event details
                Text titleText = new Text(event.getTitle());
                Text descriptionText = new Text(event.getDescription());
                Text dateText = new Text(event.getDate_event().toString());
                Text priceText = new Text(event.getPrice() + " TND");

                // Add a "Participer" button
                Button participerButton = new Button("Participer");
                participerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                participerButton.setOnAction(e -> {
                    // Navigate to AjouterParticipation and pass the selected event
                    navigateToAjouterParticipation(event);
                });

                // Add elements to the event card
                eventCard.getChildren().addAll(eventImage, titleText, descriptionText, dateText, priceText, participerButton);

                // Add the event card to the container
                eventContainer.getChildren().add(eventCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateToAjouterParticipation(Event event) {
        try {
            // Load the AjouterParticipation FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterParticipation.fxml"));
            Parent root = loader.load();

            // Get the AjouterParticipation controller
            AjouterParticipation controller = loader.getController();

            // Pass the selected event to the AjouterParticipation controller
            controller.setEvent(event);

            // Create a new stage for the AjouterParticipation interface
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Participation");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ouverture de l'interface de participation.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}






