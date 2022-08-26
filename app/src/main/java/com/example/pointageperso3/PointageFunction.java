package com.example.pointageperso3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.ConfigAppli;
import Entity.Lieu;
import Entity.Pointage;
import Entity.Societe;
import Entity.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import adapterViewRecycler.RecyclerView_1_ligne_2_String;
import Threads.EnvoieMail;
import Threads.InfoConfigAppli;
import Threads.RecupUserBDD;

@SuppressWarnings("rawtypes")
public class PointageFunction extends AppCompatActivity implements RecyclerView_1_ligne_2_String.ListenerDeSelection {


    // Récupération Intent
    private String user, password, classInit, classEchec, classReussite, fonction;

    // Constante acces permissions application
    private final String AUTORISATIONGPSCOARSE = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String AUTORISATIONGPSFIN = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String AUTORISATIONSMS = Manifest.permission.SEND_SMS;
    private final String AUTORISATIONTELEPHONE = Manifest.permission.CALL_PHONE;
    private final String AUTORISATIONECRITURE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String AUTORISATIONLECTURE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String AUTORISATIONMANAGE = Manifest.permission.MANAGE_EXTERNAL_STORAGE;
    private final String AUTORISATIONLECTURETELEPHONENUMERO = Manifest.permission.READ_PHONE_NUMBERS;
    private String[] listeAutorisation;
    private final int REQUESTCODEVALUE = 100;

    private CountDownLatch retourUser, retourConfig,retourDernierPointage;

    private TextView title, tvDernierPointage;
    private Button absence, debutPointageButton, finPointageButton,
            parametres, creationFichier, validationPointage,
            retour, checkPointage = null, nouveauLieu;

    private String bienvenueUser, commentaires = "", typeAcces;
    private String[][] dataRecycler;
    private StringBuilder pointagePrecedent;

    private int etatPointage, idPointage, lieuDePointage;

    private User utilisateur;
    private Pointage pointageActuel, pointageAInsererAvantPointageActuel, dernierEnregistrement;
    private Date date;
    private ConfigAppli configAppli;


