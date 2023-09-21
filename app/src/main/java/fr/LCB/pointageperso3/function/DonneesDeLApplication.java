package fr.LCB.pointageperso3.function;

import fr.LCB.pointageperso3.Entity.ConfigAppli;
import fr.LCB.pointageperso3.Entity.Lieu;
import fr.LCB.pointageperso3.Entity.Pointage;
import fr.LCB.pointageperso3.Entity.Societe;
import fr.LCB.pointageperso3.Entity.User;

import java.util.ArrayList;

public class DonneesDeLApplication {

    static User utilisateur;
    static Societe societe;
    static ConfigAppli confAppli;
    static ArrayList<User> listeDesUtilisateurs;
    static ArrayList<Pointage> listeDePointages;
    static ArrayList<Lieu> listeDeLieux;

    public DonneesDeLApplication(){}

    public static User getUtilisateur() {
        return utilisateur;
    }

    public static void setUtilisateur(User utilisateur) {
        DonneesDeLApplication.utilisateur = utilisateur;
    }

    public static Societe getSociete() {
        return societe;
    }

    public static void setSociete(Societe societe) {
        DonneesDeLApplication.societe = societe;
    }

    public static ConfigAppli getConfAppli() {
        return confAppli;
    }

    public static void setConfAppli(ConfigAppli confAppli) {
        DonneesDeLApplication.confAppli = confAppli;
    }

    public static ArrayList<User> getListeDesUtilisateurs() {
        return listeDesUtilisateurs;
    }

    public static void setListeDesUtilisateurs(ArrayList<User> listeDesUtilisateurs) {
        DonneesDeLApplication.listeDesUtilisateurs = listeDesUtilisateurs;
    }

    public static ArrayList<Pointage> getListeDePointages() {
        return listeDePointages;
    }

    public static void setListeDePointages(ArrayList<Pointage> listeDePointages) {
        DonneesDeLApplication.listeDePointages = listeDePointages;
    }

    public static ArrayList<Lieu> getListeDeLieux() {
        return listeDeLieux;
    }

    public static void setListeDeLieux(ArrayList<Lieu> listeDeLieux) {
        DonneesDeLApplication.listeDeLieux = listeDeLieux;
    }
}
