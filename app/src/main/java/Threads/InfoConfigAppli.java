package Threads;

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.ConfigAppli;

import java.util.concurrent.CountDownLatch;

public class InfoConfigAppli extends Thread {
    private  ConfigAppli infoConfigAppli;
    private final CountDownLatch retourConfig;

    public InfoConfigAppli(CountDownLatch retourConfig) {this.retourConfig=retourConfig; }

    @Override
    public void run() {
        super.run();
        PersoDatabase accesBdd = AccesBDD.getConnexionBDD();
        infoConfigAppli = accesBdd.DaoConfigAppli().presenceConfig();
        accesBdd.close();
        retourConfig.countDown();
    }

    public ConfigAppli getInfoConfigAppli() {
        return infoConfigAppli;
    }
}
