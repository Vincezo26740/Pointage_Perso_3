package Threads;

import DAO.AccesBDD;
import DAO.PersoDatabase;


import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import Entity.User;

public class RecupUserBDD extends Thread {
    private final String name;
    private User utilisateurTrouve;
    private CountDownLatch latchValidation;

    public RecupUserBDD(String name) {
        this.name = name;
    }

    public RecupUserBDD(String name, CountDownLatch latchValidation) {
        this.name = name;
        this.latchValidation = latchValidation;
    }

    @Override
    public void run() {
        super.run();
        PersoDatabase accesBdd = AccesBDD.getConnexionBDD();
        utilisateurTrouve = accesBdd.DaoUser().rechercheUser(name);
        ArrayList<User> infoUser = (ArrayList<User>) accesBdd.DaoUser().findAll();
//        Log.i("Recup User VV", String.valueOf(infoUser));
        accesBdd.close();
        if (latchValidation != null) {
            latchValidation.countDown();
        }
    }

    public User retourUser() {
        if (utilisateurTrouve != null) {
            return utilisateurTrouve;
        } else {
            return new User("null",
                    "null", "null", 0,
                    "null",
                    "null");
        }
    }
}