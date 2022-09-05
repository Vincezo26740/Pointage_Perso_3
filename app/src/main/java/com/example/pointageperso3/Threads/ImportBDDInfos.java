package com.example.pointageperso3.Threads;

import com.example.pointageperso3.DAO.AccesBDD;
import com.example.pointageperso3.DAO.PersoDatabase;
import com.example.pointageperso3.Entity.ConfigAppli;
import com.example.pointageperso3.Entity.Lieu;
import com.example.pointageperso3.Entity.Pointage;
import com.example.pointageperso3.Entity.Societe;
import com.example.pointageperso3.Entity.User;
import com.example.pointageperso3.function.DonneesDeLApplication;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ImportBDDInfos implements Runnable {

    CountDownLatch finDuTravaildImport;
    User utilisateur;
    Societe societe;
    ConfigAppli confAppli;
    ArrayList<User> listeDesUtilisateurs;
    ArrayList<Pointage> listeDePointages;
    ArrayList<Lieu> listeDeLieux;


    public ImportBDDInfos(CountDownLatch finDesImports
    ) {
        finDuTravaildImport = finDesImports;
    }

    public ImportBDDInfos() {
        finDuTravaildImport = new CountDownLatch(6);
    }

    @Override
    public void run() {

        PersoDatabase accesDatabase = AccesBDD.getConnexionBDD();

        confAppli = accesDatabase.DaoConfigAppli().presenceConfig();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finDuTravaildImport.countDown();
        listeDesUtilisateurs = (ArrayList<User>) accesDatabase.DaoUser().findAll();
        if (listeDesUtilisateurs.size() > 1) {
            for (int i = listeDesUtilisateurs.size() - 1; 1 == i; i--) {
                AccesBDD.getConnexionBDD().DaoUser().deleteUser(listeDesUtilisateurs.get(i));
                AccesBDD.getConnexionBDD().close();
                listeDesUtilisateurs.remove(i);
            }
            listeDesUtilisateurs = (ArrayList<User>) accesDatabase.DaoUser().findAll();
        }
        if (listeDesUtilisateurs.size() == 1) {
            utilisateur = listeDesUtilisateurs.get(0);
        }

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finDuTravaildImport.countDown();
        societe = accesDatabase.DaoSociete().findSociete();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finDuTravaildImport.countDown();
        listeDePointages = (ArrayList<Pointage>) accesDatabase.DaoPointage().findAllLimit(50);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finDuTravaildImport.countDown();
        listeDeLieux = (ArrayList<Lieu>) accesDatabase.DaoLieu().findAll();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finDuTravaildImport.countDown();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        accesDatabase.close();
        DonneesDeLApplication.setConfAppli(confAppli);
        DonneesDeLApplication.setUtilisateur(utilisateur);
        DonneesDeLApplication.setSociete(societe);
        DonneesDeLApplication.setListeDesUtilisateurs(listeDesUtilisateurs);
        DonneesDeLApplication.setListeDePointages(listeDePointages);
        DonneesDeLApplication.setListeDeLieux(listeDeLieux);

        finDuTravaildImport.countDown();
        if (finDuTravaildImport.getCount() > 0) {
            do {
                finDuTravaildImport.countDown();
            } while (finDuTravaildImport.getCount() > 0);
        }
    }

}
