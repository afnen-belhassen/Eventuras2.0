package gui;

import entities.Partner;
import entities.Partnership;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class PartnershipCellController {

    @FXML
    private HBox Hbox;

    @FXML
    private Label organizerName;

    @FXML
    private Label partnerName;

    @FXML
    private Label contractType;

    @FXML
    private Label description;

    // Method to initialize the cell with a Partner and Partnership object
    public void initializeCell(Partner partner, Partnership partnership) {
        if (partner != null && partnership != null) {
            organizerName.setText("Organizer: " + partnership.getOrganizerId()); // Replace with actual organizer name if available
            partnerName.setText("Partner: " + partner.getName());
            contractType.setText("Contract Type: " + partnership.getContractType().toString());
            description.setText("Description: " + partnership.getDescription());
        } else {
            organizerName.setText("Organizer: N/A");
            partnerName.setText("Partner: N/A");
            contractType.setText("Contract Type: N/A");
            description.setText("Description: N/A");
        }
    }
}
