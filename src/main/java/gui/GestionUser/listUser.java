package gui.GestionUser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import gui.mainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import entities.user;
import services.userService;
import javafx.event.ActionEvent;
import javafx.util.Callback;

public class listUser {
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
    private ListView<String> listviewusers;
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
        ObservableList<user> list = FXCollections.observableList(userService.getallUserdata());

        // Existing TableView configuration
        user_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        user_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        user_phonenumber.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
        user_firstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        user_lastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        user_birthday.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        user_gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        user_level.setCellValueFactory(new PropertyValueFactory<>("level"));
        user_picture.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    try {
                        URL imageUrl = getClass().getResource("/images/" + getTableView().getItems().get(getIndex()).getPicture());
                        if (imageUrl != null) {
                            Image image = new Image(imageUrl.toExternalForm());
                            imageView.setImage(image);
                            imageView.setFitWidth(140);
                            imageView.setFitHeight(140);
                            setGraphic(imageView);
                        } else {
                            System.out.println("Image not found: " + item);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
        actions.setCellFactory(createActionsCellFactory());
        list_user.setItems(list);

        // Set the ListView items and cell factory
        listviewusers.setItems(FXCollections.observableList(
                list.stream()
                        .filter(Objects::nonNull)
                        .map(user -> {
                            String username = user.toString();
                            if (username == null) {
                                return "Unknown";
                            }
                            return username;
                        })
                        .collect(Collectors.toList())
        ));

        // Add delete button to ListView
        listviewusers.setCellFactory(param -> new ListCell<String>() {
            private final HBox hbox = new HBox(10);
            private final Label label = new Label();
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    String userDetails = getItem();
                    if (userDetails != null) {
                        user userToDelete = list.stream()
                                .filter(u -> u.toString().equals(userDetails))
                                .findFirst()
                                .orElse(null);

                        if (userToDelete != null) {
                            showDeleteConfirmation(userToDelete);
                        }
                    }
                });
                hbox.getChildren().addAll(label, deleteButton);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(hbox);
                }
            }
        });
    }

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
        ObservableList<user> updatedList = FXCollections.observableList(userService.getallUserdata());
        list_user.setItems(updatedList);
        listviewusers.setItems(FXCollections.observableList(
                updatedList.stream()
                        .filter(Objects::nonNull)
                        .map(user -> {
                            String username = user.toString();
                            if (username == null) {
                                return "Unknown";
                            }
                            return username;
                        })
                        .collect(Collectors.toList())
        ));
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

    public void goto_dashboard(ActionEvent event) {
    }

    public void goto_user(ActionEvent event) {
    }

    public void goto_event(ActionEvent event) {
    }

    public void goto_forum(ActionEvent event) {
    }

    public void goto_shop(ActionEvent event) {
    }

    public void goto_blog(ActionEvent event) {
    }

    public void goto_edit(ActionEvent event) {
    }

    public void disconnect(ActionEvent event) {

    }
}