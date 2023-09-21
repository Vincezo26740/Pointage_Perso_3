package fr.LCB.pointageperso3.Threads;

import fr.LCB.pointageperso3.DAO.AccesBDD;
import fr.LCB.pointageperso3.DAO.PersoDatabase;
import fr.LCB.pointageperso3.Entity.ConfigAppli;

import java.util.concurrent.CountDownLatch;

public class InfoConfigAppli extends Thread {
    private ConfigAppli infoConfigAppli;
    private final CountDownLatch retourConfig;

    public InfoConfigAppli(CountDownLatch retourConfig) {this.retourConfig=retourConfig; }

    @Override
    public void run() {
        super.run();
        PersoDatabase accesBdd = AccesBDD.getConnexionBDD();
        infoConfigAppli = accesBdd.DaoConfigAppli().presenceConfig();
        AccesBDD.close();
        retourConfig.countDown();
    }

    public ConfigAppli getInfoConfigAppli() {
        return infoConfigAppli;
    }
}
