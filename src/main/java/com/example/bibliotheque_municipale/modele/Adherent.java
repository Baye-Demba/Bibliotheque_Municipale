package com.example.bibliotheque_municipale.modele;

import java.time.LocalDate;

public class Adherent {

    private int id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate dateInscription;
    private boolean actif;

    public Adherent() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate d) { this.dateInscription = d; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getStatutAffichage() { return actif ? "Actif" : "Suspendu"; }

    @Override
    public String toString() { return matricule + " - " + prenom + " " + nom; }
}
