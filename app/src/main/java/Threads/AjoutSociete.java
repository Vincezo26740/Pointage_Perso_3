package Threads;

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.Societe;


public class AjoutSociete extends Thread {

    private final Societe societe;

    public AjoutSociete(Societe societe) {
        this.societe = societe;
    }

    @Override
    public void run() {
        super.run();
        PersoDatabase ajoutSocieteBdd = AccesBDD.getConnexionBDD();
        ajoutSocieteBdd.DaoSociete().insert(societe);
        ajoutSocieteBdd.close();
    }
}
