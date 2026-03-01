package com.example.bibliotheque_municipale.dao;

import com.example.bibliotheque_municipale.modele.Utilisateur;
import com.example.bibliotheque_municipale.util.DB;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public Utilisateur seConnecter(String login, String motDePasse) {
        DB db = new DB();
        try {
            ResultSet rs = db.select(
                "SELECT * FROM utilisateurs WHERE login = ? AND mot_de_passe = ? AND actif = 1",
                login, motDePasse
            );
            if (rs != null && rs.next()) {
                return extraireUtilisateur(rs);
            }
        } catch (Exception ex) {
            System.out.println("seConnecter : " + ex.getMessage());
        } finally {
            db.fermer();
        }
        return null;
    }

    public List<Utilisateur> getTousLesUtilisateurs() {
        DB db = new DB();
        List<Utilisateur> liste = new ArrayList<>();
        try {
            ResultSet rs = db.select("SELECT * FROM utilisateurs ORDER BY nom");
            while (rs != null && rs.next()) {
                liste.add(extraireUtilisateur(rs));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return liste;
    }

    public int ajouter(Utilisateur u) {
        DB db = new DB();
        try {
            return db.maj(
                "INSERT INTO utilisateurs (login, mot_de_passe, nom, prenom, email, profil, actif, date_creation, premiere_connexion) VALUES (?,?,?,?,?,?,1,NOW(),1)",
                u.getLogin(), u.getMotDePasse(), u.getNom(), u.getPrenom(), u.getEmail(), u.getProfil()
            );
        } finally {
            db.fermer();
        }
    }

    public int modifier(Utilisateur u) {
        DB db = new DB();
        try {
            return db.maj(
                "UPDATE utilisateurs SET nom=?, prenom=?, email=?, profil=? WHERE id=?",
                u.getNom(), u.getPrenom(), u.getEmail(), u.getProfil(), u.getId()
            );
        } finally {
            db.fermer();
        }
    }

    public int changerStatut(int id, boolean actif) {
        DB db = new DB();
        try {
            return db.maj("UPDATE utilisateurs SET actif = ? WHERE id = ?", actif, id);
        } finally {
            db.fermer();
        }
    }

    public int changerMotDePasse(int id, String nouveauMdp) {
        DB db = new DB();
        try {
            return db.maj(
                "UPDATE utilisateurs SET mot_de_passe = ?, premiere_connexion = 0 WHERE id = ?",
                nouveauMdp, id
            );
        } finally {
            db.fermer();
        }
    }

    public int reinitialiserMotDePasse(int id, String nouveauMdp) {
        DB db = new DB();
        try {
            return db.maj(
                "UPDATE utilisateurs SET mot_de_passe = ?, premiere_connexion = 1 WHERE id = ?",
                nouveauMdp, id
            );
        } finally {
            db.fermer();
        }
    }

    private Utilisateur extraireUtilisateur(ResultSet rs) throws Exception {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setLogin(rs.getString("login"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setProfil(rs.getString("profil"));
        u.setActif(rs.getBoolean("actif"));
        u.setPremiereConnexion(rs.getBoolean("premiere_connexion"));
        return u;
    }
}
