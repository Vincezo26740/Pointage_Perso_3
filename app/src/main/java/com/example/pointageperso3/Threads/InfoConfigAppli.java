package com.example.pointageperso3.Threads;

import java.util.concurrent.CountDownLatch;

import com.example.pointageperso3.DAO.AccesBDD;
import com.example.pointageperso3.DAO.PersoDatabase;
import com.example.pointageperso3.Entity.ConfigAppli;

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
