package com.example.bibliotheque_municipale.dao;

import com.example.bibliotheque_municipale.modele.*;
import com.example.bibliotheque_municipale.util.DB;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {

    public List<Emprunt> getTousLesEmprunts() {
        return chargerEmprunts(
            "SELECT e.*, l.titre as livre_titre, l.auteur as livre_auteur, " +
            "a.nom as adh_nom, a.prenom as adh_prenom, a.matricule as adh_matricule " +
            "FROM emprunts e JOIN livres l ON e.livre_id = l.id " +
            "JOIN adherents a ON e.adherent_id = a.id ORDER BY e.date_emprunt DESC"
        );
    }

    public List<Emprunt> getEmpruntsEnCours() {
        return chargerEmprunts(
            "SELECT e.*, l.titre as livre_titre, l.auteur as livre_auteur, " +
            "a.nom as adh_nom, a.prenom as adh_prenom, a.matricule as adh_matricule " +
            "FROM emprunts e JOIN livres l ON e.livre_id = l.id " +
            "JOIN adherents a ON e.adherent_id = a.id " +
            "WHERE e.date_retour_effective IS NULL ORDER BY e.date_retour_prevue"
        );
    }

    public List<Emprunt> getRetards() {
        return chargerEmprunts(
            "SELECT e.*, l.titre as livre_titre, l.auteur as livre_auteur, " +
            "a.nom as adh_nom, a.prenom as adh_prenom, a.matricule as adh_matricule " +
            "FROM emprunts e JOIN livres l ON e.livre_id = l.id " +
            "JOIN adherents a ON e.adherent_id = a.id " +
            "WHERE e.date_retour_effective IS NULL AND e.date_retour_prevue < CURDATE() ORDER BY e.date_retour_prevue"
        );
    }

    public List<Emprunt> getHistoriqueAdherent(int adherentId) {
        DB db = new DB();
        List<Emprunt> liste = new ArrayList<>();
        try {
            ResultSet rs = db.select(
                "SELECT e.*, l.titre as livre_titre, l.auteur as livre_auteur, " +
                "a.nom as adh_nom, a.prenom as adh_prenom, a.matricule as adh_matricule " +
                "FROM emprunts e JOIN livres l ON e.livre_id = l.id " +
                "JOIN adherents a ON e.adherent_id = a.id " +
                "WHERE e.adherent_id = ? ORDER BY e.date_emprunt DESC",
                adherentId
            );
            while (rs != null && rs.next()) liste.add(extraireEmprunt(rs));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return liste;
    }

    public int enregistrerEmprunt(int livreId, int adherentId, int utilisateurId) {
        DB db = new DB();
        try {
            LocalDate retourPrevu = LocalDate.now().plusDays(14);
            int res = db.maj(
                "INSERT INTO emprunts (livre_id, adherent_id, utilisateur_id, date_emprunt, date_retour_prevue) VALUES (?,?,?,NOW(),?)",
                livreId, adherentId, utilisateurId, java.sql.Date.valueOf(retourPrevu)
            );
            if (res > 0) db.maj("UPDATE livres SET disponible = 0 WHERE id = ?", livreId);
            return res;
        } finally {
            db.fermer();
        }
    }

    public int enregistrerRetour(int empruntId, int livreId, double penalite) {
        DB db = new DB();
        try {
            int res = db.maj(
                "UPDATE emprunts SET date_retour_effective = NOW(), penalite = ? WHERE id = ?",
                penalite, empruntId
            );
            if (res > 0) db.maj("UPDATE livres SET disponible = 1 WHERE id = ?", livreId);
            return res;
        } finally {
            db.fermer();
        }
    }

    public int getNombreEmpruntsDuMois() {
        DB db = new DB();
        try {
            ResultSet rs = db.select("SELECT COUNT(*) FROM emprunts WHERE MONTH(date_emprunt) = MONTH(NOW()) AND YEAR(date_emprunt) = YEAR(NOW())");
            if (rs != null && rs.next()) return rs.getInt(1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return 0;
    }

    public int getNombreRetards() {
        DB db = new DB();
        try {
            ResultSet rs = db.select("SELECT COUNT(*) FROM emprunts WHERE date_retour_effective IS NULL AND date_retour_prevue < CURDATE()");
            if (rs != null && rs.next()) return rs.getInt(1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return 0;
    }

    private List<Emprunt> chargerEmprunts(String sql) {
        DB db = new DB();
        List<Emprunt> liste = new ArrayList<>();
        try {
            ResultSet rs = db.select(sql);
            while (rs != null && rs.next()) liste.add(extraireEmprunt(rs));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return liste;
    }

    private Emprunt extraireEmprunt(ResultSet rs) throws Exception {
        Emprunt e = new Emprunt();
        e.setId(rs.getInt("id"));

        Livre livre = new Livre();
        livre.setId(rs.getInt("livre_id"));
        livre.setTitre(rs.getString("livre_titre"));
        livre.setAuteur(rs.getString("livre_auteur"));
        e.setLivre(livre);

        Adherent adherent = new Adherent();
        adherent.setId(rs.getInt("adherent_id"));
        adherent.setNom(rs.getString("adh_nom"));
        adherent.setPrenom(rs.getString("adh_prenom"));
        adherent.setMatricule(rs.getString("adh_matricule"));
        e.setAdherent(adherent);

        if (rs.getDate("date_emprunt")      != null) e.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
        if (rs.getDate("date_retour_prevue") != null) e.setDateRetourPrevue(rs.getDate("date_retour_prevue").toLocalDate());
        if (rs.getDate("date_retour_effective") != null) e.setDateRetourEffective(rs.getDate("date_retour_effective").toLocalDate());
        e.setPenalite(rs.getDouble("penalite"));
        return e;
    }
}
