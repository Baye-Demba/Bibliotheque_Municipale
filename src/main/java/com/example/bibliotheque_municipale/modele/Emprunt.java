package com.example.bibliotheque_municipale.modele;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprunt {

    private int id;
    private Livre livre;
    private Adherent adherent;
    private Utilisateur utilisateur;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private double penalite;

    // 500 FCFA par jour de retard
    private static final double TARIF_RETARD = 500.0;

    public Emprunt() {}

    // calcul automatique selon les jours de retard
    public double calculerPenalite() {
        LocalDate dateRef = dateRetourEffective != null ? dateRetourEffective : LocalDate.now();
        if (dateRef.isAfter(dateRetourPrevue)) {
            long jours = ChronoUnit.DAYS.between(dateRetourPrevue, dateRef);
            return jours * TARIF_RETARD;
        }
        return 0.0;
    }

    public boolean estEnRetard() {
        if (dateRetourEffective != null) return false;
        return LocalDate.now().isAfter(dateRetourPrevue);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }

    public Adherent getAdherent() { return adherent; }
    public void setAdherent(Adherent adherent) { this.adherent = adherent; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate d) { this.dateEmprunt = d; }

    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDate d) { this.dateRetourPrevue = d; }

    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public void setDateRetourEffective(LocalDate d) { this.dateRetourEffective = d; }

    public double getPenalite() { return penalite; }
    public void setPenalite(double penalite) { this.penalite = penalite; }

    public String getTitreLivre() { return livre != null ? livre.getTitre() : ""; }
    public String getNomAdherent() { return adherent != null ? adherent.getPrenom() + " " + adherent.getNom() : ""; }

    public String getStatut() {
        if (dateRetourEffective != null) return "Retourne";
        if (estEnRetard()) return "En retard";
        return "En cours";
    }
}
