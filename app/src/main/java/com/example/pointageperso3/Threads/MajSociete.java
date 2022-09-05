package com.example.pointageperso3.Threads;

import com.example.pointageperso3.DAO.AccesBDD;
import com.example.pointageperso3.DAO.PersoDatabase;
import com.example.pointageperso3.Entity.Societe;


public class MajSociete extends Thread {

    private final Societe societe;

    public MajSociete(Societe societe) {
        this.societe = societe;
    }

    @Override
    public void run() {
        super.run();
        PersoDatabase ajoutSocieteBdd = AccesBDD.getConnexionBDD();
        ajoutSocieteBdd.DaoSociete().suppressionSociete();
        ajoutSocieteBdd.DaoSociete().insert(societe);
        ajoutSocieteBdd.close();
    }
}
