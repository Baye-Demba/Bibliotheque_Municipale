package com.example.bibliotheque_municipale.controleur;

import com.example.bibliotheque_municipale.dao.AdherentDAO;
import com.example.bibliotheque_municipale.dao.EmpruntDAO;
import com.example.bibliotheque_municipale.dao.LivreDAO;
import com.example.bibliotheque_municipale.modele.Adherent;
import com.example.bibliotheque_municipale.modele.Emprunt;
import com.example.bibliotheque_municipale.modele.Livre;
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
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EmpruntsController implements Initializable {

    @FXML private TableView<Emprunt> tblEmprunts;
    @FXML private TableColumn<Emprunt, String> colLivre;
    @FXML private TableColumn<Emprunt, String> colAdherent;
    @FXML private TableColumn<Emprunt, String> colDateEmprunt;
    @FXML private TableColumn<Emprunt, String> colDatePrevue;
    @FXML private TableColumn<Emprunt, String> colStatut;
    @FXML private TableColumn<Emprunt, Double> colPenalite;

    @FXML private ComboBox<Livre> cboLivre;
    @FXML private ComboBox<Adherent> cboAdherent;

    @FXML private RadioButton rbTous;
    @FXML private RadioButton rbEnCours;
    @FXML private RadioButton rbRetards;

    private EmpruntDAO empruntDAO = new EmpruntDAO();
    private LivreDAO livreDAO = new LivreDAO();
    private AdherentDAO adherentDAO = new AdherentDAO();
    private Emprunt empruntSelectionne = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colLivre.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
        colAdherent.setCellValueFactory(new PropertyValueFactory<>("nomAdherent"));
        colDateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        colDatePrevue.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colPenalite.setCellValueFactory(new PropertyValueFactory<>("penalite"));

        // seulement les livres disponibles
        cboLivre.setItems(FXCollections.observableArrayList(
            livreDAO.getTousLesLivres().stream().filter(Livre::isDisponible).toList()
        ));
        // seulement les adherents actifs
        cboAdherent.setItems(FXCollections.observableArrayList(
            adherentDAO.getTousLesAdherents().stream().filter(Adherent::isActif).toList()
        ));

        tblEmprunts.setItems(FXCollections.observableArrayList(empruntDAO.getTousLesEmprunts()));

        tblEmprunts.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> {
            empruntSelectionne = nouveau;
        });
    }

    @FXML
    void filtrerEmprunts(ActionEvent event) {
        if (rbEnCours != null && rbEnCours.isSelected()) {
            tblEmprunts.setItems(FXCollections.observableArrayList(empruntDAO.getEmpruntsEnCours()));
        } else if (rbRetards != null && rbRetards.isSelected()) {
            tblEmprunts.setItems(FXCollections.observableArrayList(empruntDAO.getRetards()));
        } else {
            tblEmprunts.setItems(FXCollections.observableArrayList(empruntDAO.getTousLesEmprunts()));
        }
    }

    @FXML
    void enregistrerEmprunt(ActionEvent event) {
        if (cboLivre.getValue() == null || cboAdherent.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Selectionnez un livre et un adherent."); return;
        }
        int res = empruntDAO.enregistrerEmprunt(
            cboLivre.getValue().getId(),
            cboAdherent.getValue().getId(),
            Session.getUtilisateurConnecte().getId()
        );
        if (res > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Emprunt enregistre ! Retour prevu dans 14 jours.");
            cboLivre.setValue(null); cboAdherent.setValue(null);
            // rafraichir les combobox
            cboLivre.setItems(FXCollections.observableArrayList(
                livreDAO.getTousLesLivres().stream().filter(Livre::isDisponible).toList()
            ));
            tblEmprunts.setItems(FXCollections.observableArrayList(empruntDAO.getTousLesEmprunts()));
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement.");
        }
    }

    @FXML
    void enregistrerRetour(ActionEvent event) {
        if (empruntSelectionne == null) { showAlert(Alert.AlertType.ERROR, "Selectionnez un emprunt."); return; }
        if (empruntSelectionne.getDateRetourEffective() != null) { showAlert(Alert.AlertType.ERROR, "Ce livre est deja retourne."); return; }

        empruntSelectionne.setDateRetourEffective(java.time.LocalDate.now());
        double penalite = empruntSelectionne.calculerPenalite();

        String msg = "Confirmer le retour ?";
        if (penalite > 0) msg += "\nPenalite : " + penalite + " FCFA";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, msg);
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (empruntDAO.enregistrerRetour(empruntSelectionne.getId(), empruntSelectionne.getLivre().getId(), penalite) > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Retour enregistre !");
                cboLivre.setItems(FXCollections.observableArrayList(
                    livreDAO.getTousLesLivres().stream().filter(Livre::isDisponible).toList()
                ));
                tblEmprunts.setItems(FXCollections.observableArrayList(empruntDAO.getTousLesEmprunts()));
                empruntSelectionne = null;
            }
        }
    }

    @FXML
    void retourDashboard(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/bibliotheque_municipale/dashboard.fxml"));
            new Stage() {{ setScene(new Scene(root, 1200, 700)); setResizable(false); setTitle("Bibliotheque Municipale"); show(); }};
        } catch (Exception ex) { System.out.println(ex); }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
