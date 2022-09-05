package com.example.pointageperso3.Threads;

import com.example.pointageperso3.DAO.AccesBDD;
import com.example.pointageperso3.DAO.PersoDatabase;
import com.example.pointageperso3.Entity.Lieu;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class RecupLieu extends Thread {

    private ArrayList<Lieu> tableauLieuPointage;
    private CountDownLatch countDownLatch;

    public RecupLieu(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        super.run();
        dbb.run();
    }

    Thread dbb = new Thread(() -> {
        PersoDatabase BDD = AccesBDD.getConnexionBDD();
        tableauLieuPointage = (ArrayList<Lieu>) BDD.DaoLieu().findAll();
        BDD.close();
        countDownLatch.countDown();
    });

    public ArrayList<Lieu> recupLieux() {
        try {
            dbb.start();
        } catch (Exception e) {
        }
        try {
            dbb.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tableauLieuPointage.size() == 0) {
            Lieu lieu = new Lieu(0, "vide", "vide", "vide");
            tableauLieuPointage.add(lieu);
        }
        return tableauLieuPointage;
    }
}
