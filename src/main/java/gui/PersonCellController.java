package gui;

import entities.Partner;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class PersonCellController {

    @FXML
    private HBox Hbox;

    @FXML
    private Label NameLabel;

    @FXML
    private Label TypeLabel;

    @FXML
    private Label ContactInfoLabel;

    @FXML
    private Label VideoLabel;

    @FXML
    private Slider ratingSlider;  // Add a reference to the Slider

    // Method to initialize the cell with a Partner object
    public void initializeCell(Partner partner) {
        if (partner != null) {
            NameLabel.setText(partner.getName());
            TypeLabel.setText(partner.getType().toString());
            ContactInfoLabel.setText(partner.getContactInfo());
            VideoLabel.setText(partner.getImagePath());

            // Set the rating slider value based on the partner's rating
            ratingSlider.setValue(partner.getRating()); // Assuming Partner has a getRating method
        }
    }

    // Optionally, you could add a method to get the current rating from the slider
    public double getRating() {
        return ratingSlider.getValue();  // Return the value of the rating slider
    }

}
