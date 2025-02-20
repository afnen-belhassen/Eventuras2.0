package org.example.gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.MyConnection;
import org.example.entities.Reservation;
import org.example.services.Service;

import java.sql.Connection;
import java.sql.SQLException;



public class AjouterReservation {
    public TextField status;
    public TextField prix;
    public TextField Nbp;
    public Button ajouter;
    Service service = new Service();
    private Connection cnx;

    public AjouterReservation() {
        cnx = MyConnection.getInstance().getCnx();
    }
    private final Service Service = new Service();

    public void ajouter(ActionEvent actionEvent) {
        Reservation reservation = new Reservation();
        String status=this.status.getText();
        Double prix = Double.parseDouble(this.prix.getText());
        int NbPlaces = Integer.parseInt(this.Nbp.getText());
        try {
            service.ajouter(new Reservation(status,NbPlaces,prix));
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
