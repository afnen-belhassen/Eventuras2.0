package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entities.user;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import services.userService;
import javafx.scene.text.Text;
public class addUser {
    userService userService = new userService();
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private DatePicker birthday_input;
    @FXML
    private TextField email_input;
    @FXML
    private ComboBox<Integer> gender_combobox1;
    @FXML
    private TextField firstname_input;
    @FXML
    private ComboBox<String> gender_combobox;
    @FXML
    private TextField lastname_input;
    @FXML
    private TextField password_input;
    @FXML
    private TextField username_input;
    @FXML
    private TextField picture_input;
    @FXML
    private TextField phonenumber_input;
    @FXML
    private Text error;
    @FXML
    void reset_input(ActionEvent event) {
        lastname_input.clear();
        birthday_input.setValue(null);
        email_input.clear();
        firstname_input.clear();
        gender_combobox.setValue(null);
        password_input.clear();
        username_input.clear();
        picture_input.clear();
        phonenumber_input.clear();
    }

    @FXML
    void submit_user(ActionEvent event) throws IOException, SQLException {
        // 1. Validate the form inputs using the `validateForm()` method.
        if (validateForm()) {
            // 2. Get the file path of the uploaded profile picture.
            String picturePath = picture_input.getText();
    
            // 3. Convert the file path to a `Path` object for easier manipulation.
            Path path = Paths.get(picturePath);
    
            // 4. Extract the file name from the path.
            String fileName = path.getFileName().toString();
    
            // 5. Get the selected date from the `birthday_input` DatePicker.
            LocalDate selectedDate = birthday_input.getValue();
    
            // 6. Format the selected date as "yyyy-MM-dd" using a `DateTimeFormatter`.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedBirthday = selectedDate.format(formatter);
    
            // 7. Get the selected gender from the `gender_combobox`.
            String selectedgender = gender_combobox.getValue();
    
            // 8. Get the selected gender value (as an integer) from the `gender_combobox1`.
            int selectedGender = gender_combobox1.getValue();
    
            // 9. Create a new `user` object with the form data.
            user user = new user(
                username_input.getText(), // Username
                email_input.getText(),    // Email
                password_input.getText(), // Password
                firstname_input.getText(), // First name
                lastname_input.getText(), // Last name
                formattedBirthday,        // Formatted birthday
                selectedgender,           // Gender
                fileName,                 // Profile picture file name
                phonenumber_input.getText() // Phone number
            );
    
            // 10. Add the user to the database using the `userService`.
            userService.addUser(user);
    
            // 11. Define the destination path for the profile picture.
            Path destinationPath = Paths.get("src/main/resources/images/Profilepictures", fileName);
    
            // 12. Copy the uploaded profile picture to the destination path.
            try {
                Files.copy(path, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // 13. Handle any errors that occur during file copying.
                e.printStackTrace();
                System.out.println("Couldn't copy the file.");
            }
        }
    }

    @FXML
    void initialize() {
        // 1. Create an `ObservableList` of gender options ("Male" and "Female").
        ObservableList<String> genderOptions = FXCollections.observableArrayList("Male", "Female");
    
        // 2. Set the gender options in the `gender_combobox`.
        gender_combobox.setItems(genderOptions);
    
        // 3. Create an `ObservableList` of integer values (1, 2, 3, 4) for `gender_combobox1`.
        ObservableList<Integer> gender_combobox1 = FXCollections.observableArrayList(1, 2, 3, 4);
    
        // 4. Hide the `picture_input` field (likely because it is populated programmatically).
        picture_input.setVisible(false);
    }

    @FXML
    void back_to_list(ActionEvent event) throws IOException {
        mainController.loadFXML("/listUser.fxml");
    }

    public void upload_img(ActionEvent actionEvent) {
        // 1. Create a `FileChooser` to allow the user to select a file.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your profile picture");
    
        // 2. Show the file chooser dialog and get the selected file.
        File selectedFile = fileChooser.showOpenDialog(null);
    
        // 3. Check if a file was selected.
        if (selectedFile != null) {
            // 4. Get the file name in lowercase.
            String fileName = selectedFile.getName().toLowerCase();
    
            // 5. Check if the file has a valid extension (PNG, JPG, or JPEG).
            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                // 6. Set the file path in the `picture_input` field.
                picture_input.setText(selectedFile.getPath());
            } else {
                // 7. Display an error message for invalid file formats.
                System.out.println("Invalid file format. Please select a PNG or JPG file.");
            }
        } else {
            // 8. Display a message if no file was selected.
            System.out.println("No file selected");
        }
    }
    private boolean validateForm() {
        // 1. Check if any required fields are empty.
        if (username_input.getText().isEmpty() || email_input.getText().isEmpty() || password_input.getText().isEmpty() ||
                firstname_input.getText().isEmpty() || lastname_input.getText().isEmpty() ||
                birthday_input.getValue() == null || gender_combobox.getValue() == null || picture_input.getText().isEmpty()) {
            // 2. Display an error message if any field is empty.
            error.setText("All fields must be filled");
            error.setVisible(true);
            return false;
        }
    
        // 3. Validate the email address using the `isEmailValid()` method.
        String email = email_input.getText();
        if (!isEmailValid(email)) {
            error.setText("Invalid email address");
            error.setVisible(true);
            return false;
        }
    
        // 4. Validate the password using the `isValidPassword()` method.
        if (!isValidPassword(password_input.getText())) {
            error.setText("Password must contain at least one uppercase letter, one number, and one special character");
            error.setVisible(true);
            return false;
        }
    
        // 5. If all validations pass, return true.
        return true;
    }

    private boolean isValidPassword(String password) {
        // 1. Define a regex pattern for password validation:
        //    - At least one uppercase letter.
        //    - At least one digit.
        //    - At least one special character.
        //    - Minimum length of 8 characters.
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!/])(?=\\S+$).{8,}$";
    
        // 2. Compile the regex pattern.
        Pattern pattern = Pattern.compile(regex);
    
        // 3. Match the password against the pattern.
        Matcher matcher = pattern.matcher(password);
    
        // 4. Return true if the password matches the pattern, otherwise false.
        return matcher.matches();
    }
    private boolean isEmailValid(String email) {
        // 1. Define a regex pattern for email validation:
        //    - Allows letters, numbers, and certain special characters.
        //    - Requires an "@" symbol and a valid domain.
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
        // 2. Compile the regex pattern.
        Pattern pattern = Pattern.compile(emailRegex);
    
        // 3. Match the email against the pattern.
        Matcher matcher = pattern.matcher(email);
    
        // 4. Return true if the email matches the pattern, otherwise false.
        return matcher.matches();
    }
}