package com.example.pointageperso3;

import java.util.ArrayList;

import Entity.ConfigAppli;
import Entity.Lieu;
import Entity.Pointage;
import Entity.Societe;
import Entity.User;

public class infosAppliTest {

    static User utilisateur;
    static Societe societe;
    static ConfigAppli confAppli;
    static ArrayList<User> listeDesUtilisateurs;
    static ArrayList<Pointage> listeDePointages;
    static ArrayList<Lieu> listeDeLieux;

    public infosAppliTest(){}

    public static User getUtilisateur() {
        return utilisateur;
    }

    public static void setUtilisateur(User utilisateur) {
        infosAppliTest.utilisateur = utilisateur;
    }

    public static Societe getSociete() {
        return societe;
    }

    public static void setSociete(Societe societe) {
        infosAppliTest.societe = societe;
    }

    public static ConfigAppli getConfAppli() {
        return confAppli;
    }

    public static void setConfAppli(ConfigAppli confAppli) {
        infosAppliTest.confAppli = confAppli;
    }

    public static ArrayList<User> getListeDesUtilisateurs() {
        return listeDesUtilisateurs;
    }

    public static void setListeDesUtilisateurs(ArrayList<User> listeDesUtilisateurs) {
        infosAppliTest.listeDesUtilisateurs = listeDesUtilisateurs;
    }

    public static ArrayList<Pointage> getListeDePointages() {
        return listeDePointages;
    }

    public static void setListeDePointages(ArrayList<Pointage> listeDePointages) {
        infosAppliTest.listeDePointages = listeDePointages;
    }

    public static ArrayList<Lieu> getListeDeLieux() {
        return listeDeLieux;
    }

    public static void setListeDeLieux(ArrayList<Lieu> listeDeLieux) {
        infosAppliTest.listeDeLieux = listeDeLieux;
    }
}
