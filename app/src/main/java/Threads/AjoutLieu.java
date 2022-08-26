package Threads;

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.Lieu;

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
