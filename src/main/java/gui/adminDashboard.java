package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;
import entities.user;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import services.userService;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
public class adminDashboard {
    @FXML
    private Text totalReclamation;

    @FXML
    private Text totalbillet;

    @FXML
    private Text totalfacture;

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private BarChart<String, Number> age_barchart;
    @FXML
    private Text date;
    @FXML
    private PieChart gender_piechart;
    @FXML
    private Pane ranking_pane;
    @FXML
    private VBox ranking_box;
    @FXML
    private Text totalusers;
    @FXML
    private Text scrolling_text;
    @FXML
    private Pane news_pane;
    @FXML
    private Text time;
    UserSession userSession = UserSession.getInstance();


    @FXML
    void initialize() {
        // 1. Update the time displayed on the dashboard.
        updateTime();
    
        // 2. Create an instance of the `userService` class to interact with user-related data.
        userService userService = new userService();
    
        // 3. Fetch all user data from the database and wrap it in an `ObservableList`.
        //    `ObservableList` is a special list that notifies the UI when its contents change.
        ObservableList<user> userList = FXCollections.observableList(userService.getallUserdata());
    
        // 4. Animate the scrolling text at the top of the dashboard.
        animateScrollingText();
    
        // 5. Animate the total number of users displayed on the dashboard.
        animateTotalUsers(userList.size());
    
        // 6. Sort the user list in descending order based on the user's level.
        //    `Comparator.comparingInt(user::getLevel).reversed()` sorts users by their level, highest first.
        userList.sort(Comparator.comparingInt(user::getLevel).reversed());
    
        // 7. Create a label for the "User Ranking" section.
        Label titleLabel = new Label("User Ranking:");
        //    Apply styling to the label (font size, weight, and color).
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");
    
        // 8. Create a `ListView` to display the ranked users.
        ListView<user> listView = new ListView<>(userList);
        //    Style the `ListView` with a transparent background and rounded corners.
        listView.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-background-radius: 20;");
    
        // 9. Set a custom cell factory for the `ListView`.
        //    This defines how each user in the list should be displayed.
        listView.setCellFactory(param -> new RankingListCell());
    
        // 10. Create a `VBox` (vertical box) to hold the "User Ranking" label and the `ListView`.
        VBox vBox = new VBox(10); // The `10` is the spacing between child elements.
        //     Style the `VBox` with a transparent background and rounded corners.
        vBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-background-radius: 20;");
        //     Center-align the contents of the `VBox`.
        vBox.setAlignment(Pos.CENTER);
    
        // 11. Add the "User Ranking" label and the `ListView` to the `VBox`.
        vBox.getChildren().addAll(titleLabel, listView);
    
        // 12. Ensure the `VBox` is center-aligned (redundant, as it was already set above).
        vBox.setAlignment(Pos.CENTER);
    
        // 13. Replace the contents of the `ranking_box` (a container in the UI) with the `VBox`.
        ranking_box.getChildren().setAll(vBox);
    
        // 14. Fetch gender distribution data from the database.
        //     This returns a `Map` where the key is the gender (e.g., "Male", "Female") and the value is the count.
        Map<String, Long> genderDistribution = userService.getGenderDistribution();
    
        // 15. Convert the gender distribution data into `PieChart.Data` objects.
        //     Each entry in the map is transformed into a `PieChart.Data` object.
        PieChart.Data[] pieChartData = genderDistribution.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .toArray(PieChart.Data[]::new);
    
        // 16. Add the gender distribution data to the `PieChart`.
        gender_piechart.getData().addAll(pieChartData);
    
        // 17. Fetch age distribution data from the database.
        //     This returns a `Map` where the key is the age group (e.g., "18-25", "26-35") and the value is the count.
        Map<String, Long> ageDistribution = userService.getAgeDistribution();
    
        // 18. Create axes for the `BarChart`.
        //     `CategoryAxis` is used for the X-axis (age groups), and `NumberAxis` is used for the Y-axis (counts).
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
    
        // 19. Create a `BarChart` with the specified axes.
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    
        // 20. Create a data series for the `BarChart`.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
    
        // 21. Add age distribution data to the series.
        //     Each entry in the map is added as a data point in the series.
        ageDistribution.entrySet().forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
    
        // 22. Add the series to the `BarChart`.
        age_barchart.getData().add(series);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // 1. Define a static inner class `RankingListCell` that extends `ListCell<user>`.
//    This class is responsible for rendering each item (user) in the `ListView`.
static class RankingListCell extends ListCell<user> {

    // 2. Override the `updateItem` method, which is called whenever the cell needs to be updated.
    //    This method determines how each user is displayed in the `ListView`.
    @Override
    protected void updateItem(user user, boolean empty) {
        // 3. Call the superclass implementation of `updateItem` to handle basic cell behavior.
        super.updateItem(user, empty);

        // 4. Check if the cell is empty or the user object is null.
        if (empty || user == null) {
            // 5. If the cell is empty or the user is null, clear the cell's content.
            setText(null);       // Clear any text.
            setGraphic(null);    // Clear any graphic (UI components).
        } else {
            // 6. If the cell is not empty and the user object is valid, create a custom layout for the cell.

            // 7. Create a `StackPane` to hold the UI components for the cell.
            //    A `StackPane` allows components to be stacked on top of each other.
            StackPane stackPane = new StackPane();

            // 8. Style the `StackPane` with a transparent background and rounded corners.
            stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-background-radius: 20;");

            // 9. Create a `Label` to display the user's username.
            Label usernameLabel = new Label(user.getUsername());

            // 10. Create a `ProgressBar` to visually represent the user's level.
            //     The user's level is divided by 10.0 to normalize it to a value between 0 and 1.
            ProgressBar progressBar = new ProgressBar(user.getLevel() / 10.0);

            // 11. Create a `Label` to display the user's level as text.
            Label levelLabel = new Label("Level: " + user.getLevel());

            // 12. Add the username label, progress bar, and level label to the `StackPane`.
            stackPane.getChildren().addAll(usernameLabel, progressBar, levelLabel);

            // 13. Align the username label to the left-center of the `StackPane`.
            StackPane.setAlignment(usernameLabel, Pos.CENTER_LEFT);

            // 14. Align the progress bar to the center of the `StackPane`.
            StackPane.setAlignment(progressBar, Pos.CENTER);

            // 15. Align the level label to the right-center of the `StackPane`.
            StackPane.setAlignment(levelLabel, Pos.CENTER_RIGHT);

            // 16. Set the `StackPane` as the graphic for the cell.
            //     This replaces the default cell content with the custom layout.
            setGraphic(stackPane);
        }
    }
}

    private void animateTotalUsers(int totalUsers) {
        int animationDurationMillis = 2000;
        int initialCount = 1;

        Timeline timeline = new Timeline();

        totalusers.setText(String.valueOf(initialCount));

        for (int count = initialCount + 1; count <= totalUsers; count++) {
            KeyValue keyValue = new KeyValue(totalusers.textProperty(), String.valueOf(count), Interpolator.LINEAR);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(animationDurationMillis * (count - initialCount) / (totalUsers - initialCount)), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.play();
    }

    private void animateScrollingText() {
        String newsText = " Welcome back !: " + userSession.getFirstname() + " " + userSession.getLastname() + " ";
        scrolling_text.setText(newsText);
        Rectangle clip = new Rectangle(1027, 60);
        news_pane.setClip(clip);
        news_pane.layout();
        double textWidth = scrolling_text.getLayoutBounds().getWidth();
        int animationDurationMillis = (int) (textWidth * 20);
        TranslateTransition transitionOut = new TranslateTransition(Duration.millis(animationDurationMillis), scrolling_text);
        transitionOut.setByX(-textWidth);
        TranslateTransition transitionIn = new TranslateTransition(Duration.ZERO, scrolling_text);
        transitionIn.setByX(1027);
        SequentialTransition sequentialTransition = new SequentialTransition(transitionOut, transitionIn);
        sequentialTransition.setCycleCount(SequentialTransition.INDEFINITE);
        sequentialTransition.play();
    }
    private void updateTime() {
        // Get the current time
        LocalTime currentTime = LocalTime.now();

        // Format the time using a DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = currentTime.format(formatter);

        LocalDate currentDate = LocalDate.now();

        // Format the date using a DateTimeFormatter
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter1);
        // Set the formatted time to the time Text element
        date.setText(formattedDate+"  |  "+formattedTime);
    }


}





