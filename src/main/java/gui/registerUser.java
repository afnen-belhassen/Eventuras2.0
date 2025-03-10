package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entities.Role;
import entities.user;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.Crole;
import utils.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import services.userService;

public class registerUser {

    @FXML
    public Text error;
    @FXML
    public ComboBox<String> role_input;
    public Button login;
    public Button submit;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private DatePicker birthday_input;
    @FXML
    private TextField email_input;
    @FXML
    private TextField firstname_input;
    @FXML
    private ComboBox<String> gender_input;
    @FXML
    private ComboBox<Integer> level_PMR_input;
    @FXML
    private TextField lastname_input;
    @FXML
    private PasswordField password_input;
    @FXML
    private PasswordField passwordconfirmation_input;
    @FXML
    private TextField picture_input;
    @FXML
    private TextField username_input;
    @FXML
    private TextField phonenumber_input;

    private boolean isInitialized = false;
    private final userService userService = new userService();
    private final Crole croleService = new Crole();

    private Connection cnx;
    public registerUser() {
        cnx=MyConnection.getInstance().getConnection();
    }

    @FXML
    void initialize() {
        // Set up gender options
        ObservableList<String> genderOptions = FXCollections.observableArrayList("Male", "Female");
        gender_input.setItems(genderOptions);

        // Set up PMR level options
        ObservableList<Integer> levels = FXCollections.observableArrayList(1, 2, 3, 4);
        level_PMR_input.setItems(levels);

        // Hide error message initially
        error.setVisible(false);

        // Hide picture input (if needed)
        picture_input.setVisible(false);

        // Initialize the ComboBox with roles
        refreshRoleComboBox();
    }

    // Refresh the roles ComboBox
    private void refreshRoleComboBox() {
        try {
            // Fetch roles from the database
            List<Role> roles = croleService.afficherAll();
            ObservableList<String> roleNames = FXCollections.observableArrayList();
            for (Role role : roles) {
                roleNames.add(role.getRoleName());
            }

            // Set the items in the ComboBox
            role_input.setItems(roleNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void back_to_login(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();


        // Switch to the AfficherEvent scene
        Stage stage = (Stage) login.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    @FXML
    void reset_inputs(ActionEvent event) {
        lastname_input.clear();
        birthday_input.setValue(null);
        email_input.clear();
        firstname_input.clear();
        gender_input.setValue(null);
        password_input.clear();
        username_input.clear();
        picture_input.clear();
        phonenumber_input.clear();
    }

    @FXML
    public void user_Submit(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Validate form inputs
        if (!validateForm()) {
            alert.setTitle("Error");
            alert.setContentText("Please fill all fields correctly.");
            alert.showAndWait();
            return;
        }

        // Retrieve role ID
        String roleName = role_input.getSelectionModel().getSelectedItem();
        int roleId = -1;

        try {
            roleId = croleService.getRoleIdByName(roleName);
            if (roleId == -1) {
                throw new SQLException("Role not found");
            }
        } catch (SQLException e) {
            alert.setTitle("Error");
            alert.setContentText("Error retrieving role ID: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Retrieve and validate picture path
        String picturePath = picture_input.getText();
        if (picturePath.isEmpty()) {
            alert.setTitle("Error");
            alert.setContentText("Please select a profile picture.");
            alert.showAndWait();
            return;
        }

        Path path = Paths.get(picturePath);
        String fileName = path.getFileName().toString();

        // Retrieve and validate birthday
        LocalDate selectedDate = birthday_input.getValue();
        if (selectedDate == null) {
            alert.setTitle("Error");
            alert.setContentText("Please select a birth date.");
            alert.showAndWait();
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedBirthday = selectedDate.format(formatter);

        // Retrieve other inputs
        String selectedGender = gender_input.getValue();
        int selectedLevel = level_PMR_input.getValue();

        // Create the user object
        user user = new user(
                roleId,
                username_input.getText(),
                email_input.getText(),
                password_input.getText(),
                firstname_input.getText(),
                lastname_input.getText(),
                formattedBirthday,
                selectedGender,
                fileName,
                phonenumber_input.getText(),
                selectedLevel,
                roleName
                );

        // Add the user to the database
        try {
            userService.addUser(user);
        } catch (SQLException e) {
            alert.setTitle("Error");
            alert.setContentText("Error adding user: " + e.getMessage());
            alert.showAndWait();
            return;
        } catch (IOException e) {
            alert.setTitle("Error");
            alert.setContentText("Error handling files: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Save the profile picture
        Path destinationPath = Paths.get("src/main/resources/images/", fileName);
        try {
            Files.copy(path, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            alert.setTitle("Error");
            alert.setContentText("Error saving image: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Show confirmation message
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setContentText("User added successfully. Please log in.");
        confirmationAlert.show();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();


        // Switch to the AfficherEvent scene
        Stage stage = (Stage) submit.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    // Profile picture upload method
    public void upload_pfp(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your profile picture");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String fileName = selectedFile.getName().toLowerCase();
            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                picture_input.setText(selectedFile.getPath());
            } else {
                System.out.println("Invalid file format. Please select a PNG or JPG file.");
            }
        } else {
            System.out.println("No file selected");
        }
    }

    // Validation form for the register page
    private boolean validateForm() {
        if (username_input.getText().isEmpty() || email_input.getText().isEmpty() || password_input.getText().isEmpty()
                || firstname_input.getText().isEmpty() || lastname_input.getText().isEmpty() ||
                birthday_input.getValue() == null || gender_input.getValue() == null || picture_input.getText().isEmpty() || role_input.getValue() == null) {
            error.setText("All fields must be filled");
            error.setVisible(true);
            return false;
        }

        String email = email_input.getText();
        if (!isEmailValid(email)) {
            error.setText("Invalid email address");
            error.setVisible(true);
            return false;
        }

        if (!isValidPassword(password_input.getText())) {
            error.setText("Password must contain at least one uppercase letter, one number, and one special character");
            error.setVisible(true);
            return false;
        }

        if (!password_input.getText().equals(passwordconfirmation_input.getText())) {
            error.setText("Your password is not the same as the confirmation");
            error.setVisible(true);
            return false;
        }
        return true;
    }

    // Password validation method
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!/])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // Email validation method
    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Method to generate user password
    public static String generatePassword() {
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String specialCharacters = "@$!%*?&";
        String allCharacters = uppercaseLetters + digits + specialCharacters;
        int passwordLength = 12;
        StringBuilder password = new StringBuilder(passwordLength);
        SecureRandom random = new SecureRandom();
        password.append(uppercaseLetters.charAt(random.nextInt(uppercaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));
        for (int i = 3; i < passwordLength; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }
        return password.toString();
    }
    public void opened(ActionEvent actionEvent) {
        if (isInitialized) {
            return;
        }

        try {
            // Fetch roles from the database
            List<Role> roles = croleService.afficherAll();
            ObservableList<String> roleNames = FXCollections.observableArrayList();
            for (Role role : roles) {
                roleNames.add(role.getRoleName());
            }

            // Add a default option (if needed)
            roleNames.add("Select Role");

            // Set the items in the ComboBox
            role_input.setItems(roleNames);
            System.out.println("ComboBox options: " + roleNames);

            // Set the default value (if needed)
            if (!roleNames.isEmpty()) {
                role_input.setValue(roleNames.get(0));
            }

            isInitialized = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}