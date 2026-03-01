package com.example.bibliotheque_municipale.controleur;

import com.example.bibliotheque_municipale.dao.UtilisateurDAO;
import com.example.bibliotheque_municipale.modele.Utilisateur;
import com.example.bibliotheque_municipale.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtMotDePasse;

    @FXML
    private Label lblErreur;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    void seConnecter(ActionEvent event) {
        String login = txtLogin.getText().trim();
        String mdp = txtMotDePasse.getText().trim();

        if (login.isEmpty() || mdp.isEmpty()) {
            lblErreur.setText("Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur u = utilisateurDAO.seConnecter(login, mdp);

        if (u == null) {
            lblErreur.setText("Login ou mot de passe incorrect.");
            txtMotDePasse.clear();
            return;
        }

        Session.setUtilisateurConnecte(u);

        try {
            String page = u.isPremiereConnexion() ? "change_mdp.fxml" : "dashboard.fxml";
            int largeur = u.isPremiereConnexion() ? 500 : 1200;
            int hauteur = u.isPremiereConnexion() ? 350 : 700;

            ((Node) event.getSource()).getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/bibliotheque_municipale/" + page));
            Scene scene = new Scene(root, largeur, hauteur);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Bibliotheque Municipale de Dakar");
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            lblErreur.setText("Erreur lors de l'ouverture.");
            System.out.println(ex);
        }
    }
}
