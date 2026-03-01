package com.example.bibliotheque_municipale.controleur;

import com.example.bibliotheque_municipale.dao.UtilisateurDAO;
import com.example.bibliotheque_municipale.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ChangeMdpController {

    @FXML
    private PasswordField txtNouveauMdp;

    @FXML
    private PasswordField txtConfirmMdp;

    @FXML
    private Label lblErreur;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    void valider(ActionEvent event) {
        String nouveauMdp = txtNouveauMdp.getText().trim();
        String confirmMdp = txtConfirmMdp.getText().trim();

        if (nouveauMdp.isEmpty()) {
            lblErreur.setText("Entrez un nouveau mot de passe.");
            return;
        }

        if (nouveauMdp.length() < 6) {
            lblErreur.setText("Minimum 6 caracteres.");
            return;
        }

        if (!nouveauMdp.equals(confirmMdp)) {
            lblErreur.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        int res = utilisateurDAO.changerMotDePasse(Session.getUtilisateurConnecte().getId(), nouveauMdp);

        if (res > 0) {
            Session.getUtilisateurConnecte().setPremiereConnexion(false);
            try {
                ((Node) event.getSource()).getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/bibliotheque_municipale/dashboard.fxml"));
                Scene scene = new Scene(root, 1200, 700);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Bibliotheque Municipale de Dakar");
                stage.setResizable(false);
                stage.show();
            } catch (Exception ex) {
                lblErreur.setText("Erreur redirection.");
                System.out.println(ex);
            }
        } else {
            lblErreur.setText("Erreur lors de la mise a jour.");
        }
    }
}
