package com.example.pointageperso3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CountDownLatch;

import Threads.ImportBDDInfos;

public class Chargement_appli extends AppCompatActivity {

    CountDownLatch finDuTravaildImport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finDuTravaildImport = new CountDownLatch(6);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.chargement_application_values);
        TextView affichageText = findViewById(R.id.chargement_app_text);
        affichageText.setText("Chargement Logiciel");
        new Thread(test).start();
    }

    Thread test = new Thread(new Runnable() {
        @Override
        public void run() {
            ImportBDDInfos importInformationAppli = new ImportBDDInfos(finDuTravaildImport);
            TextView affichageText = findViewById(R.id.chargement_app_text);
            String chargementConfig = "Chargement Logiciel\n" +
                    "Chargement de la configuration";
            String chargementConfigUser = "Chargement Logiciel\n" +
                    "Chargement de la configuration\n" +
                    "Chargement utilisateurs";
            String chargementConfigUserSociete = "Chargement Logiciel\n" +
                    "Chargement de la configuration\n" +
                    "Chargement utilisateurs\n" +
                    "Chargement societe";
            String chargementConfigUserSocietePointages = "Chargement Logiciel\n" +
                    "Chargement de la configuration\n" +
                    "Chargement utilisateurs\n" +
                    "Chargement societe\n" +
                    "Chargement des pointages";
            String chargementConfigUserSocietePointagesLieux = "Chargement Logiciel\n" +
                    "Chargement de la configuration\n" +
                    "Chargement utilisateurs\n" +
                    "Chargement societe\n" +
                    "Chargement des pointages\n" +
                    "Chargement des Lieux";
            new Thread(importInformationAppli).start();
            do {
                switch ((int) finDuTravaildImport.getCount()) {
                    case 5:
                        runOnUiThread(() -> affichageText.setText(chargementConfig));
                        break;
                    case 4:
                        runOnUiThread(() -> affichageText.setText(chargementConfigUser));
                        break;
                    case 3:
                        runOnUiThread(() -> affichageText.setText(chargementConfigUserSociete));
                        break;
                    case 2:
                        runOnUiThread(() -> affichageText.setText(chargementConfigUserSocietePointages));
                        break;
                    case 1:
                        runOnUiThread(() -> affichageText.setText(chargementConfigUserSocietePointagesLieux));
                        break;
                }
            } while (finDuTravaildImport.getCount() != 0);

            runOnUiThread(() -> affichageText.setText("Fin du chargement de l'application"));

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intentChargement = new Intent(MonAppContext.context, EcranDeConnexion.class);
            startActivity(intentChargement);
        }
    });
}
