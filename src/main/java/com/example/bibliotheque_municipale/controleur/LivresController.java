package com.example.bibliotheque_municipale.controleur;

import com.example.bibliotheque_municipale.dao.CategorieDAO;
import com.example.bibliotheque_municipale.dao.LivreDAO;
import com.example.bibliotheque_municipale.modele.Categorie;
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

public class LivresController implements Initializable {

    @FXML private TableView<Livre> tblLivres;
    @FXML private TableColumn<Livre, String> colIsbn;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colCategorie;
    @FXML private TableColumn<Livre, Integer> colAnnee;
    @FXML private TableColumn<Livre, Integer> colExemplaires;
    @FXML private TableColumn<Livre, String> colDisponibilite;

    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitre;
    @FXML private TextField txtAuteur;
    @FXML private ComboBox<Categorie> cboCategorie;
    @FXML private TextField txtAnnee;
    @FXML private TextField txtExemplaires;
    @FXML private TextField txtRecherche;
    @FXML private Button btnSupprimer;

    private LivreDAO livreDAO = new LivreDAO();
    private CategorieDAO categorieDAO = new CategorieDAO();
    private Livre livreSelectionne = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("nomCategorie"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneePublication"));
        colExemplaires.setCellValueFactory(new PropertyValueFactory<>("nombreExemplaires"));
        colDisponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibiliteAffichage"));

        cboCategorie.setItems(FXCollections.observableArrayList(categorieDAO.getToutesLesCategories()));
        tblLivres.setItems(FXCollections.observableArrayList(livreDAO.getTousLesLivres()));

        // seul l'admin peut supprimer
        if (btnSupprimer != null && !Session.estAdmin()) btnSupprimer.setDisable(true);

        tblLivres.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null) {
                livreSelectionne = nouveau;
                txtIsbn.setText(nouveau.getIsbn());
                txtTitre.setText(nouveau.getTitre());
                txtAuteur.setText(nouveau.getAuteur());
                txtAnnee.setText(String.valueOf(nouveau.getAnneePublication()));
                txtExemplaires.setText(String.valueOf(nouveau.getNombreExemplaires()));
                for (Categorie c : cboCategorie.getItems()) {
                    if (c.getId() == nouveau.getCategorie().getId()) {
                        cboCategorie.setValue(c);
                        break;
                    }
                }
            }
        });
    }

    @FXML
    void rechercher(ActionEvent event) {
        String motCle = txtRecherche.getText().trim();
        if (motCle.isEmpty()) {
            tblLivres.setItems(FXCollections.observableArrayList(livreDAO.getTousLesLivres()));
        } else {
            tblLivres.setItems(FXCollections.observableArrayList(livreDAO.rechercher(motCle)));
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        if (txtTitre.getText().trim().isEmpty() || txtAuteur.getText().trim().isEmpty() || cboCategorie.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Le titre, l'auteur et la categorie sont obligatoires.");
            return;
        }
        try {
            int annee = Integer.parseInt(txtAnnee.getText().trim());
            if (annee < 1000 || annee > 2026) {
                showAlert(Alert.AlertType.ERROR, "l'annee doit etre entre 1000 et 2026");
                return;
            }
            int exemplaires = Integer.parseInt(txtExemplaires.getText().trim());
            if (exemplaires < 1) {
                showAlert(Alert.AlertType.ERROR, "le nombre d'exemplaires doit etre au moins 1");
                return;
            }
            Livre l = new Livre();
            l.setIsbn(txtIsbn.getText().trim());
            l.setTitre(txtTitre.getText().trim());
            l.setAuteur(txtAuteur.getText().trim());
            l.setCategorie(cboCategorie.getValue());
            l.setAnneePublication(annee);
            l.setNombreExemplaires(exemplaires);
            if (livreDAO.ajouter(l) > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Livre ajoute !");
                viderFormulaire();
                tblLivres.setItems(FXCollections.observableArrayList(livreDAO.getTousLesLivres()));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Annee et exemplaires doivent etre des nombres.");
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        if (livreSelectionne == null) { showAlert(Alert.AlertType.ERROR, "Selectionnez un livre."); return; }
        try {
            int annee = Integer.parseInt(txtAnnee.getText().trim());
            if (annee < 1000 || annee > 2026) {
                showAlert(Alert.AlertType.ERROR, "l'annee doit etre entre 1000 et 2026");
                return;
            }
            int exemplaires = Integer.parseInt(txtExemplaires.getText().trim());
            if (exemplaires < 1) {
                showAlert(Alert.AlertType.ERROR, "le nombre d'exemplaires doit etre au moins 1");
                return;
            }
            livreSelectionne.setIsbn(txtIsbn.getText().trim());
            livreSelectionne.setTitre(txtTitre.getText().trim());
            livreSelectionne.setAuteur(txtAuteur.getText().trim());
            livreSelectionne.setCategorie(cboCategorie.getValue());
            livreSelectionne.setAnneePublication(annee);
            livreSelectionne.setNombreExemplaires(exemplaires);
            if (livreDAO.modifier(livreSelectionne) > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Livre modifie !");
                viderFormulaire();
                tblLivres.setItems(FXCollections.observableArrayList(livreDAO.getTousLesLivres()));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Annee et exemplaires doivent etre des nombres.");
        }
    }

    @FXML
    void supprimer(ActionEvent event) {
        if (livreSelectionne == null) { showAlert(Alert.AlertType.ERROR, "Selectionnez un livre."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + livreSelectionne.getTitre() + " ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (livreDAO.supprimer(livreSelectionne.getId()) > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Livre supprime !");
                viderFormulaire();
                tblLivres.setItems(FXCollections.observableArrayList(livreDAO.getTousLesLivres()));
            }
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
        txtIsbn.clear(); txtTitre.clear(); txtAuteur.clear();
        txtAnnee.clear(); txtExemplaires.clear(); cboCategorie.setValue(null);
        livreSelectionne = null; tblLivres.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
