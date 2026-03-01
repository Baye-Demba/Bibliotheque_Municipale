package com.example.bibliotheque_municipale.dao;

import com.example.bibliotheque_municipale.modele.Adherent;
import com.example.bibliotheque_municipale.util.DB;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdherentDAO {

    public List<Adherent> getTousLesAdherents() {
        DB db = new DB();
        List<Adherent> liste = new ArrayList<>();
        try {
            ResultSet rs = db.select("SELECT * FROM adherents ORDER BY nom");
            while (rs != null && rs.next()) liste.add(extraireAdherent(rs));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return liste;
    }

    public int ajouter(Adherent a) {
        DB db = new DB();
        try {
            String matricule = genererMatricule();
            return db.maj(
                "INSERT INTO adherents (matricule, nom, prenom, email, telephone, adresse, date_inscription, actif) VALUES (?,?,?,?,?,?,NOW(),1)",
                matricule, a.getNom(), a.getPrenom(), a.getEmail(), a.getTelephone(), a.getAdresse()
            );
        } finally {
            db.fermer();
        }
    }

    public int modifier(Adherent a) {
        DB db = new DB();
        try {
            return db.maj(
                "UPDATE adherents SET nom=?, prenom=?, email=?, telephone=?, adresse=? WHERE id=?",
                a.getNom(), a.getPrenom(), a.getEmail(), a.getTelephone(), a.getAdresse(), a.getId()
            );
        } finally {
            db.fermer();
        }
    }

    public int changerStatut(int id, boolean actif) {
        DB db = new DB();
        try {
            return db.maj("UPDATE adherents SET actif = ? WHERE id = ?", actif, id);
        } finally {
            db.fermer();
        }
    }

    public int getNombreTotalAdherents() {
        DB db = new DB();
        try {
            ResultSet rs = db.select("SELECT COUNT(*) FROM adherents WHERE actif = 1");
            if (rs != null && rs.next()) return rs.getInt(1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return 0;
    }

    private String genererMatricule() {
        DB db = new DB();
        try {
            int annee = LocalDate.now().getYear();
            ResultSet rs = db.select("SELECT COUNT(*) FROM adherents WHERE YEAR(date_inscription) = ?", annee);
            if (rs != null && rs.next()) {
                int num = rs.getInt(1) + 1;
                return String.format("ADH-%d-%03d", annee, num);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return "ADH-" + System.currentTimeMillis();
    }

    private Adherent extraireAdherent(ResultSet rs) throws Exception {
        Adherent a = new Adherent();
        a.setId(rs.getInt("id"));
        a.setMatricule(rs.getString("matricule"));
        a.setNom(rs.getString("nom"));
        a.setPrenom(rs.getString("prenom"));
        a.setEmail(rs.getString("email"));
        a.setTelephone(rs.getString("telephone"));
        a.setAdresse(rs.getString("adresse"));
        if (rs.getDate("date_inscription") != null)
            a.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        a.setActif(rs.getBoolean("actif"));
        return a;
    }
}
