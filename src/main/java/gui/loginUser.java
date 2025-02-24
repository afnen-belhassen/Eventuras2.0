package gui;

import entities.user;
import javafx.scene.control.*;
import services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Session;

import java.io.IOException;
import java.sql.SQLException;

public class loginUser {

    public Hyperlink register;
    @FXML
    private TextField email_input;

    @FXML
    private PasswordField password_input;

    @FXML
    private Text error;

    @FXML
    private Button submitButton;

    private userService userService = new userService();

    @FXML
    void login() throws SQLException {
        try {
            String email = email_input.getText().trim();
            String password = password_input.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Email and password cannot be empty.");
                return;
            }

            user user = userService.authenticateUser(email, password);

            if (user != null) {
                Session.getInstance().startSession(user);
                System.out.println("User logged in");
                // Store user session
                UserSession.getInstance(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getFirstname(),
                        user.getLastname(),
                        user.getBirthday(),
                        user.getGender(),
                        user.getPicture(),
                        user.getPhonenumber(),
                        user.getLevel(),
                        user.getRole()
                );

                // Redirect based on role
                switch (user.getRole().toLowerCase()) {
                    case "admin":
                        loadPage("/adminDashboard.fxml");
                        break;
                    case "participant":
                        loadPage("/participantDashboard.fxml");
                        break;
                    case "organisateur":
                        loadPage("/organisateurDashboard.fxml");
                        break;
                    default:
                        showAlert("Error", "Unknown role. Please contact support.");
                }
            } else {
                showAlert("Error", "Incorrect email or password. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    @FXML
    void register_page(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/registerUser.fxml"));
        Parent root = loader.load();


        // Switch to the AfficherEvent scene
        Stage stage = (Stage) register.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    @FXML
    void redirect_passwordpage(ActionEvent event) throws IOException {
        loadPage("/forgotPassword.fxml");
    }

    private void loadPage(String fxmlPath) throws IOException {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        error.setVisible(false);
    }
}