    private ArrayList<Pointage> listPointage;
    private final SimpleDateFormat formatagedate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);

    private LocationManager locationManager;
    double latitude, longitude;

    private boolean fonctionUsed;

    private Pointage dernierPointage ;

    //****************//


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();

    }

    private void recupIntent() {
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");
        classInit = getIntent().getStringExtra("class");
        classEchec = getIntent().getStringExtra("classEchec");
        classReussite = getIntent().getStringExtra("classReussite");
        fonction = getIntent().getStringExtra("fonction");
    }

    private void requestPermissions() {
        ArrayList<String> listPermission = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONLECTURE) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONLECTURE);
        }
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONECRITURE) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONECRITURE);
        }
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONGPSCOARSE) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONGPSCOARSE);
        }
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONGPSFIN) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONGPSFIN);
        }
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONTELEPHONE) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONTELEPHONE);
        }
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONLECTURETELEPHONENUMERO) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONLECTURETELEPHONENUMERO);
        }
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONSMS) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONSMS);
        }
        if (Build.VERSION.SDK_INT >= 30) {
            if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONMANAGE) != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(AUTORISATIONMANAGE);
            }
        }
        listeAutorisation = new String[listPermission.size()];
        for (int i = 0; i < listeAutorisation.length; i++) {
            listeAutorisation[i] = listPermission.get(i);
        }
        if (listeAutorisation.length != 0) {
            ActivityCompat.requestPermissions(PointageFunction.this, listeAutorisation, REQUESTCODEVALUE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        recupIntent();
        super.onResume();

        // récupération utilisateur infos
        retourUser = new CountDownLatch(1);

        RecupUserBDD bddUser = new RecupUserBDD(user, retourUser);
        new Thread(bddUser).start();
        try {
            retourUser.await();
        } catch (InterruptedException ignored) {
        }

        utilisateur = bddUser.retourUser();
        // récupération configuration infos

        retourConfig = new CountDownLatch(1);

        InfoConfigAppli infoConfigAppli = new InfoConfigAppli(retourConfig);
        new Thread(infoConfigAppli).start();
        try {
            retourConfig.await();
        } catch (InterruptedException ignored) {
        }



        configAppli = infoConfigAppli.getInfoConfigAppli();
        bienvenueUser = "Pointage de \n" + utilisateur.getFirstName();
        setContentView(R.layout.accueil);
        title = findViewById(R.id.tv_title_pointage);
        title.setText(bienvenueUser);
        activationBouton();

        if (fonction.equals("envoieEmail")) {
            EnvoiMailViaMessagerie();
        }
    }

    private void activationBouton() {
        retour = findViewById(R.id.btn_Retour);
        retour.setOnClickListener(this::pagePrecedente);

        absence = findViewById(R.id.btn_pointage_absence);
        absence.setOnClickListener(this::declarationAbsence);

        checkPointage = findViewById(R.id.btn_CtrlPointage);
        checkPointage.setOnClickListener(this::lecturePointages);

        debutPointageButton = findViewById(R.id.btn_pointage_debut);
        debutPointageButton.setOnClickListener(this::debutPointage);

        finPointageButton = findViewById(R.id.btn_pointage_fin);
        finPointageButton.setOnClickListener(this::finPointage);

        parametres = findViewById(R.id.btn_Parametres);
        parametres.setOnClickListener(this::parametres);

        creationFichier = findViewById(R.id.btn_CtrlPointage2);
        creationFichier.setOnClickListener(this::envoieEmail);
    }

    private void envoieEmail(View view) {

        Intent demandeAttenteEtControle = new Intent(this, Attente_affichage.class);

        demandeAttenteEtControle.putExtra("user", utilisateur.getName());
        demandeAttenteEtControle.putExtra("password", "");
        demandeAttenteEtControle.putExtra("class", getClass().getName());
        demandeAttenteEtControle.putExtra("classEchec", getClass().getName());
        demandeAttenteEtControle.putExtra("classReussite", getClass().getName());
        demandeAttenteEtControle.putExtra("fonction", "envoieEmail");

        startActivity(demandeAttenteEtControle);
    }

    Societe testSocRecup;
    CountDownLatch recuprsociete = new CountDownLatch(1);
    private void EnvoiMailViaMessagerie() {
        CountDownLatch CD = new CountDownLatch(1);

        // reset de l'intent fonction pour éviter une boucle infinie
        getIntent().putExtra("fonction", "");

        File path = EnvoieMail.getPathFichier();
        String messageText = getString(R.string.corps_fichier_csv) + user;
        new Thread(recupSociete).start();
        try {
            recuprsociete.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // requete pour envoie mail
        Intent sendMail = new Intent(Intent.ACTION_SEND);
        sendMail.setType("message/rfc822"); // setting file format
       sendMail.putExtra(Intent.EXTRA_EMAIL, testSocRecup.getEmail());
        sendMail.putExtra(Intent.EXTRA_SUBJECT, messageText);// setting corps de page
        //création du provider pour échange avec les autres programmes
        sendMail.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", path));
        sendMail.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //choix du choix du programme à utilisé
        startActivity(Intent.createChooser(sendMail, getString(R.string.titre_chooser_application_mail)));

    }

    Thread recupSociete = new Thread(new Runnable() {
        @Override
        public void run() {
            PersoDatabase recupsoc = AccesBDD.getConnexionBDD();
            testSocRecup = recupsoc.DaoSociete().findSociete();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recupsoc.close();
            recuprsociete.countDown();
        }
    });

    private void parametres(View view) {
        Intent goParametre = new Intent(this, Parametres.class);

        goParametre.putExtra("user", user);
        goParametre.putExtra("password", "");
        goParametre.putExtra("class", getClass().getName());
        goParametre.putExtra("classEchec", "");
        goParametre.putExtra("classReussite", "");
        goParametre.putExtra("fonction", "");

        startActivity(goParametre);
    }

    private void desactivationBouton() {
        retour = findViewById(R.id.btn_Retour);
        retour.setOnClickListener(null);
        if (findViewById(R.id.btn_pointage_absence) != null) {
            Button absence = findViewById(R.id.btn_pointage_absence);
            absence.setOnClickListener(null);
        }

        checkPointage = findViewById(R.id.btn_CtrlPointage);
        checkPointage.setOnClickListener(null);
        if (findViewById(R.id.btn_pointage_debut) != null) {
            Button debutPointageButton = findViewById(R.id.btn_pointage_debut);
            debutPointageButton.setOnClickListener(null);

            Button finPointageButton = findViewById(R.id.btn_pointage_fin);
            finPointageButton.setOnClickListener(null);

            Button parametres = findViewById(R.id.btn_Parametres);
            parametres.setOnClickListener(null);
        }
    }

    private void setDateTitleAndCheckButtonListener() {
        date = new Date();
        String dateFormater = formatagedate.format(date);
        TextView datePage = findViewById(R.id.tv_PointageDate);
        datePage.setText(dateFormater);
        retour = findViewById(R.id.btn_Retour);
        retour.setOnClickListener(this::retour);
    }

    private void declarationAbsence(View view) {
        setContentView(R.layout.abscence_pointage);
        setDateTitleAndCheckButtonListener();
    }

    public void valeurAbsence(View view) {
        // Is the button now checked?
        boolean checked = false;
        // Check which radio button was clicked

        etatPointage = 3;
        TextView titre;
        EditText holder;
        retour = findViewById(R.id.btn_Retour);
        retour.setOnClickListener(this::retour);
        validationPointage = findViewById(R.id.btn_validationAbsence);
        RadioButton maladie = findViewById(R.id.CB_absence_maladie);
        RadioButton personnelle = findViewById(R.id.CB_absence_personnelle);
        RadioButton autre = findViewById(R.id.CB_absence_autre);
        RadioButton conges = findViewById(R.id.CB_absence_demande_congees);
        do {
            if (maladie.isChecked()) {
                commentaires = getString(R.string.absence_raison_maladie);
                validationPointage.setOnClickListener(this::absence);
                checked = true;
            }
            if (personnelle.isChecked()) {
                commentaires = getString(R.string.absence_raison_personnelle);
                validationPointage.setOnClickListener(this::absence);
                checked = true;
            }
            if (autre.isChecked()) {
                commentaires = getString(R.string.absence_raison_autre);
                setContentView(R.layout.abscence_autre_detatil);
                retour = findViewById(R.id.btn_Retour);
                retour.setOnClickListener(this::declarationAbsence);
                titre = findViewById(R.id.TV_AbsenceAutre_Explications);
                String textTitre = getString(R.string.absence) + " " + getString(R.string.absence_raison_autre);
                titre.setText(textTitre);
                holder = findViewById(R.id.ET_abcenceAutre);
                holder.setHint(getString(R.string.detaillez_les_raisons_de_votre_absence));
                validationPointage = findViewById(R.id.btn_validationAbsence_detail);
                validationPointage.setOnClickListener(this::validationAbsenceDetail);
                checked = true;
            }
            if (conges.isChecked()) {
                commentaires = getString(R.string.demande_de_congees_texte);
                setContentView(R.layout.abscence_autre_detatil);
                retour = findViewById(R.id.btn_Retour);
                retour.setOnClickListener(this::declarationAbsence);
                titre = findViewById(R.id.TV_AbsenceAutre_Explications);
                titre.setText(getString(R.string.demande_de_congees_texte));
                holder = findViewById(R.id.ET_abcenceAutre);
                holder.setHint(getString(R.string.dates_demandes_congees));
                validationPointage = findViewById(R.id.btn_validationAbsence_detail);
                validationPointage.setOnClickListener(this::validationAbsenceDetail);
                checked = true;
            }
        } while (!checked);
    }

    private void absence(View view) {
        creationPointage();
    }

    private void validationAbsenceDetail(View view) {
        commentaires = commentaires + findViewById(R.id.ET_abcenceAutre).getAutofillValue().getTextValue();
        creationPointage();
    }

    Thread dernierPointageDebutEtFinAffichage = new Thread(() -> {
        StringBuilder concatenationDernierPointage = new StringBuilder();
        PersoDatabase accesBdd = AccesBDD.getConnexionBDD();
        ArrayList<Pointage> dernierEnregistrementBDDList = (ArrayList<Pointage>) accesBdd.DaoPointage().findAllLimit(1);
        accesBdd.close();
        if (dernierEnregistrementBDDList.size() > 0) {
            Pointage dernierEnregistrementBDD = dernierEnregistrementBDDList.get(0);
            String lieuDernierPointage = accesBdd.DaoLieu().findNameLieu(dernierEnregistrementBDD.getReferenceLieuDePointage());
            accesBdd.close();
            concatenationDernierPointage.append(getString(R.string.dernier_mointage_fait));
            concatenationDernierPointage.append(dernierEnregistrementBDD.getNomUtilisateur()).append("\n");
            concatenationDernierPointage.append(getString(R.string.pointe_le)).append(formatagedate.format(dernierEnregistrementBDD.getDateDebut())).append("\n");
            switch (dernierEnregistrementBDD.getEtatPointage()) {
                case 1:
                    concatenationDernierPointage.append(getString(R.string.pointage_debut)).append("\n");
                    break;
                case 2:
                    concatenationDernierPointage.append(getString(R.string.fin_depointage)).append("\n");
                    break;
                case 3:
                    concatenationDernierPointage.append(getString(R.string.maladie_ou_congee)).append("\n");
                    break;
                default:
                    concatenationDernierPointage.append(getString(R.string.a_controler)).append("\n");
                    break;
            }
            if (dernierEnregistrementBDD.getEtatPointage() != 3) {
                concatenationDernierPointage.append(getString(R.string.lieux_de_pointage));
                switch (dernierEnregistrementBDD.getLieuDePointage()) {
                    case 0:
                        concatenationDernierPointage.append(getString(R.string.entreprise));
                        break;
                    case 1:
                        concatenationDernierPointage.append(lieuDernierPointage);
                        break;
                    case 2:
//                            pointagePrecedent.append("Lieu : ").append(testNomLieu);
                        break;
                    case 3:
                        concatenationDernierPointage.append(getString(R.string.en_pause));
                        break;
                    case 4:
                        concatenationDernierPointage.append(getString(R.string.fin_de_journee));
                        break;
                    case 5:
                        concatenationDernierPointage.append(getString(R.string.autres_raisons));
                        concatenationDernierPointage.append("\n");
                        concatenationDernierPointage.append(getString(R.string.commentaires));
                        concatenationDernierPointage.append(dernierEnregistrementBDD.getCommentaires());
                        break;
                }
            }
            runOnUiThread(() -> {
                tvDernierPointage.setText(concatenationDernierPointage);
            });
        } else {
            runOnUiThread(() -> {
                tvDernierPointage.setText(getString(R.string.pas_de_pointage));
            });
        }
    });

    private void debutPointage(View view) {
        setContentView(R.layout.pointage_debut);
        setDateTitleAndCheckButtonListener();
        tvDernierPointage = findViewById(R.id.tv_dernierPointage);
        new Thread(dernierPointageDebutEtFinAffichage).start();
    }

    public void valeurBoutonRadioDebut(View view) {
        // Is the button now checked?
        boolean checked = false;
        // Check which radio button was clicked
        RadioButton entreprise = findViewById(R.id.CB_entreprise_debut);
        RadioButton lieu = findViewById(R.id.CB_chantier_debut);
        RadioButton pause = findViewById(R.id.CB_pause_debut);
        RadioButton autre = findViewById(R.id.CB_autre_debut);
        validationPointage = findViewById(R.id.btn_validationPointage_debut);

        do {
            etatPointage = 1;
            if (entreprise.isChecked()) {
                lieuDePointage = 0;
                checked = true;
            } else if (lieu.isChecked()) {
                lieuDePointage = 1;
                checked = true;
            } else if (pause.isChecked()) {
                lieuDePointage = 3;
                checked = true;
            } else if (autre.isChecked()) {
                lieuDePointage = 5;
                checked = true;
            }

        } while (!checked);
        validationPointage.setOnClickListener(this::selectionAffichageLieuxDePointage);
    }

    private void finPointage(View view) {
        setContentView(R.layout.pointage_fin);
        setDateTitleAndCheckButtonListener();
        tvDernierPointage = findViewById(R.id.tv_dernierPointage);
        new Thread(dernierPointageDebutEtFinAffichage).start();
    }

    public void valeurBoutonRadioFin(View view) {
        // Is the button now checked?
        boolean checked = true;
        // Check which radio button was clicked
        RadioButton entreprise = findViewById(R.id.CB_entreprise_fin);
        RadioButton chantier = findViewById(R.id.CB_chantier_fin);
        RadioButton pause = findViewById(R.id.CB_pause_fin);
        RadioButton autre = findViewById(R.id.CB_autre_fin);
        RadioButton journee = findViewById(R.id.CB_journee_fin);
        etatPointage = 2;
        do {
            if (entreprise.isChecked()) {
                lieuDePointage = 0;
            } else if (chantier.isChecked()) {
                lieuDePointage = 1;
            } else if (pause.isChecked()) {
                lieuDePointage = 3;
            } else if (autre.isChecked()) {
                lieuDePointage = 5;
            } else if (journee.isChecked()) {
                lieuDePointage = 4;
            } else {
                checked = false;
            }
        } while (!checked);
        validationPointage = findViewById(R.id.btn_validationPointage_fin);
        validationPointage.setOnClickListener(this::selectionAffichageLieuxDePointage);
    }

    private void selectionAffichageLieuxDePointage(View view) {
        switch (lieuDePointage) {
            case 0:
            case 3:
            case 4:
                creationPointage();
                break;
            case 1:
                setContentView(R.layout.pointage_detail_endroit);
                findViewById(R.id.btn_Retour).setOnClickListener(this::debutPointage);
                nouveauLieu = findViewById(R.id.ajout_lieu);
                nouveauLieu.setOnClickListener(this::ajoutLieuListe);
                selectionLieuRecyclerView();
                break;
            case 2:
                break;
            case 5:
                setContentView(R.layout.pointage_quel_raison_autre);
                findViewById(R.id.btn_Retour).setOnClickListener(this::debutPointage);
                validationPointage = findViewById(R.id.btn_validationPointage_raisonAutre);
                validationPointage.setOnClickListener(this::enregistrementAutreRaison);

                break;
            default:
                onResume();
                break;
        }
    }

    private void ajoutLieuListe(View view) {
        Intent intentLieu = new Intent(this, AjoutDeLieu.class);

        intentLieu.putExtra("user", user);
        intentLieu.putExtra("password", "");
        intentLieu.putExtra("class", getClass().getName());
        intentLieu.putExtra("classEchec", getClass().getName());
        intentLieu.putExtra("classReussite", getClass().getName());
        intentLieu.putExtra("fonction", "");

        startActivity(intentLieu);
    }

    private void enregistrementAutreRaison(View view) {
        commentaires = findViewById(R.id.et_raison_autre).getAutofillValue().getTextValue().toString();
        creationPointage();
    }

    private void pagePrecedente(View view) {
        Intent pagePrecedente = new Intent(this, EcranDeConnexion.class);

        pagePrecedente.putExtra("user", user);
        pagePrecedente.putExtra("password", "");
        pagePrecedente.putExtra("class", getClass().getName());
        pagePrecedente.putExtra("classEchec", "");
        pagePrecedente.putExtra("classReussite", "");
        pagePrecedente.putExtra("fonction", "");

        pagePrecedente.putExtra("typeAcces", typeAcces);

        startActivity(pagePrecedente);
    }

    private void retour(View view) {
        onResume();
    }

    Thread RecyclerViewThread = new Thread(() -> runOnUiThread(() -> {
        androidx.recyclerview.widget.RecyclerView rv = findViewById(R.id.RV_1);
        androidx.recyclerview.widget.RecyclerView.LayoutManager RLM = new LinearLayoutManager(this);
        RecyclerView_1_ligne_2_String RCVP = new RecyclerView_1_ligne_2_String(dataRecycler, this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(RLM);
        rv.setAdapter(RCVP);
    }));

    private void selectionFournisseurRecyclerview() {
    }

    ArrayList<Lieu> tableauLieuPointage;

    private void selectionLieuRecyclerView() {
        ExecutorService selectionLieu = Executors.newSingleThreadExecutor();
        selectionLieu.execute(() -> {
            runOnUiThread(() -> {
            });
// <<<<<<<<<<<<<<<<<<<<<<<<           TRAITEMENT DE LA 2ND THREAD          >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            PersoDatabase BDD = AccesBDD.getConnexionBDD();
            tableauLieuPointage = (ArrayList<Lieu>) BDD.DaoLieu().findAll();
            // si tableau vide, début installation programme

// <<<<<<<<<<<<<<<<<<<<<<<<         FIN  TRAITEMENT DE LA 2ND THREAD          >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

            int i = 0;
            dataRecycler = new String[tableauLieuPointage.size()][2];
            for (Lieu lieuPointage : tableauLieuPointage) {
                dataRecycler[i][0] = lieuPointage.getName();
                dataRecycler[i][1] = lieuPointage.getTown();
                i++;
            }
            new Thread(RecyclerViewThread).start();
            try {
                RecyclerViewThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void ListenerDeSelectionRecylcerView(Intent intent) {
        idPointage = intent.getIntExtra("position", 0);
        creationPointage();
    }

    private void creationPointage() {

        pointageActuel = new Pointage(utilisateur.getId(), utilisateur.getName(), date.getTime(), commentaires, etatPointage);
        pointageActuel.setLieuDePointage(lieuDePointage);

        pointageActuel.setLatitude(latitude);
        pointageActuel.setLongitude(longitude);

        if (lieuDePointage == 1) {
            Lieu lieuRecup = tableauLieuPointage.get(idPointage);
            pointageActuel.setReferenceLieuDePointage(lieuRecup.getId());
        } else {
            validationPointage.setClickable(false);
            validationPointage.setOnClickListener(null);
        }
        enregistrementDonneesPointage();
    }

    private Pointage referencePointageCreation(Pointage referencePointage, int etatPointage) {
        Pointage pointage = new Pointage(referencePointage.getIdUser(),
                referencePointage.getNomUtilisateur(),
                referencePointage.getDateDebut(),
                referencePointage.getCommentaires(),
                etatPointage);
        pointage.setReferenceLieuDePointage(referencePointage.getReferenceLieuDePointage());
        pointage.setLieuDePointage(referencePointage.getLieuDePointage());
        pointage.setEtatJournee(referencePointage.getEtatJournee());
        pointage.setLongitude(referencePointage.getLongitude());
        pointage.setLatitude(referencePointage.getLatitude());

        return pointage;
    }

    private Pointage creationPointageIntermediarePlus1(Pointage referencePointage) {
        long rajoutUneMs;
        rajoutUneMs = referencePointage.getDateDebut();
        rajoutUneMs++;
        Pointage pointageCreer = referencePointageCreation(referencePointage, 2);
        pointageCreer.setDateDebut(rajoutUneMs);
        return pointageCreer;
    }

    private Pointage creationPointageIntermediareMoins1(Pointage referencePointage) {
        long retraitUneMs;
        retraitUneMs = referencePointage.getDateDebut();
        retraitUneMs--;
        Pointage pointageCreer = referencePointageCreation(referencePointage, 1);
        pointageCreer.setDateDebut(retraitUneMs);
        return pointageCreer;
    }

    public void enregistrementDonneesPointage() {
        ExecutorService enregistrement = Executors.newSingleThreadExecutor();
        enregistrement.execute(() -> {
// <<<<<<<<<<<<<<<<<<<<<<<<           TRAITEMENT DE LA 2ND THREAD          >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            PersoDatabase BDD = AccesBDD.getConnexionBDD();

            ////      CONTROLE DE LA BONNE SAISIE DES POINTAGES        \\\\
            List<Pointage> recupDernierPointage = BDD.DaoPointage().findAllLimit(1);

            //  --- --- --- Controle de la bonne suite entre DEBUT et FIN --- --- ---
            ArrayList<Pointage> pointagesAEnregistre = new ArrayList<>();
            if (recupDernierPointage.size() > 0) {
                dernierEnregistrement = recupDernierPointage.get(0);
                switch (pointageActuel.getEtatPointage()) {
                    case 1:
                        if (pointageActuel.getEtatPointage() == dernierEnregistrement.getEtatPointage()) {
                            pointageAInsererAvantPointageActuel = creationPointageIntermediarePlus1(dernierEnregistrement);
                            pointagesAEnregistre.add(pointageAInsererAvantPointageActuel);
                        }
                        break;
                    case 2:
                        if (pointageActuel.getEtatPointage() == dernierEnregistrement.getEtatPointage()) {
                            pointageAInsererAvantPointageActuel = creationPointageIntermediareMoins1(pointageActuel);
                            pointagesAEnregistre.add(pointageAInsererAvantPointageActuel);
                        } else if (dernierEnregistrement.getEtatPointage() == 1) {
                            if (pointageActuel.getLieuDePointage() != dernierEnregistrement.getLieuDePointage() ||
                                    pointageActuel.getReferenceLieuDePointage() != dernierEnregistrement.getReferenceLieuDePointage()) {
                                pointageAInsererAvantPointageActuel = creationPointageIntermediarePlus1(dernierEnregistrement);
                                pointagesAEnregistre.add(pointageAInsererAvantPointageActuel);
                                pointageAInsererAvantPointageActuel = creationPointageIntermediareMoins1(pointageActuel);
                                pointagesAEnregistre.add(pointageAInsererAvantPointageActuel);
                            }
                        } else {
                            pointageAInsererAvantPointageActuel = creationPointageIntermediareMoins1(pointageActuel);
                            pointagesAEnregistre.add(pointageAInsererAvantPointageActuel);
                        }
                        break;
                    default:
                        break;
                }
            }
            pointagesAEnregistre.add(pointageActuel);
            for (int i = 0; i < pointagesAEnregistre.size(); i++) {
                Pointage pointage = referencePointageCreation(pointagesAEnregistre.get(i), pointagesAEnregistre.get(i).getEtatPointage());
                BDD.DaoPointage().insertPointageEntity(pointage);
            }
// <<<<<<<<<<<<<<<<<<<<<<<<         FIN  TRAITEMENT DE LA 2ND THREAD          >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //Post traitement
            etatPointage = lieuDePointage = idPointage = 0;
            commentaires = "";
            runOnUiThread(this::onResume);
        });
    }

    private void lecturePointages(View view) {
        pointagePrecedent = new StringBuilder();
        desactivationBouton();
        ExecutorService lectureDonnees = Executors.newSingleThreadExecutor();
        lectureDonnees.execute(() -> {
            fermetureFenetreToast = new CountDownLatch(1);
            //postTraitement
// <<<<<<<<<<<<<<<<<<<<<<<<           TRAITEMENT DE LA 2ND THREAD          >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //connexion BDD
            PersoDatabase BDD = AccesBDD.getConnexionBDD();
            // *****************           RECUPERATION DES DERNIERS POINTAGES          ****************************
            // limitation des donnees recues
            int limitAffichagePointage = 6;
            listPointage = (ArrayList<Pointage>) BDD.DaoPointage().findListById(utilisateur.getId(), limitAffichagePointage);
            int indice = listPointage.size() - 1; // mise en place d'un indice pour contrôle sur les pointages suivants
            SimpleDateFormat formatagedate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);
            SimpleDateFormat formatagedate2 = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);

            // *****************           LECTURE DES INFOS BDD           ****************************
            for (int i = 0; i <= indice; i++) {
                String testNomLieu = BDD.DaoLieu().findNameLieu(listPointage.get(i).getReferenceLieuDePointage());
                String dateInit = formatagedate.format(listPointage.get(i).getDateDebut()); // Date BDD formatage long
                String date1 = formatagedate2.format(listPointage.get(i).getDateDebut());   // Date BDD formatage court

                if (i == 0) {
                    pointagePrecedent.append("/******************************/\n");
                }
                pointagePrecedent.append(listPointage.get(i).getNomUtilisateur()).append("\n");
//                pointagePrecedent.append(" Pointé le ").append(dateInit).append("\n");

                switch (listPointage.get(i).getEtatPointage()) {
                    case 1:
                        pointagePrecedent.append(getString(R.string.lieux_de_pointage));
                        switch (listPointage.get(i).getLieuDePointage()) {
                            case 0:
                                pointagePrecedent.append(getString(R.string.entreprise));
                                break;
                            case 1:
                                pointagePrecedent.append(testNomLieu);
                                break;
                            case 2:
                                break;
                            case 3:
                                pointagePrecedent.append(getString(R.string.en_pause));
                                break;
                            case 4:
                                pointagePrecedent.append(getString(R.string.fin_de_journee));
                                break;
                            case 5:
                                pointagePrecedent.append(getString(R.string.autres_raisons));
                                pointagePrecedent.append("\n");
                                pointagePrecedent.append(getString(R.string.commentaires) + "\n");
                                pointagePrecedent.append(listPointage.get(i).getCommentaires());
                                break;
                        }
                        pointagePrecedent.append("\n" + getString(R.string.debut) + " ").append(dateInit).append("\n");
                        break;
                    case 2:
                        pointagePrecedent.append(getString(R.string.lieux_de_pointage));
                        if (i + 1 <= listPointage.size()) {
                            switch (listPointage.get(i).getLieuDePointage()) {
                                case 0:
                                    pointagePrecedent.append(getString(R.string.entreprise));
                                    break;
                                case 1:
                                    pointagePrecedent.append(testNomLieu);
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    pointagePrecedent.append(getString(R.string.en_pause));
                                    break;
                                case 4:
                                    pointagePrecedent.append(getString(R.string.fin_depointage));
                                    break;
                                case 5:
                                    pointagePrecedent.append(getString(R.string.autres_raisons));
                                    pointagePrecedent.append("\n");
                                    pointagePrecedent.append(getString(R.string.commentaires));
                                    pointagePrecedent.append(listPointage.get(i).getCommentaires());
                                    break;
                            }
                        }
                        if (i + 1 < listPointage.size()) {
                            i++;
                        }
                        pointagePrecedent.append("\n").append(getString(R.string.debut)).append(" ").append(formatagedate.format(listPointage.get(i).getDateDebut())).append("\n");

                        pointagePrecedent.append(getString(R.string.fin)).append(" ").append(dateInit).append("\n");

                        break;
                    case 3:
                        pointagePrecedent.append(getString(R.string.maladie_ou_congee)).append("\n");
                        break;
                    default:
                        pointagePrecedent.append(getString(R.string.a_controler)).append("\n");
                        break;
                }
                pointagePrecedent.append("\n");
                if (listPointage.get(i).getEtatJournee() == 1) {
                    pointagePrecedent.append("< - - - - - - - - - - - - - >\n");
                }
                if (i < indice) {
                    String date2 = formatagedate2.format(listPointage.get(i + 1).getDateDebut());
                    if (!date1.equals(date2)) {
                        pointagePrecedent.append("/- - - - - - - - - - - - - - - - - - - - - - - - - - - -/\n");
                    }
                }
                if (i == indice) {
                    pointagePrecedent.append("/******************************/\n");
                }
            }
// <<<<<<<<<<<<<<<<<<<<<<<<         FIN  TRAITEMENT DE LA 2ND THREAD          >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

            //Post traitement
            runOnUiThread(() -> {
                View test = findViewById(R.id.L_remplacementToast);
                test.setVisibility(View.VISIBLE);
                TextView toastExterne = findViewById(R.id.test_toast_remplacement);
                Button retourToast = findViewById(R.id.btn_RetourToast);
                retourToast.setOnClickListener(v -> fermetureFenetreToast.countDown());
                if (listPointage.size() > 0) {
                    toastExterne.setText(pointagePrecedent.toString());
                } else
                    toastExterne.setText(getString(R.string.pas_de_pointage));
            });
            new Thread(toastCompteur).start();
        });
    }

    CountDownLatch fermetureFenetreToast;

    //Thread durée affichage pointage
    Thread toastCompteur = new Thread(() -> {
        int compteur = 20;
        boolean fin = false;
        try {
            fermetureFenetreToast.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> {
            View test = findViewById(R.id.L_remplacementToast);
            test.setVisibility(View.GONE);
            activationBouton();
        });
    });

    //Géo-localisation
//
//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        longitude = location.getLongitude();
//        latitude = location.getLatitude();
//    }
//
//    @Override
//    public void onFlushComplete(int requestCode) {
//        LocationListener.super.onFlushComplete(requestCode);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, new int[]{PackageManager.PERMISSION_GRANTED});
//        if (requestCode != REQUESTCODEVALUE)
//            return;
//        int idxPermFine = -1;
//        for (int i = 0; i < permissions.length && idxPermFine == -1; i++) {
//            if (permissions[i].
//                    equals(Manifest.permission.ACCESS_FINE_LOCATION))
//                idxPermFine = i;
//            grantResults[idxPermFine] = PackageManager.PERMISSION_GRANTED;
//        }
//    }
//
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        LocationListener.super.onStatusChanged(provider, status, extras);
//        switch (status) {
//            case LocationProvider.AVAILABLE:
//            case LocationProvider.OUT_OF_SERVICE:
//            case LocationProvider.TEMPORARILY_UNAVAILABLE:
//        }
//    }
//
//    @Override
//    public void onActivityResult(Object result) {
//
//    }
}
