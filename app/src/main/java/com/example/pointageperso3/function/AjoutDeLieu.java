package com.example.pointageperso3.function;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointageperso3.DAO.AccesBDD;
import com.example.pointageperso3.DAO.PersoDatabase;
import com.example.pointageperso3.Entity.Lieu;
import com.example.pointageperso3.R;
import com.example.pointageperso3.Threads.AjoutLieu;
import com.example.pointageperso3.Threads.ImportBDDInfos;
import com.example.pointageperso3.Threads.RecupLieu;
import com.example.pointageperso3.adapterViewRecycler.RecyclerViewLieu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AjoutDeLieu extends AppCompatActivity {


    private String nomLieu, adresseLieu, villeLieu;
    private int codePostalLieu;
    private String[][] dataRecycler;
    private Lieu newLieu;
    private Button validationLieu;
    private RecupLieu recupLieux;
    private View retour;
    private final BlockingDeque<Runnable> blockQueue = new LinkedBlockingDeque<>();
    private ThreadPoolExecutor ThreadExecutorMajRecyclerView;
    private CountDownLatch countDownAjoutLieu, okLieuMajRV = new CountDownLatch(1);
    private boolean nomLieuBoolean, adresseLieuBoolean, CPLieuBoolean, villeLieuBoolean;

    // Récupération Intent
    private String user, password, classInit, classEchec, classReussite, fonction;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okLieuMajRV = new CountDownLatch(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recupIntent();
        setContentView(R.layout.ajout_de_lieu);
        retour = findViewById(R.id.retour);
        retour.setOnClickListener(this::retour);
        validationLieu = findViewById(R.id.btn_validation_ajout_Lieu);
        validationLieu.setVisibility(View.INVISIBLE);

        parametreLieu();
    }

    private void recupIntent() {
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");
        classInit = getIntent().getStringExtra("class");
        classEchec = getIntent().getStringExtra("classEchec");
        classReussite = getIntent().getStringExtra("classReussite");
        fonction = getIntent().getStringExtra("fonction");
    }

    private void retour(View view) {
        try {
            Intent retourClasse = new Intent(this, Class.forName(classEchec));

            retourClasse.putExtra("user", user);
            retourClasse.putExtra("password", "");
            retourClasse.putExtra("class", getClass().getName());
            retourClasse.putExtra("classEchec", PointageFunction.class.getName());
            retourClasse.putExtra("classReussite", Parametres.class.getName());
            retourClasse.putExtra("fonction", "");

            startActivity(retourClasse);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parametreLieu() {
        definitionVueLieu();
        okLieuMajRV = new CountDownLatch(1);
        new Thread(recycleurVueMaj).start();
        try {
            okLieuMajRV.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recycleurVueLieu();
    }

    private void validationLieu(View view) {
        new Thread(recycleurVueMaj).start();

        newLieu = new Lieu(codePostalLieu, nomLieu, adresseLieu, villeLieu);
        blockQueue.clear();
        blockQueue.add(ajoutLieu);
        ThreadExecutorMajRecyclerView.prestartAllCoreThreads();

    }

    Thread ajoutLieu = new Thread(() -> {
        AjoutLieu ajoutLieuClass = new AjoutLieu(newLieu);
        ajoutLieuClass.start();
        runOnUiThread(this::onResume);
    });

    Thread recycleurVueMaj = new Thread(() -> {
        CountDownLatch majAttente = new CountDownLatch(6);
        ImportBDDInfos majInfosAppli = new ImportBDDInfos(majAttente);
        new Thread(majInfosAppli).start();

        try {
            majAttente.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        ArrayList<Lieu> tableauLieuPointage = DonneesDeLApplication.getListeDeLieux();
        countDownAjoutLieu = new CountDownLatch(1);
        recupLieux = new RecupLieu(countDownAjoutLieu);
        ArrayList<Lieu> tableauLieuPointage;
        blockQueue.add(recupLieux);
        ThreadExecutorMajRecyclerView = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MILLISECONDS, blockQueue);
        ThreadExecutorMajRecyclerView.prestartAllCoreThreads();
        try {
            countDownAjoutLieu.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tableauLieuPointage = recupLieux.recupLieux();

        int i = 0;
        List<Integer> AEnlever = new ArrayList<>();
        dataRecycler = new String[tableauLieuPointage.size()][2];
        for (Lieu lieuPointage : tableauLieuPointage) {
            if ((lieuPointage.getName() == null) &&
                    (lieuPointage.getTown() == null) &&
                    (lieuPointage.getAddress() == null) &&
                    (lieuPointage.getCP() == 0)) {
                AEnlever.add(i);
            }
            i++;
        }
        PersoDatabase vidangeTableauPointage = AccesBDD.getConnexionBDD();
        i = 0;
        if (AEnlever.size() == tableauLieuPointage.size()) {
            tableauLieuPointage.clear();
            vidangeTableauPointage.DaoLieu().truncateSansRAZId();
        } else if (AEnlever.size() > 0) {
            for (Integer indiceLieuAEnlever : AEnlever) {
                tableauLieuPointage.remove(i);
                vidangeTableauPointage.DaoLieu().deletePointageEntity(tableauLieuPointage.get(indiceLieuAEnlever));
                i++;
            }
        }
        if (tableauLieuPointage.size() > 0) {
            dataRecycler = new String[tableauLieuPointage.size()][2];
            for (Lieu lieuPointage : tableauLieuPointage) {
                dataRecycler[i][0] = lieuPointage.getName();
                dataRecycler[i][1] = lieuPointage.getTown();
                i++;
            }
        } else {
            dataRecycler = new String[1][2];
            dataRecycler[0][0] = "Pas de Lieu";
            dataRecycler[0][1] = "";
        }

        if (okLieuMajRV.getCount() == 0) {
            okLieuMajRV = new CountDownLatch(1);
        }
        okLieuMajRV.countDown();
    });

    private void definitionVueLieu() {

        EditText nameLieu = findViewById(R.id.nom_Lieu);
        EditText adresseLieuET = findViewById(R.id.adresse_Lieu);
        EditText CPLieu = findViewById(R.id.CP_Lieu);
        EditText villeLieuET = findViewById(R.id.town_Lieu);

        nameLieu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    nomLieuBoolean = true;
                    nomLieu =((TextView) nameLieu).getText().toString();
                    controleLieuValidation();
                } else {
                    nomLieuBoolean = false;
                    controleLieuValidation();
                }
            }
        });
        adresseLieuET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    adresseLieuBoolean = true;
                    adresseLieu = adresseLieuET.getText().toString();
                    controleLieuValidation();
                } else {
                    adresseLieuBoolean = false;
                    controleLieuValidation();
                }
            }
        });
        CPLieu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 4) {
                    CPLieu.setTextColor(getColor(R.color.black));
                    CPLieuBoolean = true;
                    codePostalLieu = Integer.parseInt(CPLieu.getText().toString());
                    controleLieuValidation();
                } else {
                    CPLieu.setTextColor(getColor(R.color.red));
                    CPLieuBoolean = false;
                    controleLieuValidation();
                }
            }
        });
        villeLieuET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    villeLieuBoolean = true;
                    villeLieu = villeLieuET.getText().toString();
                    controleLieuValidation();
                } else {
                    villeLieuBoolean = false;
                    controleLieuValidation();
                }
            }
        });
    }

    private void controleLieuValidation() {
        if (nomLieuBoolean && adresseLieuBoolean && CPLieuBoolean && villeLieuBoolean) {
            validationLieu.setVisibility(View.VISIBLE);
            validationLieu.setOnClickListener(this::validationLieu);
        } else {
            validationLieu.setVisibility(View.INVISIBLE);
            validationLieu.setOnClickListener(null);
        }
    }

    private void recycleurVueLieu() {
        RecyclerView rv = findViewById(R.id.RecyclerViewLieu);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        RecyclerViewLieu RCVP = new RecyclerViewLieu(dataRecycler);
        rv.setAdapter(RCVP);
        rv.setLayoutManager(llm);
    }
}
