package gui;


import entities.Categorie;
import entities.Event;
import entities.user;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceCategorie;
import services.ServiceEvent;
import utils.MyConnection;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AjouterEvent implements Initializable {
    public FlowPane imageContainer;
    public Button upload;
    public TextField prixEve;
    public Button GoToEvents;
    public Button profil;
    public Button Collaborations;
    public Button tickets;
    public Button Acceuil;
    public Button reclam;
    public TextField activityName;
    public Button addActivityButton;
    public Label errorDate;
    public Label errorLoc;
    public Label errorTtile;
    public Label errorDesc;
    public Label errorPrix;
    public Label errorCateg;
    public Button Next;
    @FXML
    private TextField titleEvent;
    @FXML
    private TextField descEve;
    @FXML
    private TextField locEve;
    @FXML
    private ComboBox<String> categEve;
    @FXML
    private DatePicker dateEve;
    @FXML
    private Button Valider;
    @FXML
    private ImageView imageEve;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;
    private Stage stage;
    private Scene scene;

    private final Connection cnx;
    private boolean isInitialized = false;
    private String imagePath;
    private List<File> imagesList = new ArrayList<>();
    private int currentImageIndex = 0;

    public AjouterEvent() {
        cnx = MyConnection.getInstance().getConnection();
    }
    private List<String> activities = new ArrayList<>();
    private final ServiceEvent ServiceEvent = new ServiceEvent();
    user currentUser = Session.getInstance().getCurrentUser();

    //Fonction d'ajout nécessite changement de controle de saisie sous forme de labels(instead of pop-ups)
    @FXML

    public void Ajouter(ActionEvent actionEvent) throws IOException {
        // Hide all error labels initially
        errorTtile.setVisible(false);
        errorDesc.setVisible(false);
        errorLoc.setVisible(false);
        errorDate.setVisible(false);
        errorPrix.setVisible(false);
        errorCateg.setVisible(false);

        // Clear previous error messages
        errorTtile.setText("");
        errorDesc.setText("");
        errorLoc.setText("");
        errorDate.setText("");
        errorPrix.setText("");
        errorCateg.setText("");

        String title = titleEvent.getText().trim();
        String desc = descEve.getText().trim();
        String loc = locEve.getText().trim();
        LocalDate localDate = dateEve.getValue();
        String prixText = prixEve.getText().trim();

        // Vérification des champs vides
        boolean hasError = false;

        if (title.isEmpty()) {
            errorTtile.setText("Veuillez entrer un titre.");
            errorTtile.setVisible(true); // Show the error label
            hasError = true;
        } else if (title.length() < 3 || title.length() > 50) {
            errorTtile.setText("Le titre doit contenir entre 3 et 50 caractères.");
            errorTtile.setVisible(true); // Show the error label
            hasError = true;
        }

        if (desc.isEmpty()) {
            errorDesc.setText("Veuillez entrer une description.");
            errorDesc.setVisible(true); // Show the error label
            hasError = true;
        } else if (desc.length() < 10 || desc.length() > 255) {
            errorDesc.setText("La description doit contenir entre 10 et 255 caractères.");
            errorDesc.setVisible(true); // Show the error label
            hasError = true;
        }

        if (loc.isEmpty()) {
            errorLoc.setText("Veuillez entrer une localisation.");
            errorLoc.setVisible(true); // Show the error label
            hasError = true;
        } else if (loc.length() < 3) {
            errorLoc.setText("La localisation doit contenir au moins 3 caractères.");
            errorLoc.setVisible(true); // Show the error label
            hasError = true;
        }

        if (localDate == null) {
            errorDate.setText("Veuillez sélectionner une date !");
            errorDate.setVisible(true); // Show the error label
            hasError = true;
        } else if (localDate.isBefore(LocalDate.now())) {
            errorDate.setText("La date doit être dans le futur.");
            errorDate.setVisible(true); // Show the error label
            hasError = true;
        }

        Double prix = null;
        if (!prixText.isEmpty()) {
            try {
                prix = Double.parseDouble(prixText);
            } catch (NumberFormatException e) {
                errorPrix.setText("Le prix doit être un nombre valide.");
                errorPrix.setVisible(true); // Show the error label
                hasError = true;
            }
        }

        if (hasError) {
            return; // Stop execution if there are errors
        }

        // Conversion de la date
        Date date = Date.valueOf(localDate);

        // Récupération de la catégorie
        String categoryName = categEve.getSelectionModel().getSelectedItem();
        int categoryId = -1;

        try {
            ServiceCategorie serviceCategorie = new ServiceCategorie();
            categoryId = serviceCategorie.getCategoryIdByName(categoryName);
            if (categoryId == -1) {
                errorCateg.setText("Catégorie introuvable.");
                errorCateg.setVisible(true); // Show the error label
                return; // Stop execution if category is not found
            }
        } catch (SQLException e) {
            errorCateg.setText("Erreur de récupération de la catégorie.");
            errorCateg.setVisible(true); // Show the error label
            return; // Stop execution if there's an issue with category retrieval
        }

        // Create Event object
        Event eve = new Event(title, desc, date, loc, currentUser.getId(), categoryId, imagePath, prix);
        eve.setActivities(activities);

        // Add event
        try {
            ServiceEvent.ajouter(eve);
        } catch (Exception ex) {
            errorCateg.setText("Erreur lors de l'ajout de l'événement.");
            errorCateg.setVisible(true); // Show the error label
            return; // Stop execution if adding event fails
        }

        // Confirmation and transition logic
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir réserver ?");
        alert.setContentText("Cliquez sur 'Oui' pour confirmer ou 'Annuler' pour revenir en arrière.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) Next.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            System.out.println("Réservation annulée.");
        }
    }

    //Fonction d'ouverture du dropdown list (nécessite ajout personnalisé de catégorie)
    public void Opened(ActionEvent actionEvent) {
        if (isInitialized) {
            return;
        }

        ServiceCategorie serviceCategorie = new ServiceCategorie();
        try {
            List<Categorie> categories = serviceCategorie.afficherAll();
            ObservableList<String> categoryNames = FXCollections.observableArrayList();
            for (Categorie c : categories) {
                categoryNames.add(c.getName());
            }

            // Add "Autre" to the dropdown list
            categoryNames.add("Autre");

            categEve.setItems(categoryNames);
            System.out.println("ComboBox options: " + categoryNames);

            if (!categoryNames.isEmpty()) {
                categEve.setValue(categoryNames.get(0));
            }

            isInitialized = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //to open a pop up when choosing Autre:
    private void openAddCategoryPopup() {
        System.out.println("Opening pop-up window for adding a new category..."); // Debug statement

        // Create a new dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une nouvelle catégorie");
        dialog.setHeaderText("Entrez le nom de la nouvelle catégorie :");

        // Set the button types (OK and Cancel)
        ButtonType addButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create a TextField for the new category name
        TextField categoryField = new TextField();
        categoryField.setPromptText("Nom de la catégorie");

        // Add the TextField to the dialog
        dialog.getDialogPane().setContent(new VBox(10, categoryField));

        // Enable/disable the "Ajouter" button depending on whether a category name is entered
        Node addButtonNode = dialog.getDialogPane().lookupButton(addButton);
        addButtonNode.setDisable(true);
        categoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButtonNode.setDisable(newValue.trim().isEmpty());
        });

        // Handle the result of the dialog
        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                return categoryField.getText().trim();
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(categoryName -> {
            System.out.println("New category added: " + categoryName); // Debug statement
            // Add the new category to the database and refresh the ComboBox
            addNewCategory(categoryName);
        });
    }
    private void addNewCategory(String categoryName) {
        System.out.println("Adding new category: " + categoryName); // Debug statement

        ServiceCategorie serviceCategorie = new ServiceCategorie();
        try {
            // Check if the category already exists
            if (serviceCategorie.categoryExists(categoryName)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Catégorie existante");
                alert.setContentText("Cette catégorie existe déjà.");
                alert.showAndWait();
                return;
            }

            // Add the new category to the database
            serviceCategorie.ajouter(new Categorie(categoryName));

            // Refresh the ComboBox
            refreshCategoryComboBox();

            // Select the newly added category
            categEve.getSelectionModel().select(categoryName);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("La catégorie a été ajoutée avec succès !");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
            alert.showAndWait();
        }
    }
    private void refreshCategoryComboBox() {
        ServiceCategorie serviceCategorie = new ServiceCategorie();
        try {
            // Fetch categories from the database
            List<Categorie> categories = serviceCategorie.afficherAll();
            ObservableList<String> categoryNames = FXCollections.observableArrayList();
            for (Categorie c : categories) {
                categoryNames.add(c.getName());
            }

            // Add "Autre" as a special option (not part of the database)
            categoryNames.add("Autre");

            // Set the items in the ComboBox
            categEve.setItems(categoryNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Fonction d'initialisation
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the ComboBox with categories
        refreshCategoryComboBox();

        // Set up the valueProperty listener for the ComboBox
        categEve.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("valeur changée de " + oldValue + " à " + newValue);

            // Check if "Autre" is selected
            if ("Autre".equals(newValue)) {
                // Open a pop-up window to add a new category
                openAddCategoryPopup();
            }
        });

        // Set the default date to today
        dateEve.setValue(LocalDate.now());

        // Load images
        loadImages();
    }

    //Fonction de téléchargement d'images pour le téleversement
    private void loadImages() {
        File imagesDir = new File(getClass().getResource("/ImagesEvents").getFile());
        if (imagesDir.exists() && imagesDir.isDirectory()) {
            File[] files = imagesDir.listFiles((dir, name) ->
                    name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
            );
            if (files != null) {
                imagesList = List.of(files);
                if (!imagesList.isEmpty()) {
                    currentImageIndex = 0;
                    displayImage();
                }
            }
        }
    }

    //Fonction d'affichage de l'image dans l'imageView
    private void displayImage() {
        if (!imagesList.isEmpty() && currentImageIndex >= 0 && currentImageIndex < imagesList.size()) {
            File imageFile = imagesList.get(currentImageIndex);
            imagePath = imageFile.getAbsolutePath();
            imageEve.setImage(new Image(imageFile.toURI().toString()));
        }
    }

    //BUTTON pour afficher next image
    public void showNextImage(ActionEvent actionEvent) {
        if (!imagesList.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % imagesList.size();
            displayImage();
        }
    }

    //Button pour affichage preced image
    public void showPreviousImage(ActionEvent actionEvent) {
        if (!imagesList.isEmpty()) {
            currentImageIndex = (currentImageIndex - 1 + imagesList.size()) % imagesList.size();
            displayImage();
        }
    }

    public void uploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisis une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg","*.JPG","*.PNG")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                File destDir = new File("src/main/resources/ImagesEvents");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                File destFile = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = destFile.getAbsolutePath();
                imageEve.setImage(new Image(destFile.toURI().toString()));

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Erreur de televersement d'image: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    //check ml profil to check his own events(nzid feha changes)
    public void checkEvents(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvent.fxml"));
            Parent root = loader.load();
            System.out.println("FXML");
            // Set the display mode to "list" for checkEvents
            AfficherEvent controller = loader.getController();
            profil.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("no");
        }
    }


    //display events in the events button
    public void showEvents(ActionEvent event) throws IOException {
        // Load the AfficherEvent interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEventHOME.fxml"));
        Parent root = loader.load();

        AfficherEventHOME afficherEventController = loader.getController();
        afficherEventController.showAllEvents(); // Call the method to display all events

        // Switch to the AfficherEvent scene
        stage = (Stage) GoToEvents.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
    }

    //display last 3 events in the home section
    public void showAcceuil(ActionEvent event) throws IOException {
        // Load the AfficherEvent interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEventHOME.fxml"));
        Parent root = loader.load();

        AfficherEventHOME afficherEventController = loader.getController();
        afficherEventController.showLastThreeEvents(); // Call the method to display last 3 events

        // Switch to the AfficherEvent scene
        stage = (Stage) Acceuil.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

    }

    public void ajouterActivite(ActionEvent event) {
        String name = activityName.getText(); // Get the activity name from the TextField
        if (!name.isEmpty()) {
            // Add the activity to the list
            activities.add(name);

            // Clear the TextField so the user can add another activity
            activityName.clear();

            // Optional: Show a confirmation message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Activité ajoutée");
            alert.setHeaderText("L'activité a été ajoutée avec succès !");
            alert.showAndWait();
        } else {
            // Show an alert if the TextField is empty
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champ vide");
            alert.setHeaderText("Le nom de l'activité ne peut pas être vide !");
            alert.showAndWait();
        }
    }

}



