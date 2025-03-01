package gui;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import entities.user;
import services.userService;
import javafx.event.ActionEvent;
import javafx.util.Callback;

public class listUser {
    public TextField searchbar_id;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private TableColumn<user, Void> actions;
    @FXML
    private TableView<user> list_user;
    @FXML
    private TableColumn<user, String> user_birthday;
    @FXML
    private TableColumn<user, String> user_email;
    @FXML
    private ListView<user> listviewusers;
    @FXML
    private TableColumn<user, String> user_phonenumber;
    @FXML
    private TableColumn<user, String> user_firstname;
    @FXML
    private TableColumn<user, String> user_lastname;
    @FXML
    private TableColumn<user, String> user_username;
    @FXML
    private TableColumn<user, String> user_gender;
    @FXML
    private TableColumn<user, String> user_picture;
    @FXML
    private TableColumn<user, Integer> user_level;
    userService userService = new userService();

    @FXML
    void add_user(ActionEvent event) throws IOException {
        mainController.loadFXML("/addUser.fxml");
    }

    @FXML
    void initialize() {
        searchbar_id.textProperty().addListener((observable, oldValue, newValue) -> refreshlist());

        // Fetch user data and ensure it's wrapped in an ObservableList
        ObservableList<user> userList = FXCollections.observableArrayList(
                userService.getallUserdata().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        // Set the ListView items
        listviewusers.setItems(userList);

        // Customize the cell factory to display user info with a delete button
        listviewusers.setCellFactory(param -> new ListCell<user>() {
            private final HBox hbox = new HBox(10);
            private final Label label = new Label();
            private final Button deleteButton = new Button("Delete");

            {
                // Style the delete button
                deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

                // Handle delete button action
                deleteButton.setOnAction(event -> {
                    user userToDelete = getItem();
                    if (userToDelete != null) {
                        showDeleteConfirmation(userToDelete);
                    }
                });

                // Add components to the HBox
                hbox.getChildren().addAll(label, deleteButton);
            }

            @Override
            protected void updateItem(user user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setGraphic(null);
                } else {
                    // Create a styled HBox to hold user information
                    HBox hbox = new HBox(20);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setPadding(new Insets(5));

                    // Display user information
                    Label usernameLabel = new Label("Username: " + (user.getUsername() != null ? user.getUsername() : "Unknown"));
                    Label firstnameLabel = new Label("First Name: " + (user.getFirstname() != null ? user.getFirstname() : "Unknown"));
                    Label lastnameLabel = new Label("Last Name: " + (user.getLastname() != null ? user.getLastname() : "Unknown"));
                    Label emailLabel = new Label("Email: " + (user.getEmail() != null ? user.getEmail() : "Unknown"));

                    // Apply some styling
                    Stream.of(usernameLabel, firstnameLabel, lastnameLabel, emailLabel).forEach(label -> {
                        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                    });

                    // Create a delete button
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");

                    // Delete button action
                    deleteButton.setOnAction(event -> showDeleteConfirmation(user));

                    // Add everything to the HBox
                    hbox.getChildren().addAll(usernameLabel, firstnameLabel, lastnameLabel, emailLabel, deleteButton);

                    setGraphic(hbox);
                }
            }

        });
    }

    // Method to show a delete confirmation dialog



    private Callback<TableColumn<user, Void>, TableCell<user, Void>> createActionsCellFactory() {
        return new Callback<TableColumn<user, Void>, TableCell<user, Void>>() {
            @Override
            public TableCell<user, Void> call(final TableColumn<user, Void> param) {
                return new TableCell<user, Void>() {
                    private final Button btnUpdate = new Button("Update");
                    private final Button btnDelete = new Button("Delete");
                    {
                        btnUpdate.setOnAction(event -> {
                            user user = getTableView().getItems().get(getIndex());
                            System.out.println(user);
                            try {
                                FXMLLoader loader = mainController.loadFXML("/updateUser.fxml");
                                updateUser updateController = loader.getController();
                                updateController.setUser(user);
                                System.out.println("Selected Personne: " + user);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        btnDelete.setOnAction(event -> {
                            user user = getTableView().getItems().get(getIndex());
                            showDeleteConfirmation(user);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            user currentUser = getTableView().getItems().get(getIndex());
                            if (currentUser != null) {
                                HBox buttonsBox = new HBox(btnUpdate, btnDelete);
                                setGraphic(buttonsBox);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        };
    }

    public void refreshlist() {
        String searchQuery = searchbar_id.getText().toLowerCase();

        ObservableList<user> updatedList = FXCollections.observableList(userService.getallUserdata());

        // Filter users by first name if search query is not empty
        ObservableList<user> filteredList = updatedList.stream()
                .filter(user -> user.getFirstname().toLowerCase().contains(searchQuery))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        listviewusers.setItems(filteredList);
    }


    private void showDeleteConfirmation(user user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete this user: " + user.getUsername() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.deleteUser(user.getId());
                String image = user.getPicture();
                if (image != null && !image.isEmpty()) {
                    Path imagePath = Paths.get("src/main/resources/images/", image);
                    try {
                        Files.deleteIfExists(imagePath);
                        System.out.println("Image deleted: " + imagePath);
                    } catch (IOException e) {
                        System.out.println("Error deleting image: " + e.getMessage());
                    }
                }
                refreshlist();
            }
        });
    }

    @FXML
    void display_charts(ActionEvent event) throws IOException {
        mainController.loadFXML("/Crudrole.fxml");
    }
}