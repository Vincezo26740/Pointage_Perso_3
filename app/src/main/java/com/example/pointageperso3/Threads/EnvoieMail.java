package com.example.pointageperso3.Threads;

import com.example.pointageperso3.function.MonAppContext;
import com.example.pointageperso3.R;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import com.example.pointageperso3.DAO.AccesBDD;
import com.example.pointageperso3.Entity.Societe;
import com.example.pointageperso3.Entity.User;

public class EnvoieMail extends Thread {
    private static File pathFichier;
    private static String getDonneesString;
    private final CountDownLatch travailFini = new CountDownLatch(1);
    private final CountDownLatch recCDL;
    private final User user;
    private static Societe societe = null;
    //    String pathFichier;
    private final CreationFichier demarrageFichier;

    public EnvoieMail(String userName, CountDownLatch CDL) {
        recCDL = CDL;
        user = AccesBDD.getConnexionBDD().DaoUser().rechercheUser(userName);
        societe = AccesBDD.getConnexionBDD().DaoSociete().findSociete();
        AccesBDD.getConnexionBDD().close();
        demarrageFichier = new CreationFichier(travailFini);
        new Thread(demarrageFichier).start();
        try {
            travailFini.await();
            recCDL.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pathFichier = demarrageFichier.getNewPath();
        getDonneesString = MonAppContext.context.getString(R.string.envoie_des_pointages) + user.getName();
    }

    public static File getPathFichier() {
        return pathFichier;
    }

    public String getUserString() {
        return user.getName();
    }

    public static String getEmailSociete1String() {

        if (societe != null) {
            if (societe.getEmail() != null) {
                return societe.getEmail();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getEmailSociete2String() {
        if (societe != null) {
            if (societe.getEmail2() != null) {
                return societe.getEmail2();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getEmailSociete3String() {
        if (societe != null) {
            if (societe.getEmail3() != null) {
                return societe.getEmail3();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
}
