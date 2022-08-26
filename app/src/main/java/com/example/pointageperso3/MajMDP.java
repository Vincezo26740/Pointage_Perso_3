package com.example.pointageperso3;

import static android.text.Html.escapeHtml;

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

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.ConfigAppli;
import Entity.User;

import java.util.concurrent.CountDownLatch;

import at.favre.lib.crypto.bcrypt.BCrypt;
import Threads.RecupUserBDD;

public class MajMDP extends AppCompatActivity {

    private User userRecup;
    private final CountDownLatch testControleUserLatch = new CountDownLatch(1);
    private final CountDownLatch chgtMdpLatch = new CountDownLatch(1);
    private final CountDownLatch recupUser = new CountDownLatch(1);

    // Récupération Intent
    private String user, password, classInit, classEchec, classReussite, fonction;

    // Getion de la page
    private TextView userTV, longMotTV, carSpecTV, majusculeTV, chiffreTV;
    private EditText userET, passWord1ET, passWord2ET;
    private String passWord1, passWord2;
    private boolean passWord1OK;
    private Button validationChgtPassWord, validationUser;
    private String textLorsDeLaCreationDeLaPage = "";

    // Constantes de REGEX Mot de passe
    private final String REGEX_LONGUEUR_MDP = "(.{8,})";
    private final String REGEX_MAJUSCULES_MDP = "((.*)([A-Z])(.*)){2,}";
    private final String REGEX_NOMBRES_MDP = "((.*)([0-9])(.*)){2,}";
    private final String REGEX_CAR_SPEC_MDP = "((.*)([^\\w])(.*)){2,}";
    private final String REGEX_PAS_ESPACE_MDP = "[^\\p{Space}]+";
    // boolean REGEX
    private boolean testValidationLongueur, testValidationMajuscule,
            testValidationChiffres, testValidationCarSpec,
            testValidationEspace;
    // boolean egalité mdp
    private boolean passWord1EgalspassWord2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recupIntent();
        textLorsDeLaCreationDeLaPage = getString(R.string.utilisateur);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.chgt_mdp);
        userTV = findViewById(R.id.tv_oublieMdpAffichage);
        longMotTV = findViewById(R.id.motLong);
        carSpecTV = findViewById(R.id.carSpec);
        majusculeTV = findViewById(R.id.majuscules);
        chiffreTV = findViewById(R.id.chiffres);
        validationChgtPassWord = findViewById(R.id.btn_validation_chgtMdp);
        userTV.setText(textLorsDeLaCreationDeLaPage);
        userET = findViewById(R.id.et_username_oublie);
        passWord1ET = findViewById(R.id.et_mdp1_oublie);
        passWord2ET = findViewById(R.id.et_mdp2_oublie);

        if (!fonction.equals("ajout1User")) {
            //activation de la vue saisie user
            vueSaisieUsernameActivation();
            vueSaisieMdpDesactivation();

            findViewById(R.id.btn_validation_username).setOnClickListener(v -> {
                user = escapeHtml(userET.getAutofillValue().getTextValue().toString());

                RecupUserBDD recupUserBDD = new RecupUserBDD(user, recupUser);
                new Thread(recupUserBDD).start();
                try {
                    recupUser.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userRecup = recupUserBDD.retourUser();

                if (!userRecup.getName().equals("null")) {
                    String concatenation = "nom Utilisateur: " + user;
                    userTV.setText(concatenation);
                    controleValiditeMDP();
                } else {
                    textLorsDeLaCreationDeLaPage = "Utilisateur non reconnu";
                    userET.setText("");
                    onResume();
                }
            });
        } else {

            RecupUserBDD recupUserBDD = new RecupUserBDD(user, recupUser);
            new Thread(recupUserBDD).start();
            try {
                recupUser.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            userRecup = recupUserBDD.retourUser();

            controleValiditeMDP();
        }
    }

    private void controleValiditeMDP() {
        //changment des vues
        vueSaisieUsernameDesactivation();
        vueSaisieMdpActivation();
        validationChgtPassWord.setVisibility(View.INVISIBLE);
        passWord1ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    if (s.toString().matches(REGEX_PAS_ESPACE_MDP)) {
                        userET.setTextColor(getColor(R.color.black));
                        testValidationEspace = true;
                    } else {
                        userET.setTextColor(getColor(R.color.red));
                        testValidationEspace = false;
                    }
                    if (s.toString().matches(REGEX_LONGUEUR_MDP)) {
                        longMotTV.setTextColor(getColor(R.color.green));
                        testValidationLongueur = true;
                    } else {
                        longMotTV.setTextColor(getColor(R.color.black));
                        testValidationLongueur = false;
                    }
                    if (s.toString().matches(REGEX_MAJUSCULES_MDP)) {
                        majusculeTV.setTextColor(getColor(R.color.green));
                        testValidationMajuscule = true;
                    } else {
                        majusculeTV.setTextColor(getColor(R.color.black));
                        testValidationMajuscule = false;
                    }
                    if (s.toString().matches(REGEX_NOMBRES_MDP)) {
                        chiffreTV.setTextColor(getColor(R.color.green));
                        testValidationChiffres = true;
                    } else {
                        chiffreTV.setTextColor(getColor(R.color.black));
                        testValidationChiffres = false;
                    }
                    if (s.toString().matches(REGEX_CAR_SPEC_MDP)) {
                        carSpecTV.setTextColor(getColor(R.color.green));
                        testValidationCarSpec = true;
                    } else {
                        carSpecTV.setTextColor(getColor(R.color.black));
                        testValidationCarSpec = false;
                    }
                }
                if (testValidationLongueur && testValidationMajuscule &&
                        testValidationChiffres && testValidationCarSpec &&
                        testValidationEspace) {
                    passWord1 = escapeHtml(passWord1ET.getAutofillValue().getTextValue().toString());
                    passWord1OK = true;
                } else {
                    passWord1OK = false;
                }
            }
        });
        passWord2ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passWord2 = escapeHtml(passWord2ET.getAutofillValue().getTextValue().toString());
                if (passWord1.equals(passWord2)) {
                    activationBoutonValider();
                    passWord1EgalspassWord2 = true;
                } else {
                    passWord1EgalspassWord2 = false;
                }
            }
        });
    }

    private void activationBoutonValider() {
        validationChgtPassWord.setVisibility(View.VISIBLE);
        validationChgtPassWord.setOnClickListener(this::changementDeMotDePasse);
    }

    private void recupIntent() {
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");
        classInit = getIntent().getStringExtra("class");
        classEchec = getIntent().getStringExtra("classEchec");
        classReussite = getIntent().getStringExtra("classReussite");
        fonction = getIntent().getStringExtra("fonction");
    }

    private void vueSaisieUsernameActivation() {
        findViewById(R.id.chgt_mdp_username).setVisibility(View.VISIBLE);
    }

    private void vueSaisieUsernameDesactivation() {
        findViewById(R.id.chgt_mdp_username).setVisibility(View.GONE);
    }

    private void vueSaisieMdpActivation() {
        findViewById(R.id.RL_saisie_MDP).setVisibility(View.VISIBLE);

    }

    private void vueSaisieMdpDesactivation() {
        findViewById(R.id.RL_saisie_MDP).setVisibility(View.GONE);
    }

    private void changementDeMotDePasse(View view) {

        if (passWord1EgalspassWord2) {
            new Thread(ChgtMotDePasseBddSiValide).start();
            try {
                chgtMdpLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!fonction.equals("ajout1User")) {
                retourClasse();
            } else {
                new Thread(creationInfoConfigPremiereFois).start();
                Intent renvoieVersLaPageDeParametreDeLaSociete = new Intent(this, Parametres.class);

                renvoieVersLaPageDeParametreDeLaSociete.putExtra("user", user);
                renvoieVersLaPageDeParametreDeLaSociete.putExtra("password", "");
                renvoieVersLaPageDeParametreDeLaSociete.putExtra("class", getClass().getName());
                renvoieVersLaPageDeParametreDeLaSociete.putExtra("classEchec", EcranDeConnexion.class.getName());
                renvoieVersLaPageDeParametreDeLaSociete.putExtra("classReussite", PointageFunction.class.getName());
                renvoieVersLaPageDeParametreDeLaSociete.putExtra("fonction", fonction);

                startActivity(renvoieVersLaPageDeParametreDeLaSociete);
            }
        } else {
            String ancienText = userTV.getText().toString() + "\n Erreur dans le changement \n des mots de passe";
            userTV.setText(ancienText);
            passWord1ET.setText("");
            passWord2ET.setText("");
        }
    }


    private void retourClasse() {
        Intent retourClass;
        if (classInit.equals("Parametres")) {
            retourClass = new Intent(this, Parametres.class);
        } else {
            retourClass = new Intent(this, PointageFunction.class);
        }

        retourClass.putExtra("user", user);
        retourClass.putExtra("password", "");
        retourClass.putExtra("class", getClass().getName());
        retourClass.putExtra("classEchec", EcranDeConnexion.class.getName());
        retourClass.putExtra("classReussite", PointageFunction.class.getName());
        retourClass.putExtra("fonction", "");

        startActivity(retourClass);
    }

    Thread creationInfoConfigPremiereFois = new Thread(() -> {
        PersoDatabase accesBddConfig1Fois = AccesBDD.getConnexionBDD();
        ConfigAppli applicationParametrage = new ConfigAppli();
        accesBddConfig1Fois.DaoConfigAppli().insertConfig(applicationParametrage);
        accesBddConfig1Fois.close();
    });

    Thread ChgtMotDePasseBddSiValide = new Thread(() -> {
        userRecup.setPW(BCrypt.withDefaults().hashToString(12, passWord1.toCharArray()));
        PersoDatabase majMDP = AccesBDD.getConnexionBDD();
        majMDP.DaoUser().updateUser(userRecup);
        chgtMdpLatch.countDown();
    });
}
