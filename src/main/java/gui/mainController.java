package gui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class mainController extends Application {
    private static Stage primaryStage;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException{
        mainController.primaryStage = primaryStage; // Initialize the primaryStage
        primaryStage.setTitle("Eventuras");
        loadFXML("/login.fxml"); // Load the initial FXML
        primaryStage.show();

    }
    public static FXMLLoader loadFXML(String fxmlFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(mainController.class.getResource(fxmlFileName));
        Parent root = loader.load();
        if (primaryStage != null) {
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            throw new IllegalStateException("Primary stage is not initialized.");
        }
        return loader;
    }
    public static void switchScene(String fxmlFileName) throws IOException {
        loadFXML(fxmlFileName);
    }



}
