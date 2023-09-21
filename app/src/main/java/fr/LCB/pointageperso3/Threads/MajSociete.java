package fr.LCB.pointageperso3.Threads;

import fr.LCB.pointageperso3.DAO.AccesBDD;
import fr.LCB.pointageperso3.DAO.PersoDatabase;
import fr.LCB.pointageperso3.Entity.Societe;


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
        AccesBDD.close();
    }
}
