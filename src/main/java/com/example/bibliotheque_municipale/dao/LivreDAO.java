package com.example.bibliotheque_municipale.dao;

import com.example.bibliotheque_municipale.modele.Categorie;
import com.example.bibliotheque_municipale.modele.Livre;
import com.example.bibliotheque_municipale.util.DB;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {

    public List<Livre> getTousLesLivres() {
        DB db = new DB();
        List<Livre> liste = new ArrayList<>();
        try {
            ResultSet rs = db.select(
                    "SELECT l.*, c.id as cat_id, c.libelle as cat_libelle, c.description as cat_desc " +
                            "FROM livres l LEFT JOIN categories c ON l.categorie_id = c.id ORDER BY l.titre"
            );
            while (rs != null && rs.next()) liste.add(extraireLivre(rs));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return liste;
    }

    public List<Livre> rechercher(String motCle) {
        DB db = new DB();
        List<Livre> liste = new ArrayList<>();
        try {
            String s = "%" + motCle + "%";
            ResultSet rs = db.select(
                    "SELECT l.*, c.id as cat_id, c.libelle as cat_libelle, c.description as cat_desc " +
                            "FROM livres l LEFT JOIN categories c ON l.categorie_id = c.id " +
                            "WHERE l.titre LIKE ? OR l.auteur LIKE ? OR l.isbn LIKE ?",
                    s, s, s
            );
            while (rs != null && rs.next()) liste.add(extraireLivre(rs));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return liste;
    }

    public int ajouter(Livre l) {
        DB db = new DB();
        try {
            // on insere d'abord sans isbn pour recuperer l'id genere
            int res = db.maj(
                    "INSERT INTO livres (titre, auteur, categorie_id, annee_publication, nombre_exemplaires, disponible) VALUES (?,?,?,?,?,1)",
                    l.getTitre(), l.getAuteur(), l.getCategorie().getId(), l.getAnneePublication(), l.getNombreExemplaires()
            );
            if (res > 0) {
                // recuperer le dernier id insere
                ResultSet rs = db.select("SELECT LAST_INSERT_ID() as last_id");
                if (rs != null && rs.next()) {
                    int newId = rs.getInt("last_id");
                    // si isbn vide ou null on genere automatiquement
                    String isbn = (l.getIsbn() == null || l.getIsbn().trim().isEmpty())
                            ? "sans-isbn-" + newId
                            : l.getIsbn().trim();
                    db.maj("UPDATE livres SET isbn = ? WHERE id = ?", isbn, newId);
                }
            }
            return res;
        } catch (Exception ex) {
            System.out.println("Erreur ajouter livre : " + ex.getMessage());
            return 0;
        } finally {
            db.fermer();
        }
    }

    public int modifier(Livre l) {
        DB db = new DB();
        try {
            // si isbn vide on garde "sans-isbn-id"
            String isbn = (l.getIsbn() == null || l.getIsbn().trim().isEmpty())
                    ? "sans-isbn-" + l.getId()
                    : l.getIsbn().trim();
            return db.maj(
                    "UPDATE livres SET isbn=?, titre=?, auteur=?, categorie_id=?, annee_publication=?, nombre_exemplaires=? WHERE id=?",
                    isbn, l.getTitre(), l.getAuteur(), l.getCategorie().getId(), l.getAnneePublication(), l.getNombreExemplaires(), l.getId()
            );
        } catch (Exception ex) {
            System.out.println("Erreur modifier livre : " + ex.getMessage());
            return 0;
        } finally {
            db.fermer();
        }
    }

    public int supprimer(int id) {
        DB db = new DB();
        try {
            return db.maj("DELETE FROM livres WHERE id = ?", id);
        } finally {
            db.fermer();
        }
    }

    public int getNombreTotalLivres() {
        DB db = new DB();
        try {
            ResultSet rs = db.select("SELECT COUNT(*) FROM livres");
            if (rs != null && rs.next()) return rs.getInt(1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return 0;
    }

    private Livre extraireLivre(ResultSet rs) throws Exception {
        Livre l = new Livre();
        l.setId(rs.getInt("id"));
        l.setIsbn(rs.getString("isbn"));
        l.setTitre(rs.getString("titre"));
        l.setAuteur(rs.getString("auteur"));
        l.setAnneePublication(rs.getInt("annee_publication"));
        l.setNombreExemplaires(rs.getInt("nombre_exemplaires"));
        l.setDisponible(rs.getBoolean("disponible"));
        Categorie cat = new Categorie(rs.getInt("cat_id"), rs.getString("cat_libelle"), rs.getString("cat_desc"));
        l.setCategorie(cat);
        return l;
    }
}
