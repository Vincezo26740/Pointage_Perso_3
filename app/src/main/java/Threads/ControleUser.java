package Threads;

import com.example.pointageperso3.R;


import java.util.concurrent.CountDownLatch;

import at.favre.lib.crypto.bcrypt.BCrypt;
import Entity.User;

public class ControleUser extends Thread {
    private static final String TAG = String.valueOf(R.string.TAG);
    private final String name;
    private String PWDClair;
    boolean validate = false;
    private User utilisateurBDD;
    private CountDownLatch latch, latchReussite, latchEchec, recupUser;

    public ControleUser(String name) {
        this.name = name;
    }

    public ControleUser(String name, String PWDClair, CountDownLatch latch) {
        this.name = name;
        this.PWDClair = PWDClair;
        this.latch = latch;
    }

    public ControleUser(String name, String PWDClair, CountDownLatch latchReussite, CountDownLatch latchEchec) {
        this.name = name;
        this.PWDClair = PWDClair;
        this.latchReussite = latchReussite;
        this.latchEchec = latchEchec;
    }

    @Override
    public void run() {
        super.run();
        recupUser = new CountDownLatch(1);
        RecupUserBDD recupUserBDD = new RecupUserBDD(name, recupUser);
        new Thread(recupUserBDD).start();
        try {
            recupUser.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        utilisateurBDD = recupUserBDD.retourUser();

        if (!utilisateurBDD.getName().equals("null") && !PWDClair.equals("")) {
            validate = BCrypt.verifyer().verify(PWDClair.toCharArray(), utilisateurBDD.getPW()).verified;
            //validation du countDown si controle du MDP OK
        }
        if (validate) {
            latchReussite.countDown();
        } else if (latchEchec != null) {
            latchEchec.countDown();
        }
        if (latch != null) {
            latch.countDown();
        }
    }

    public User retourUser() {
        return utilisateurBDD;
    }
}
