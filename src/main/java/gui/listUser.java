package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import entities.Role;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import entities.user;
import services.Crole;
import services.userService;
import javafx.event.ActionEvent;

public class listUser {
    @FXML
    private TextField searchbar_id;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private GridPane grid;
    @FXML
    private ScrollPane scroll;

    private Crole crole = new Crole();

    private userService userService = new userService();

    @FXML
    void add_user(ActionEvent event) throws IOException {
        mainController.loadFXML("/addUser.fxml");
    }

    @FXML
    void display_charts(ActionEvent event) throws IOException {
        mainController.loadFXML("/Crudrole.fxml");
    }

    @FXML
    void initialize() {
        // Set up search functionality
        searchbar_id.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                populateGrid(newValue.toLowerCase());
            } catch (Exception e) {
                System.err.println("Error refreshing grid: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Initial population
        try {
            populateGrid("");
        } catch (Exception e) {
            System.err.println("Error initializing grid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateGrid(String searchText) {
        // Clear existing grid content
        grid.getChildren().clear();

        // Get all users
        List<user> users = userService.getallUserdata();

        // Set initial position
        int column = 0;
        int row = 1;
        boolean foundResults = false;

        // Populate the grid with filtered users
        for (user currentUser : users) {
            // Apply search filter if search text is not empty
            if (!searchText.isEmpty() &&
                    !currentUser.getFirstname().toLowerCase().contains(searchText) &&
                    !currentUser.getLastname().toLowerCase().contains(searchText) &&
                    !currentUser.getUsername().toLowerCase().contains(searchText) &&
                    !currentUser.getEmail().toLowerCase().contains(searchText)) {
                continue; // Skip users that don't match the search criteria
            }

            foundResults = true;
            String imagePath = currentUser.getPicture();

            // Check if image exists
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);

                if (imageFile.exists()) {
                    try {
                        // Load image
                        Image userImage = new Image(imageFile.toURI().toString());

                        // Create user card
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/item.fxml"));
                        AnchorPane userCard = fxmlLoader.load();
                        ItemController itemController = fxmlLoader.getController();

                        int role = currentUser.getId_role();
                        Role userRole = crole.getRoleById(role);
                        System.out.println("Role: " + userRole.getRoleName());
                        // Set user data in card
                        itemController.setLocation(currentUser.getFirstname(), userRole.getRoleName(), userImage, currentUser);

                        // Add card to grid with margin
                        GridPane.setMargin(userCard, new Insets(10));
                        grid.add(userCard, column, row);

                        // Move to next position
                        column++;
                        if (column == 3) { // 3 cards per row
                            column = 0;
                            row++;
                        }
                    } catch (IOException e) {
                        System.err.println("Error loading user card: " + e.getMessage());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Image file not found: " + imagePath);
                }
            } else {
                System.out.println("No image path for user: " + currentUser.getFirstname());
            }
        }

        // If no results found, display a message
        if (!foundResults && !searchText.isEmpty()) {
            Label noResultsLabel = new Label("No users found matching: " + searchText);
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
            grid.add(noResultsLabel, 0, 0, 3, 1); // Span across all columns
        }

        // Adjust grid size for proper scrolling
        grid.setPrefHeight((row + 1) * 250); // Adjust based on your card height
    }

    // Method to handle deletion of a user (can be called from the item controller)
    public void deleteUser(user user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user " + user.getUsername() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Delete user from database
                userService.deleteUser(user.getId());

                // Delete user profile image if it exists
                String imagePath = user.getPicture();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        Files.deleteIfExists(Paths.get(imagePath));
                        System.out.println("User profile image deleted: " + imagePath);
                    } catch (IOException e) {
                        System.err.println("Error deleting image: " + e.getMessage());
                    }
                }

                // Refresh the grid
                populateGrid(searchbar_id.getText().toLowerCase());
            }
        });
    }
}