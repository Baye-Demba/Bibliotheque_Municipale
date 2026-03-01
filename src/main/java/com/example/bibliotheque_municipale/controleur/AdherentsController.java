package com.example.bibliotheque_municipale.controleur;

import com.example.bibliotheque_municipale.dao.AdherentDAO;
import com.example.bibliotheque_municipale.dao.EmpruntDAO;
import com.example.bibliotheque_municipale.modele.Adherent;
import com.example.bibliotheque_municipale.modele.Emprunt;
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

public class AdherentsController implements Initializable {

    @FXML private TableView<Adherent> tblAdherents;
    @FXML private TableColumn<Adherent, String> colMatricule;
    @FXML private TableColumn<Adherent, String> colNom;
    @FXML private TableColumn<Adherent, String> colPrenom;
    @FXML private TableColumn<Adherent, String> colEmail;
    @FXML private TableColumn<Adherent, String> colTelephone;
    @FXML private TableColumn<Adherent, String> colStatut;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtAdresse;

    @FXML private TableView<Emprunt> tblHistorique;
    @FXML private TableColumn<Emprunt, String> colLivreHisto;
    @FXML private TableColumn<Emprunt, String> colDateEmpHisto;
    @FXML private TableColumn<Emprunt, String> colStatutHisto;

    private AdherentDAO adherentDAO = new AdherentDAO();
    private EmpruntDAO empruntDAO = new EmpruntDAO();
    private Adherent adherentSelectionne = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statutAffichage"));

        if (tblHistorique != null) {
            colLivreHisto.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
            colDateEmpHisto.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
            colStatutHisto.setCellValueFactory(new PropertyValueFactory<>("statut"));
        }

        tblAdherents.setItems(FXCollections.observableArrayList(adherentDAO.getTousLesAdherents()));

        tblAdherents.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null) {
                adherentSelectionne = nouveau;
                txtNom.setText(nouveau.getNom());
                txtPrenom.setText(nouveau.getPrenom());
                txtEmail.setText(nouveau.getEmail());
                txtTelephone.setText(nouveau.getTelephone());
                txtAdresse.setText(nouveau.getAdresse());
                if (tblHistorique != null) {
                    tblHistorique.setItems(FXCollections.observableArrayList(empruntDAO.getHistoriqueAdherent(nouveau.getId())));
                }
            }
        });
    }

    @FXML
    void inscrire(ActionEvent event) {
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Nom et prenom obligatoires."); return;
        }
        Adherent a = new Adherent();
        a.setNom(txtNom.getText().trim()); a.setPrenom(txtPrenom.getText().trim());
        a.setEmail(txtEmail.getText().trim()); a.setTelephone(txtTelephone.getText().trim());
        a.setAdresse(txtAdresse.getText().trim());
        if (adherentDAO.ajouter(a) > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Adherent inscrit !");
            viderFormulaire();
            tblAdherents.setItems(FXCollections.observableArrayList(adherentDAO.getTousLesAdherents()));
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        if (adherentSelectionne == null) { showAlert(Alert.AlertType.ERROR, "Selectionnez un adherent."); return; }
        adherentSelectionne.setNom(txtNom.getText().trim()); adherentSelectionne.setPrenom(txtPrenom.getText().trim());
        adherentSelectionne.setEmail(txtEmail.getText().trim()); adherentSelectionne.setTelephone(txtTelephone.getText().trim());
        adherentSelectionne.setAdresse(txtAdresse.getText().trim());
        if (adherentDAO.modifier(adherentSelectionne) > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Informations mises a jour !");
            tblAdherents.setItems(FXCollections.observableArrayList(adherentDAO.getTousLesAdherents()));
        }
    }

    @FXML
    void suspendre(ActionEvent event) {
        if (adherentSelectionne == null) { showAlert(Alert.AlertType.ERROR, "Selectionnez un adherent."); return; }
        boolean nouvelEtat = !adherentSelectionne.isActif();
        if (adherentDAO.changerStatut(adherentSelectionne.getId(), nouvelEtat) > 0) {
            showAlert(Alert.AlertType.INFORMATION, nouvelEtat ? "Adherent reactive." : "Adherent suspendu.");
            viderFormulaire();
            tblAdherents.setItems(FXCollections.observableArrayList(adherentDAO.getTousLesAdherents()));
        }
    }

    @FXML
    void vider(ActionEvent event) { viderFormulaire(); }

    @FXML
    void retourDashboard(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/bibliotheque_municipale/dashboard.fxml"));
            new Stage() {{ setScene(new Scene(root, 1200, 700)); setResizable(false); setTitle("Bibliotheque Municipale"); show(); }};
        } catch (Exception ex) { System.out.println(ex); }
    }

    private void viderFormulaire() {
        txtNom.clear(); txtPrenom.clear(); txtEmail.clear(); txtTelephone.clear(); txtAdresse.clear();
        adherentSelectionne = null; tblAdherents.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
