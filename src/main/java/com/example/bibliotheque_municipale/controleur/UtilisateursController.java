package com.example.bibliotheque_municipale.controleur;

import com.example.bibliotheque_municipale.dao.UtilisateurDAO;
import com.example.bibliotheque_municipale.modele.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
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

public class UtilisateursController implements Initializable {

    @FXML private TableView<Utilisateur> tblUtilisateurs;
    @FXML private TableColumn<Utilisateur, String> colLogin;
    @FXML private TableColumn<Utilisateur, String> colNom;
    @FXML private TableColumn<Utilisateur, String> colPrenom;
    @FXML private TableColumn<Utilisateur, String> colEmail;
    @FXML private TableColumn<Utilisateur, String> colProfil;
    @FXML private TableColumn<Utilisateur, String> colActif;

    @FXML private TextField txtLogin;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cboProfil;
    @FXML private PasswordField txtMotDePasse;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private Utilisateur utilisateurSelectionne = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colProfil.setCellValueFactory(new PropertyValueFactory<>("profil"));
        colActif.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isActif() ? "Actif" : "Desactive"));

        cboProfil.setItems(FXCollections.observableArrayList("ADMIN", "BIBLIOTHECAIRE"));
        tblUtilisateurs.setItems(FXCollections.observableArrayList(utilisateurDAO.getTousLesUtilisateurs()));

        tblUtilisateurs.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null) {
                utilisateurSelectionne = nouveau;
                txtLogin.setText(nouveau.getLogin()); txtNom.setText(nouveau.getNom());
                txtPrenom.setText(nouveau.getPrenom()); txtEmail.setText(nouveau.getEmail());
                cboProfil.setValue(nouveau.getProfil()); txtMotDePasse.clear();
            }
        });
    }

    @FXML
    void ajouter(ActionEvent event) {
        if (txtLogin.getText().trim().isEmpty() || txtMotDePasse.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login et mot de passe obligatoires."); return;
        }
        Utilisateur u = new Utilisateur();
        u.setLogin(txtLogin.getText().trim()); u.setMotDePasse(txtMotDePasse.getText().trim());
        u.setNom(txtNom.getText().trim()); u.setPrenom(txtPrenom.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setProfil(cboProfil.getValue() != null ? cboProfil.getValue() : "BIBLIOTHECAIRE");
        if (utilisateurDAO.ajouter(u) > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Utilisateur cree !");
            viderFormulaire();
            tblUtilisateurs.setItems(FXCollections.observableArrayList(utilisateurDAO.getTousLesUtilisateurs()));
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        if (utilisateurSelectionne == null) { showAlert(Alert.AlertType.ERROR, "Selectionnez un utilisateur."); return; }
        utilisateurSelectionne.setNom(txtNom.getText().trim()); utilisateurSelectionne.setPrenom(txtPrenom.getText().trim());
        utilisateurSelectionne.setEmail(txtEmail.getText().trim()); utilisateurSelectionne.setProfil(cboProfil.getValue());
        if (utilisateurDAO.modifier(utilisateurSelectionne) > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Modifie !");
            tblUtilisateurs.setItems(FXCollections.observableArrayList(utilisateurDAO.getTousLesUtilisateurs()));
        }
    }

    @FXML
    void changerStatut(ActionEvent event) {
        if (utilisateurSelectionne == null) { showAlert(Alert.AlertType.ERROR, "Selectionnez un utilisateur."); return; }
        boolean nouvelEtat = !utilisateurSelectionne.isActif();
        if (utilisateurDAO.changerStatut(utilisateurSelectionne.getId(), nouvelEtat) > 0) {
            showAlert(Alert.AlertType.INFORMATION, nouvelEtat ? "Compte active." : "Compte desactive.");
            viderFormulaire();
            tblUtilisateurs.setItems(FXCollections.observableArrayList(utilisateurDAO.getTousLesUtilisateurs()));
        }
    }

    @FXML
    void reinitialiserMdp(ActionEvent event) {
        if (utilisateurSelectionne == null) { showAlert(Alert.AlertType.ERROR, "Selectionnez un utilisateur."); return; }
        String mdpParDefaut = "Bibl2025!";
        if (utilisateurDAO.reinitialiserMotDePasse(utilisateurSelectionne.getId(), mdpParDefaut) > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Mot de passe reinitialise a : " + mdpParDefaut);
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
        txtLogin.clear(); txtNom.clear(); txtPrenom.clear(); txtEmail.clear();
        txtMotDePasse.clear(); cboProfil.setValue(null);
        utilisateurSelectionne = null; tblUtilisateurs.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
