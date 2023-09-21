package fr.LCB.pointageperso3.Threads;

import fr.LCB.pointageperso3.DAO.AccesBDD;
import fr.LCB.pointageperso3.DAO.PersoDatabase;
import fr.LCB.pointageperso3.Entity.Societe;


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
