package com.example.bibliotheque_municipale.modele;

public class Utilisateur {

    private int id;
    private String login;
    private String motDePasse;
    private String nom;
    private String prenom;
    private String email;
    private String profil;
    private boolean actif;
    private boolean premiereConnexion;

    public Utilisateur() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public boolean isPremiereConnexion() { return premiereConnexion; }
    public void setPremiereConnexion(boolean b) { this.premiereConnexion = b; }

    @Override
    public String toString() { return prenom + " " + nom; }
}
