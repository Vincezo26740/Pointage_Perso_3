package com.example.pointageperso3.function;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pointageperso3.Entity.Lieu;
import com.example.pointageperso3.Entity.Societe;
import com.example.pointageperso3.Entity.User;
import com.example.pointageperso3.R;
import com.example.pointageperso3.Threads.AjoutSociete;
import com.example.pointageperso3.Threads.ImportBDDInfos;
import com.example.pointageperso3.Threads.MajSociete;
import com.example.pointageperso3.Threads.MajUser;
import com.example.pointageperso3.Threads.RecupLieu;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parametres extends AppCompatActivity {

    private View btnUser, btnSociete, btnLieux, layoutBtnParams, retour, layoutUser, layoutSociete, layoutLieu;
    private String nomSociete, adresseSociete, villeSociete, email1Societe, email2Societe, email3Societe;
    private String nomUser, adresseUser, villeUser, emailUser, CPUser, prenomUser;
    private String nomLieu, adresseLieu, villeLieu;
    private int codePostalSociete, codePostalLieu;
    private String[][] dataRecycler;
    private Lieu newLieu;
    private Societe newSociete;
    private RecupLieu recupLieux;
    private User userEntityBDD;
    private Societe societe;
    private final CountDownLatch latch = new CountDownLatch(1);
    boolean choixEnCoursParamtres = false;

    private Button validationUser, nouveauMdp;

    private boolean nameOk, email1OK, adresseOk, CPOk, villeOk;
    private boolean email2OK = true, email3OK = true;
    private boolean nameUserOk, prenomUserOk, emailUserOK, adresseUserOk, CPUserOk, villeUserOk;


    // Récupération Intent
    private String user, password, classInit, classEchec, classReussite, fonction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recupIntent();
        setContentView(R.layout.parametrage_application);
        btnUser = findViewById(R.id.paraUser);
        layoutUser = findViewById(R.id.LayoutParaUser);
        btnSociete = findViewById(R.id.paraSociete);
        layoutSociete = findViewById(R.id.LayoutParaSociete);
        btnLieux = findViewById(R.id.paraLieu);
        retour = findViewById(R.id.btn_Retour);
        layoutBtnParams = findViewById(R.id.Layoutparametres);
        activationDesBoutons();
        desactivationVues();
        new Thread(recupUserEtSociete).start();

    }

    private void recupIntent() {
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");
        classInit = getIntent().getStringExtra("class");
        classEchec = getIntent().getStringExtra("classEchec");
        classReussite = getIntent().getStringExtra("classReussite");
        fonction = getIntent().getStringExtra("fonction");
    }

    public void activationDesBoutons() {
        btnUser.setOnClickListener(this::parametresUser);
        btnSociete.setOnClickListener(this::parametresSociete);
        btnLieux.setOnClickListener(this::parametreLieu);
        retour.setOnClickListener(this::retour);
    }

    public void desactivationDesBoutons() {
        btnUser.setOnClickListener(null);
        btnSociete.setOnClickListener(null);
        btnLieux.setOnClickListener(null);
        retour.setOnClickListener(null);
    }

    private void desactivationVues() {
        layoutUser.setVisibility(View.GONE);
        layoutSociete.setVisibility(View.GONE);
    }

    private void activationVue(@NonNull View view) {
        view.setVisibility(View.VISIBLE);
        reductionZoneDeChoix();
    }

    private void retour(View view) {
        Intent retour = new Intent(this, PointageFunction.class);

        retour.putExtra("user", user);
        retour.putExtra("password", "");
        retour.putExtra("class", getClass().getName());
        retour.putExtra("classEchec", PointageFunction.class.getName());
        retour.putExtra("classReussite", PointageFunction.class.getName());
        retour.putExtra("fonction", "");

        startActivity(retour);
    }

    private void parametresUser(View view) {
        desactivationVues();
        activationVue(layoutUser);
        definitionVueUser();
    }

    private void changmentMDPUser(View view) {
        Intent intent = new Intent(this, MajMDP.class);

        intent.putExtra("user", user);
        intent.putExtra("password", "");
        intent.putExtra("class", getClass().getName());
        intent.putExtra("classEchec", PointageFunction.class.getName());
        intent.putExtra("classReussite", Parametres.class.getName());
        intent.putExtra("fonction", "");

        startActivity(intent);
    }

    private void parametresSociete(View view) {
        desactivationVues();
        activationVue(layoutSociete);
        definitionVueSociete();
    }

    Thread ajoutSociete = new Thread(() -> {
        if (societe == null) {
            AjoutSociete ajoutSociete = new AjoutSociete(newSociete);
            societe = newSociete;
            ajoutSociete.start();
        } else {
            MajSociete majSociete = new MajSociete(newSociete);
            societe = newSociete;
            majSociete.start();
        }
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(Parametres.this, "Création/Mise à jour de la société réalisée", Toast.LENGTH_LONG);

            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        });

    });

    Thread recupUserEtSociete = new Thread(() -> {
        CountDownLatch majAttente = new CountDownLatch(6);
        ImportBDDInfos majInfosAppli = new ImportBDDInfos(majAttente);
        new Thread(majInfosAppli).start();
        userEntityBDD = DonneesDeLApplication.getUtilisateur();
        societe = DonneesDeLApplication.getSociete();
//        confAppli = DonneesDeLApplication.getConfAppli();
//        listeDesUtilisateurs = DonneesDeLApplication.getListeDesUtilisateurs();
//        listeDePointages = DonneesDeLApplication.getListeDePointages();
//        listeDeLieux = DonneesDeLApplication.getListeDeLieux();
        try {
            majAttente.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        PersoDatabase accesBdd = AccesBDD.getConnexionBDD();
//        userEntityBDD = accesBdd.DaoUser().rechercheUser(user);
//        societe = accesBdd.DaoSociete().findSociete();
//        accesBdd.close();
//        try {
//            Thread.currentThread().join(1000);
//        } catch (Exception ignored) {
//        }
        runOnUiThread(() -> {
            definitionVueUser();
            definitionVueSociete();
            if (fonction.equals("ajout1User")) {
                desactivationVues();
                activationVue(layoutSociete);
                definitionVueSociete();
            }
        });
    });

    private void razZoneDeChoix() {
        if (choixEnCoursParamtres) {
            reductionLL = findViewById(R.id.scroll_view_btn_parametres);
            recupParamsLayout = reductionLL.getLayoutParams();
            int heigtLayout = reductionLL.getHeight();
            heigtLayout = heigtLayout * 3;
            recupParamsLayout.height = heigtLayout;
            reductionLL.setLayoutParams(recupParamsLayout);
            reductionLL.requestLayout();
        }
    }

    View reductionLL;
    ViewGroup.LayoutParams recupParamsLayout;

    private void reductionZoneDeChoix() {
        reductionLL = findViewById(R.id.scroll_view_btn_parametres);
        recupParamsLayout = reductionLL.getLayoutParams();
        if (choixEnCoursParamtres) {
            razZoneDeChoix();
            int heightLayout = recupParamsLayout.height;
            recupParamsLayout.height = heightLayout / 3;
        } else {
            int heightLayout = reductionLL.getHeight();
            recupParamsLayout.height = heightLayout / 3;
        }
        reductionLL.setLayoutParams(recupParamsLayout);
        choixEnCoursParamtres = true;
    }

    private void definitionVueUser() {
        EditText name, prenom, email, adresse, CP, town;

        validationUser = findViewById(R.id.btn_validation_maj_user);
        validationUser.setVisibility(View.GONE);
        name = findViewById(R.id.maj_nom);
        adresse = findViewById(R.id.maj_adresse);
        CP = findViewById(R.id.maj_CP);
        town = findViewById(R.id.maj_town);
        email = findViewById(R.id.maj_email);
        prenom = findViewById(R.id.maj_prenom);
        nouveauMdp = findViewById(R.id.changement_mdpuser_parametres);
        nouveauMdp.setOnClickListener(this::changmentMDPUser);
        if (userEntityBDD != null) {
            if (userEntityBDD.getName() != null) {
                name.setText(userEntityBDD.getName());
            }
            if (userEntityBDD.getAddress() != null) {
                adresse.setText(userEntityBDD.getAddress());
            }
            if (userEntityBDD.getCP() != 0) {
                String CPspecString = String.valueOf(userEntityBDD.getCP());
                CP.setText(CPspecString);
            }
            if (userEntityBDD.getTown() != null) {
                town.setText(userEntityBDD.getTown());
            }
            if (userEntityBDD.getEmail() != null) {
                email.setText(userEntityBDD.getEmail());

            }
            if (userEntityBDD.getFirstName() != null) {
                prenom.setText(userEntityBDD.getFirstName());
            }

            validationUser.setVisibility(View.VISIBLE);
            validationUser.setOnClickListener(this::miseAJourUtilisateur);
        }
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!name.getAutofillValue().getTextValue().toString().isEmpty()) {
                    nameUserOk = true;
                    controleValidationVisible();
                } else {
                    nameUserOk = false;
                    controleValidationVisible();
                }
            }
        });
        adresse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!adresse.getAutofillValue().getTextValue().toString().isEmpty()) {
                    adresseUserOk = true;
                    controleValidationVisible();
                } else {
                    adresseUserOk = false;
                    controleValidationVisible();
                }
            }
        });
        CP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((s.length() > 4)) {
                    CPUserOk = true;
                    CP.setTextColor(getColor(R.color.black));
                    controleValidationVisible();
                } else {
                    CPUserOk = false;
                    CP.setTextColor(getColor(R.color.red));
                    controleValidationVisible();
                }
            }
        });
        town.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!town.getAutofillValue().getTextValue().toString().isEmpty()) {
                    villeUserOk = true;
                    controleValidationVisible();
                } else {
                    villeUserOk = false;
                    controleValidationVisible();
                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern paternRegexMail = Pattern.compile("([a-zA-Z0-9_-]+)((\\.)([a-zA-Z0-9_-]*))*@([a-zA-Z0-9_-]+)(\\.)+([a-zA-Z0-9_]{2,4})");
                Matcher matcher;
                matcher = paternRegexMail.matcher(s);
                if (!s.toString().isEmpty()) {
                    if (matcher.matches()) {
                        email.setTextColor(getColor(R.color.black));
                        emailUserOK = true;
                        controleValidationVisible();
                    } else {
                        emailUserOK = false;
                        email.setTextColor(getColor(R.color.red));
                    }
                }
            }
        });
        prenom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!prenom.getAutofillValue().getTextValue().toString().isEmpty()) {
                    prenom.setTextColor(getColor(R.color.black));
                    prenomUserOk = true;
                    controleValidationVisible();

                } else {
                    prenomUserOk = false;
                    prenom.setTextColor(getColor(R.color.red));
                    controleValidationVisible();
                }
            }
        });
    }

    private void controleValidationVisible() {
        if (nameUserOk && prenomUserOk
                && emailUserOK && adresseUserOk
                && CPUserOk && villeUserOk) {
            validationUser.setVisibility(View.VISIBLE);
            validationUser.setOnClickListener(this::miseAJourUtilisateur);
        } else {
            validationUser.setVisibility(View.INVISIBLE);
            validationUser.setOnClickListener(null);
        }
    }

    private void miseAJourUtilisateur(View view) {
        nomUser = findViewById(R.id.maj_nom).getAutofillValue().getTextValue().toString();
        adresseUser = findViewById(R.id.maj_adresse).getAutofillValue().getTextValue().toString();
        CPUser = findViewById(R.id.maj_CP).getAutofillValue().getTextValue().toString();
        villeUser = findViewById(R.id.maj_town).getAutofillValue().getTextValue().toString();
        emailUser = findViewById(R.id.maj_email).getAutofillValue().getTextValue().toString();
        prenomUser = findViewById(R.id.maj_prenom).getAutofillValue().getTextValue().toString();

        userEntityBDD.setName(nomUser);
        userEntityBDD.setFirstName(prenomUser);
        userEntityBDD.setEmail(emailUser);
        userEntityBDD.setAddress(adresseUser);
        userEntityBDD.setCP(Integer.parseInt(CPUser));
        userEntityBDD.setTown(villeUser);

        MajUser majUser = new MajUser(userEntityBDD);
        new Thread(majUser).start();

        Toast toast = Toast.makeText(Parametres.this, "Création/Mise à jour de l'utilisateur", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();

    }

    private void validationSociete(View view) {

        TextView mail1 = findViewById(R.id.email1_societe);
        TextView mail2 = findViewById(R.id.email2_societe);
        TextView mail3 = findViewById(R.id.email3_societe);
        String textToast = "Mise à jour de votre société";
        email1Societe = mail1.getAutofillValue().getTextValue().toString();
        email2Societe = mail2.getAutofillValue().getTextValue().toString();
        email3Societe = mail3.getAutofillValue().getTextValue().toString();

        nomSociete = findViewById(R.id.nom_Societe).getAutofillValue().getTextValue().toString();
        adresseSociete = findViewById(R.id.adresse_societe).getAutofillValue().getTextValue().toString();
        codePostalSociete = Integer.parseInt(findViewById(R.id.CP_societe).getAutofillValue().getTextValue().toString());
        villeSociete = findViewById(R.id.town_societe).getAutofillValue().getTextValue().toString();
        newSociete = new Societe(codePostalSociete, nomSociete, adresseSociete, villeSociete, email1Societe);

        new Thread(ajoutSociete).start();

        Toast toast = Toast.makeText(this, textToast, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        if (fonction.equals("ajout1User")) {
            Intent renvoiePointageFonction = new Intent(this, PointageFunction.class);

            renvoiePointageFonction.putExtra("user", user);
            renvoiePointageFonction.putExtra("password", "");
            renvoiePointageFonction.putExtra("class", getClass().getName());
            renvoiePointageFonction.putExtra("classEchec", PointageFunction.class.getName());
            renvoiePointageFonction.putExtra("classReussite", PointageFunction.class.getName());
            renvoiePointageFonction.putExtra("fonction", "");

            startActivity(renvoiePointageFonction);
        }
    }

    private void definitionVueSociete() {

        findViewById(R.id.btn_validation_societe).setVisibility(View.GONE);
        EditText nameSociete = findViewById(R.id.nom_Societe);
        EditText adresseSociete = findViewById(R.id.adresse_societe);
        EditText CPSociete = findViewById(R.id.CP_societe);
        EditText townSociete = findViewById(R.id.town_societe);
        EditText mail1 = findViewById(R.id.email1_societe);
        EditText mail2 = findViewById(R.id.email2_societe);
        EditText mail3 = findViewById(R.id.email3_societe);
        if (societe != null) {
            if (societe.getName() != null) {
                nameSociete.setText(societe.getName());
            }
            if (societe.getAddress() != null) {
                adresseSociete.setText(societe.getAddress());
            }
            if (societe.getCP() != 0) {
                String CPspecString = "" + societe.getCP() + "";
                CPSociete.setText(CPspecString);
            }
            if (societe.getTown() != null) {
                townSociete.setText(societe.getTown());
            }
            if (societe.getEmail() != null) {
                mail1.setText(societe.getEmail());

            }
            if (societe.getEmail2() != null) {
                mail2.setText(societe.getEmail2());

            }
            if (societe.getEmail3() != null) {
                mail3.setText(societe.getEmail3());
            }
            findViewById(R.id.btn_validation_societe).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_validation_societe).setOnClickListener(this::validationSociete);
        }
        nameSociete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!nameSociete.getAutofillValue().getTextValue().toString().isEmpty()) {
                    nameOk = true;
                    controleModifText();
                } else {
                    nameOk = false;
                    controleModifText();
                }
            }
        });
        adresseSociete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!adresseSociete.getAutofillValue().getTextValue().toString().isEmpty()) {
                    adresseOk = true;
                    controleModifText();
                } else {
                    adresseOk = false;
                    controleModifText();
                }
            }
        });
        CPSociete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((s.length() > 4)) {
                    CPSociete.setTextColor(getColor(R.color.black));
                    CPOk = true;
                    controleModifText();
                } else {
                    CPSociete.setTextColor(getColor(R.color.red));
                    CPOk = false;
                    controleModifText();
                }
            }
        });
        townSociete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!townSociete.getAutofillValue().getTextValue().toString().isEmpty()) {
                    villeOk = true;
                    controleModifText();
                } else {
                    villeOk = false;
                    controleModifText();
                }
            }
        });
        mail1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern paternRegexMail = Pattern.compile("([a-zA-Z0-9_-]+)((\\.)([a-zA-Z0-9_-]*))*@([a-zA-Z0-9_-]+)(\\.)+([a-zA-Z0-9_]{2,4})");
                Matcher matcher;
                matcher = paternRegexMail.matcher(s);
                if (!s.toString().isEmpty()) {
                    if (matcher.matches()) {
                        mail1.setTextColor(getColor(R.color.black));
                        email1OK = true;
                        controleModifText();
                    } else {
                        email1OK = false;
                        mail1.setTextColor(getColor(R.color.red));
                    }
                }
            }
        });
        mail2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern paternRegexMail = Pattern.compile("([a-zA-Z0-9_-]+)((\\.)([a-zA-Z0-9_-]*))*@([a-zA-Z0-9_-]+)(\\.)+([a-zA-Z0-9_]{2,4})");
                Matcher matcher;
                matcher = paternRegexMail.matcher(s);
                if (matcher.matches()) {
                    mail2.setTextColor(getColor(R.color.black));
                    email2OK = true;
                    controleModifText();
                } else if (s.toString().isEmpty()) {
                    email2OK = true;
                    controleModifText();
                } else {
                    email2OK = false;
                    mail2.setTextColor(getColor(R.color.red));
                    controleModifText();
                }
            }
        });
        mail3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Pattern paternRegexMail = Pattern.compile("([a-zA-Z0-9_-]+)((\\.)([a-zA-Z0-9_-]*))*@([a-zA-Z0-9_-]+)(\\.)+([a-zA-Z0-9_]{2,4})");
                Matcher matcher;
                matcher = paternRegexMail.matcher(s);
                if (matcher.matches()) {
                    mail3.setTextColor(getColor(R.color.black));
                    email3OK = true;
                    controleModifText();
                } else if (s.toString().isEmpty()) {
                    email3OK = true;
                    controleModifText();
                } else {
                    email3OK = false;
                    mail3.setTextColor(getColor(R.color.red));
                    controleModifText();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern paternRegexMail = Pattern.compile("([a-zA-Z0-9_-]+)((\\.)([a-zA-Z0-9_-]*))*@([a-zA-Z0-9_-]+)(\\.)+([a-zA-Z0-9_]{2,4})");
                Matcher matcher;
                matcher = paternRegexMail.matcher(s);
                if (matcher.matches()) {
                    mail3.setTextColor(getColor(R.color.black));
                    email3OK = true;
                    controleModifText();
                } else if (s.toString().isEmpty()) {
                    email3OK = true;
                    controleModifText();
                } else {
                    email3OK = false;
                    mail3.setTextColor(getColor(R.color.red));
                    controleModifText();
                }
            }
        });
    }

    private void controleModifText() {
        if (nameOk && email1OK && email2OK && email3OK && adresseOk && CPOk & villeOk) {
            findViewById(R.id.btn_validation_societe).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_validation_societe).setOnClickListener(this::validationSociete);
        } else {
            findViewById(R.id.btn_validation_societe).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_validation_societe).setOnClickListener(null);
        }
    }

    private void parametreLieu(View view) {
        Intent intentLieu = new Intent(this, AjoutDeLieu.class);

        intentLieu.putExtra("user", user);
        intentLieu.putExtra("password", "");
        intentLieu.putExtra("class", getClass().getName());
        intentLieu.putExtra("classEchec", PointageFunction.class.getName());
        intentLieu.putExtra("classReussite", PointageFunction.class.getName());
        intentLieu.putExtra("fonction", "");

        startActivity(intentLieu);
    }
}
