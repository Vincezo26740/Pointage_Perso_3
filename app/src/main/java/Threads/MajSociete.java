package Threads;

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.Societe;


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
