package com.example.pointageperso3.Threads;

import com.example.pointageperso3.DAO.AccesBDD;
import com.example.pointageperso3.DAO.PersoDatabase;
import com.example.pointageperso3.Entity.Lieu;

public class AjoutLieu extends Thread {

    private final Lieu lieu;

    public AjoutLieu( Lieu lieuAAjouter) {
        this.lieu = lieuAAjouter;
    }

    @Override
    public void run() {
        super.run();
        PersoDatabase ajoutLieuBdd = AccesBDD.getConnexionBDD();
        ajoutLieuBdd.DaoLieu().insert(lieu);
    }
}
