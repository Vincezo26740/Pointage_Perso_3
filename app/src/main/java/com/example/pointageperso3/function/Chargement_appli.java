package com.example.pointageperso3.function;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pointageperso3.R;
import com.example.pointageperso3.Threads.ImportBDDInfos;

import java.util.concurrent.CountDownLatch;

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
        new Thread(test).start();
    }

    Thread test = new Thread(new Runnable() {
        @Override
        public void run() {
            ImportBDDInfos importInformationAppli = new ImportBDDInfos(finDuTravaildImport);
            TextView affichageText = findViewById(R.id.chargement_app_text);
            String chargement = getString(R.string.chargement);
            String chargementConfig = chargement + "\n" + getString(R.string.chgtConf);
            String chargementConfigUser = chargementConfig + "\n" + getString(R.string.chgtUser);
            String chargementConfigUserSociete = chargementConfigUser + "\n" + getString(R.string.chgtSoc);
            String chargementConfigUserSocietePointages = chargementConfigUserSociete + "\n" + getString(R.string.chgtPtge);
            String chargementConfigUserSocietePointagesLieux = chargementConfigUserSocietePointages + "\n" + getString(R.string.chgtLieux);

            new Thread(importInformationAppli).start();
            runOnUiThread(() -> affichageText.setText(chargement));
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

            runOnUiThread(() -> {
                String fin = chargementConfigUserSocietePointages+"\n" + getString(R.string.chgtFin);
                affichageText.setText(fin);
            });

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
