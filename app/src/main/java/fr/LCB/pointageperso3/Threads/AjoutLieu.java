package fr.LCB.pointageperso3.Threads;

import fr.LCB.pointageperso3.DAO.AccesBDD;
import fr.LCB.pointageperso3.DAO.PersoDatabase;
import fr.LCB.pointageperso3.Entity.Lieu;

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
