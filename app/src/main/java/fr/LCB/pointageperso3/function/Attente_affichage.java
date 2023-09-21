package fr.LCB.pointageperso3.function;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import fr.LCB.pointageperso3.R;
import fr.LCB.pointageperso3.Threads.ControleUser;
import fr.LCB.pointageperso3.Threads.EnvoieMail;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Attente_affichage extends AppCompatActivity {

    private View rotation;
    private TextView tvWait;
    private final String TAG = String.valueOf(R.string.TAG);

    // Récupération Intent
    private  String user, password, classInit, classEchec, classReussite,  fonction;

    private  CountDownLatch countDownReussiteTraitement, countDownEchecTraitement;
    private  Intent intent;
    private  boolean passage = false;
    private int tempo = 300;
    private EnvoieMail testEnvoieMail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.affichage_attente);
    }

    private void recupIntent(){
//        Log.i(TAG, "recupIntent: test TAG valeur");
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");
        classInit = getIntent().getStringExtra("class");
        classEchec = getIntent().getStringExtra("classEchec");
        classReussite = getIntent().getStringExtra("classReussite");
        fonction = getIntent().getStringExtra("fonction");

    }

    @Override
    protected void onResume() {
        super.onResume();
        recupIntent();
        countDownReussiteTraitement = new CountDownLatch(1);
        countDownEchecTraitement = new CountDownLatch(1);
        setContentView(R.layout.affichage_attente);
        rotation = findViewById(R.id.wait_rotation);
        tvWait = findViewById(R.id.tv_wait);
        //lancement des traitement
        lancement();
    }

    public void lancement() {
        new Thread(reglageClass).start();
        new Thread(affichageFlecheAttente).start();
        new Thread(decompteTemps).start();
    }

    Thread reglageClass = new Thread(new Runnable() {
        @Override
        public void run() {
            //traitement des lancement à faire en fonction des besoins
            //Réglage des class a appelée en fonction du résultat (réussite / échec)
            //classEchec correspond à un échec, on retourne à la classe précédente
            if (classEchec.equals(EcranDeConnexion.class.getName())) {
                ControleUser test = new ControleUser(user, password, countDownReussiteTraitement, countDownEchecTraitement);
                new Thread(test).start();
            }
            if (classEchec.equals(PointageFunction.class.getName())) {
                if (Objects.equals(fonction, "envoieEmail")) {
                    testEnvoieMail = new EnvoieMail(user, countDownReussiteTraitement);
                    creationFichier();
                }
            }
        }
    });

    private void creationFichier() {
        ExecutorService creationFichier = Executors.newSingleThreadExecutor();
        creationFichier.execute(() -> {
            new Thread(testEnvoieMail).start();
        });
        try {
            countDownReussiteTraitement.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Thread actionsCountDown = new Thread(() -> {
        if (countDownEchecTraitement.getCount() == 0) {
//
            passage = true;
            retourClass();
        }
        if (countDownReussiteTraitement.getCount() == 0) {
//
            passage = true;
            reussite();
        }
        if (tempo == 0) {
            passage = true;
            retourClass();
            countDownEchecTraitement.countDown();
        }
    });

    Thread affichageFlecheAttente = new Thread(new Runnable() {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    rotation.setRotation(rotation.getRotation() - 1);
                });
            }
            while (!passage);
        }
    });

    Thread decompteTemps = new Thread(new Runnable() {
        @Override
        public void run() {
            do {
                new Thread(actionsCountDown).start();
//                Log.i(TAG, "decompte: " + tempo);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tempo--;
            } while (!passage);
        }
    });


    public void retourClass() {
        try {
            intent = new Intent(this, Class.forName(classEchec));

            intent.putExtra("user",user);
            intent.putExtra("password","");
            intent.putExtra("class",getClass().getName());
            intent.putExtra("classEchec","");
            intent.putExtra("classReussite","");
            intent.putExtra("fonction","");

            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void reussite() {
        try {
            intent = new Intent(this, Class.forName(classReussite));

            intent.putExtra("user",user);
            intent.putExtra("password","");
            intent.putExtra("class",getClass().getName());
            intent.putExtra("classEchec","");
            intent.putExtra("classReussite","");
            if (classEchec.equals(PointageFunction.class.getName())) {
                intent.putExtra("fonction", "envoieEmail");
            } else {
                intent.putExtra("fonction", "");
            }

            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
