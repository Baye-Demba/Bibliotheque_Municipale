package com.example.bibliotheque_municipale.dao;

import com.example.bibliotheque_municipale.modele.Categorie;
import com.example.bibliotheque_municipale.util.DB;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAO {

    public List<Categorie> getToutesLesCategories() {
        DB db = new DB();
        List<Categorie> liste = new ArrayList<>();
        try {
            ResultSet rs = db.select("SELECT * FROM categories ORDER BY libelle");
            while (rs != null && rs.next()) {
                liste.add(new Categorie(rs.getInt("id"), rs.getString("libelle"), rs.getString("description")));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            db.fermer();
        }
        return liste;
    }

    public int ajouter(Categorie c) {
        DB db = new DB();
        try {
            return db.maj("INSERT INTO categories (libelle, description) VALUES (?, ?)", c.getLibelle(), c.getDescription());
        } finally {
            db.fermer();
        }
    }

    public int modifier(Categorie c) {
        DB db = new DB();
        try {
            return db.maj("UPDATE categories SET libelle = ?, description = ? WHERE id = ?", c.getLibelle(), c.getDescription(), c.getId());
        } finally {
            db.fermer();
        }
    }

    public int supprimer(int id) {
        DB db = new DB();
        try {
            return db.maj("DELETE FROM categories WHERE id = ?", id);
        } finally {
            db.fermer();
        }
    }
}
