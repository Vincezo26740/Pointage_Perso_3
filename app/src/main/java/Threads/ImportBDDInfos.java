package Threads;

import com.example.pointageperso3.EcranDeConnexion;
import com.example.pointageperso3.infosAppliTest;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.ConfigAppli;
import Entity.Lieu;
import Entity.Pointage;
import Entity.Societe;
import Entity.User;

public class ImportBDDInfos implements Runnable {

    CountDownLatch finDuTravaildImport;
    User utilisateur;
    Societe societe;
    ConfigAppli confAppli;
    ArrayList<User> listeDesUtilisateurs;
    ArrayList<Pointage> listeDePointages;
    ArrayList<Lieu> listeDeLieux;
    infosAppliTest recupInfos = new infosAppliTest();

    public ImportBDDInfos(CountDownLatch finDesImports
    ) {
        finDuTravaildImport = finDesImports;
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
        listeDePointages = (ArrayList<Pointage>) accesDatabase.DaoPointage().findAll();
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
        finDuTravaildImport.countDown();
        accesDatabase.close();
        recupInfos.setConfAppli(confAppli);
        recupInfos.setUtilisateur(utilisateur);
        recupInfos.setSociete(societe);
        recupInfos.setListeDesUtilisateurs(listeDesUtilisateurs);
        recupInfos.setListeDePointages(listeDePointages);
        recupInfos.setListeDeLieux(listeDeLieux);
    }

}
