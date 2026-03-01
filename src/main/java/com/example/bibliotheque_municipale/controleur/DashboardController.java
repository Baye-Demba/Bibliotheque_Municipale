package com.example.bibliotheque_municipale.controleur;

import com.example.bibliotheque_municipale.dao.AdherentDAO;
import com.example.bibliotheque_municipale.dao.EmpruntDAO;
import com.example.bibliotheque_municipale.dao.LivreDAO;
import com.example.bibliotheque_municipale.modele.Emprunt;
import com.example.bibliotheque_municipale.util.Session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblNomUtilisateur;
    @FXML private Label lblProfil;
    @FXML private Label lblNbLivres;
    @FXML private Label lblNbAdherents;
    @FXML private Label lblEmpruntsMonth;
    @FXML private Label lblRetards;

    @FXML private TableView<Emprunt> tblRetards;
    @FXML private TableColumn<Emprunt, String> colLivreRetard;
    @FXML private TableColumn<Emprunt, String> colAdherentRetard;
    @FXML private TableColumn<Emprunt, String> colDatePrevueRetard;

    @FXML private HBox menuUtilisateurs;

    private LivreDAO livreDAO = new LivreDAO();
    private AdherentDAO adherentDAO = new AdherentDAO();
    private EmpruntDAO empruntDAO = new EmpruntDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Session.getUtilisateurConnecte() != null) {
            lblNomUtilisateur.setText(Session.getUtilisateurConnecte().getPrenom() + " " + Session.getUtilisateurConnecte().getNom());
            lblProfil.setText(Session.getUtilisateurConnecte().getProfil());
        }

        // cacher le menu utilisateurs si pas admin
        if (menuUtilisateurs != null && !Session.estAdmin()) {
            menuUtilisateurs.setVisible(false);
            menuUtilisateurs.setManaged(false);
        }

        lblNbLivres.setText(String.valueOf(livreDAO.getNombreTotalLivres()));
        lblNbAdherents.setText(String.valueOf(adherentDAO.getNombreTotalAdherents()));
        lblEmpruntsMonth.setText(String.valueOf(empruntDAO.getNombreEmpruntsDuMois()));
        lblRetards.setText(String.valueOf(empruntDAO.getNombreRetards()));

        colLivreRetard.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
        colAdherentRetard.setCellValueFactory(new PropertyValueFactory<>("nomAdherent"));
        colDatePrevueRetard.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        tblRetards.setItems(FXCollections.observableArrayList(empruntDAO.getRetards()));
    }

    @FXML
    void ouvrirLivres(ActionEvent event) {
        ouvrirPage(event, "livres.fxml", 1200, 700);
    }

    @FXML
    void ouvrirAdherents(ActionEvent event) {
        ouvrirPage(event, "Adherants.fxml", 1200, 700);
    }

    @FXML
    void ouvrirEmprunts(ActionEvent event) {
        ouvrirPage(event, "emprunts.fxml", 1200, 700);
    }

    @FXML
    void ouvrirUtilisateurs(ActionEvent event) {
        if (!Session.estAdmin()) {
            new Alert(Alert.AlertType.WARNING, "Acces reserve aux administrateurs.").showAndWait();
            return;
        }
        ouvrirPage(event, "Utilisateurs.fxml", 1200, 700);
    }

    @FXML
    void seDeconnecter(ActionEvent event) {
        Session.vider();
        ouvrirPage(event, "login.fxml", 900, 550);
    }

    private void ouvrirPage(ActionEvent event, String fxml, int l, int h) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/bibliotheque_municipale/" + fxml));
            Scene scene = new Scene(root, l, h);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Bibliotheque Municipale de Dakar");
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
