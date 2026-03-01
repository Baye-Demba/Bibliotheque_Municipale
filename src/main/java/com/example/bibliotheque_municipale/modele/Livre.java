package com.example.bibliotheque_municipale.modele;

public class Livre {

    private int id;
    private String isbn;
    private String titre;
    private String auteur;
    private Categorie categorie;
    private int anneePublication;
    private int nombreExemplaires;
    private boolean disponible;

    public Livre() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public int getAnneePublication() { return anneePublication; }
    public void setAnneePublication(int anneePublication) { this.anneePublication = anneePublication; }

    public int getNombreExemplaires() { return nombreExemplaires; }
    public void setNombreExemplaires(int n) { this.nombreExemplaires = n; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getNomCategorie() { return categorie != null ? categorie.getLibelle() : ""; }
    public String getDisponibiliteAffichage() { return disponible ? "Disponible" : "Indisponible"; }

    @Override
    public String toString() { return titre + " - " + auteur; }
}
