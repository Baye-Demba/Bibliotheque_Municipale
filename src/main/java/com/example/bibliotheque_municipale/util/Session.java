package com.example.bibliotheque_municipale.util;

import com.example.bibliotheque_municipale.modele.Utilisateur;

// on garde l'utilisateur connecte en memoire
public class Session {

    private static Utilisateur utilisateurConnecte = null;

    public static void setUtilisateurConnecte(Utilisateur u) {
        utilisateurConnecte = u;
    }

    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static void vider() {
        utilisateurConnecte = null;
    }

    public static boolean estAdmin() {
        return utilisateurConnecte != null && utilisateurConnecte.getProfil().equals("ADMIN");
    }
}
