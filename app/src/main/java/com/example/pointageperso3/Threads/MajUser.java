package com.example.pointageperso3.Threads;

import com.example.pointageperso3.DAO.AccesBDD;
import com.example.pointageperso3.DAO.PersoDatabase;
import com.example.pointageperso3.Entity.User;

public class MajUser extends Thread {
    String mdp;
    User userToChange;
    PersoDatabase BDDAcces;

    public MajUser(User userToChange) {
        this.userToChange = userToChange;
    }


    public MajUser(User userToChange, String MDP) {
        this.userToChange = userToChange;
        this.mdp = MDP;
    }

    @Override
    public void run() {
        super.run();
        BDDAcces = AccesBDD.getConnexionBDD();
        BDDAcces.DaoUser().updateUser(userToChange); // réinsert user

//        new Thread(testT).start();
    }

    Thread testT = new Thread(() -> {
        BDDAcces = AccesBDD.getConnexionBDD();
        BDDAcces.DaoUser().updateUser(userToChange); // réinsert user
    });
}
