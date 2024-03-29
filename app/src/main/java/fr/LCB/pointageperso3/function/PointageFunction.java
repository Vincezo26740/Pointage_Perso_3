package fr.LCB.pointageperso3.function;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import fr.LCB.pointageperso3.BuildConfig;
import fr.LCB.pointageperso3.DAO.AccesBDD;
import fr.LCB.pointageperso3.DAO.PersoDatabase;
import fr.LCB.pointageperso3.Entity.ConfigAppli;
import fr.LCB.pointageperso3.Entity.Lieu;
import fr.LCB.pointageperso3.Entity.Pointage;
import fr.LCB.pointageperso3.Entity.Societe;

import fr.LCB.pointageperso3.Entity.User;

import fr.LCB.pointageperso3.R;
import fr.LCB.pointageperso3.Threads.EnvoieMail;
import fr.LCB.pointageperso3.Threads.ImportBDDInfos;
import fr.LCB.pointageperso3.adapterViewRecycler.RecyclerView_1_ligne_2_String;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//@SuppressWarnings("rawtypes")
public class PointageFunction extends AppCompatActivity implements RecyclerView_1_ligne_2_String.ListenerDeSelection, LocationListener {


    // Récupération Intent
    private String user;
    private String fonction;

//    private CountDownLatch retourUser, retourConfig, retourDernierPointage;

    private TextView tvDernierPointage;
    private TextView commentairesTV;
    private Button validationPointage;
    private Button retour;
    private Button checkPointage = null;

    private String commentaires = "";
    private String typeAcces;
    private String[][] dataRecycler;
    private StringBuilder pointagePrecedent;

    private int etatPointage, idPointage, lieuDePointage;

    private User utilisateur;
    private Pointage pointageActuel, dernierEnregistrement;
    private Date date;

    LocationManager locationManager;
    //    private ArrayList<Pointage> listPointage;
    private final SimpleDateFormat formatagedate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);

    double latitude, longitude;

//    private boolean fonctionUsed;
//
//    private Pointage dernierPointage;

    //****************//
//    User utilisateur;
    private Societe societe;
    private ArrayList<Pointage> listeDePointages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();
    }

    private void recupIntent() {
        user = getIntent().getStringExtra("user");
        String password = getIntent().getStringExtra("password");
        String classInit = getIntent().getStringExtra("class");
        String classEchec = getIntent().getStringExtra("classEchec");
        String classReussite = getIntent().getStringExtra("classReussite");
        fonction = getIntent().getStringExtra("fonction");
    }

    String AUTORISATIONGPSCOARSE = Manifest.permission.ACCESS_COARSE_LOCATION;
    String AUTORISATIONGPSFIN = Manifest.permission.ACCESS_FINE_LOCATION;
    int REQUESTCODEVALUE = 100;

    CountDownLatch majInfoBdd;

    private void requestPermissions() {
        ArrayList<String> listPermission = new ArrayList<>();
        String AUTORISATIONLECTURE = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONLECTURE) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONLECTURE);
        }
        String AUTORISATIONECRITURE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONECRITURE) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONECRITURE);
        }
        // Constante acces permissions application

        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONGPSCOARSE) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONGPSCOARSE);
        }
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONGPSFIN) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONGPSFIN);
        }
        String AUTORISATIONTELEPHONE = Manifest.permission.CALL_PHONE;
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONTELEPHONE) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONTELEPHONE);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            String AUTORISATIONLECTURETELEPHONENUMERO = Manifest.permission.READ_PHONE_NUMBERS;
            if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONLECTURETELEPHONENUMERO) != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(AUTORISATIONLECTURETELEPHONENUMERO);
            }
        }
        String AUTORISATIONSMS = Manifest.permission.SEND_SMS;
        if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONSMS) != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(AUTORISATIONSMS);
        }
        if (Build.VERSION.SDK_INT >= 30) {
            String AUTORISATIONMANAGE = Manifest.permission.MANAGE_EXTERNAL_STORAGE;
            if (ActivityCompat.checkSelfPermission(MonAppContext.context, AUTORISATIONMANAGE) != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(AUTORISATIONMANAGE);
            }
        }
        String[] listeAutorisation = new String[listPermission.size()];
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
        //Géo-localisation
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);    //Géo-localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{AUTORISATIONGPSFIN, AUTORISATIONGPSCOARSE}, REQUESTCODEVALUE);
            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
        }
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 100, this);
        }

        majInfoBdd = new CountDownLatch(1);
        new Thread(majInfoAppli).start();
        try {
            majInfoBdd.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String bienvenueUser = getString(R.string.pointage_de) + " \n" + utilisateur.getFirstName();
        setContentView(R.layout.accueil);

        TextView title = findViewById(R.id.tv_title_pointage);
        title.setText(bienvenueUser);
        activationBouton();
        if (fonction.equals("envoieEmail")) {
            EnvoiMailViaMessagerie();
        }
    }

    private void activationBouton() {
        retour = findViewById(R.id.btn_Retour);
        retour.setOnClickListener(this::pagePrecedente);

        Button absence = findViewById(R.id.btn_pointage_absence);
        absence.setOnClickListener(this::declarationAbsence);

        checkPointage = findViewById(R.id.btn_CtrlPointage);
        checkPointage.setOnClickListener(this::lecturePointages);

        Button debutPointageButton = findViewById(R.id.btn_pointage_debut);
        Button finPointageButton = findViewById(R.id.btn_pointage_fin);
        if (listeDePointages.size() > 0) {
            if (listeDePointages.get(0).getDateFin() != 0) {
                debutPointageButton.setVisibility(View.VISIBLE);
                debutPointageButton.setOnClickListener(this::debutPointage);
                finPointageButton.setVisibility(View.GONE);
                finPointageButton.setOnClickListener(null);
            } else {
                debutPointageButton.setVisibility(View.GONE);
                debutPointageButton.setOnClickListener(null);
                finPointageButton.setVisibility(View.VISIBLE);
                finPointageButton.setOnClickListener(this::finPointage);
            }
        } else {
            debutPointageButton.setVisibility(View.VISIBLE);
            debutPointageButton.setOnClickListener(this::debutPointage);
            finPointageButton.setVisibility(View.GONE);
            finPointageButton.setOnClickListener(null);
        }


        Button parametres = findViewById(R.id.btn_Parametres);
        parametres.setOnClickListener(this::parametres);

        Button creationFichier = findViewById(R.id.btn_CtrlPointage2);
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

    private void EnvoiMailViaMessagerie() {

        // reset de l'intent fonction pour éviter une boucle infinie
        getIntent().putExtra("fonction", "");

        File path = EnvoieMail.getPathFichier();
        String messageText = getString(R.string.corps_fichier_csv) + user;
//        Uri uriPath = Uri.fromFile(path);

        CountDownLatch majAttente = new CountDownLatch(6);
        ImportBDDInfos majInfosAppli = new ImportBDDInfos(majAttente);
        new Thread(majInfosAppli).start();
        utilisateur = DonneesDeLApplication.getUtilisateur();
        societe = DonneesDeLApplication.getSociete();
        if (societe == null) {
            Intent renvoieVersLaPageDeParametreDeLaSociete = new Intent(this, Parametres.class);

            renvoieVersLaPageDeParametreDeLaSociete.putExtra("user", user);
            renvoieVersLaPageDeParametreDeLaSociete.putExtra("password", "");
            renvoieVersLaPageDeParametreDeLaSociete.putExtra("class", getClass().getName());
            renvoieVersLaPageDeParametreDeLaSociete.putExtra("classEchec", EcranDeConnexion.class.getName());
            renvoieVersLaPageDeParametreDeLaSociete.putExtra("classReussite", PointageFunction.class.getName());
            renvoieVersLaPageDeParametreDeLaSociete.putExtra("fonction", "ajout1User");

            startActivity(renvoieVersLaPageDeParametreDeLaSociete);
        } else {
//            confAppli = DonneesDeLApplication.getConfAppli();
//            listeDesUtilisateurs = DonneesDeLApplication.getListeDesUtilisateurs();
            listeDePointages = DonneesDeLApplication.getListeDePointages();
//            listeDeLieux = DonneesDeLApplication.getListeDeLieux();
            try {
                majAttente.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // requete pour envoie mail
            Intent sendMail = new Intent(Intent.ACTION_SEND);
            sendMail.setType("message/rfc822"); // setting file format
            sendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{societe.getEmail()});
            sendMail.putExtra(Intent.EXTRA_SUBJECT, messageText);// setting corps de page
            sendMail.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //création du provider pour échange avec les autres programmes
            sendMail.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", path));
//            sendMail.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), this.getPackageName() + ".provider", path));


            //choix du choix du programme à utilisé
//            startActivity(Intent.createChooser(sendMail, getString(R.string.titre_chooser_application_mail)));
            startActivity(sendMail);
        }
    }

    private void parametres(View view) {
        Intent goParametre = new Intent(this, Parametres.class);

        goParametre.putExtra("user", utilisateur.getName());
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
        commentairesTV = findViewById(R.id.ET_abcenceAutre);
        commentaires = commentaires + commentairesTV.getText();
        creationPointage();
    }

    Thread dernierPointageDebutEtFinAffichage = new Thread(() -> {
        CountDownLatch maj = new CountDownLatch(6);
        ImportBDDInfos majInfosAppli = new ImportBDDInfos(maj);
        new Thread(majInfosAppli).start();
        try {
            maj.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (listeDePointages.size() > 0) {
            String dernierEnregistrementBDD = listeDePointages.get(0).toString();
            runOnUiThread(() -> tvDernierPointage.setText(dernierEnregistrementBDD));
        } else {
            runOnUiThread(() -> tvDernierPointage.setText(getString(R.string.pas_de_pointage)));
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
        RadioButton finPointagePrecedent = findViewById(R.id.CB_fin_pointage_precedent);
        RadioButton autre = findViewById(R.id.CB_autre_fin);
        RadioButton journee = findViewById(R.id.CB_journee_fin);
        etatPointage = 2;
        do {
            if (finPointagePrecedent.isChecked()) {
                lieuDePointage = 0;
            } else if (autre.isChecked()) {
                lieuDePointage = 5;
            } else if (journee.isChecked()) {
                commentaires = getString(R.string.fin_de_journee);
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
                Button nouveauLieu = findViewById(R.id.ajout_lieu);
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
        commentairesTV = findViewById(R.id.et_raison_autre);
        commentaires = commentairesTV.getText().toString();
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

//    private void selectionFournisseurRecyclerview() {
//    }

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

        //Géo-localisation
        pointageActuel.setLatitude(latitude);
        pointageActuel.setLongitude(longitude);
        //Géo-localisation
//        if (recupDernierPointage != null) {
//            if (recupDernierPointage.getLatitude() != pointageActuel.getLatitude()) {
//                pointageActuel.setLatitude(recupDernierPointage.getLatitude());
//            }
//            if (recupDernierPointage.getLongitude() != pointageActuel.getLongitude()) {
//                pointageActuel.setLongitude(longitude);
//            }
            if (latitude == 0 && longitude == 0) {
                if (recupDernierPointage != null) {
                    commentaires = getString(R.string.a_controler) + " \n " + commentaires;
                } else {
                    commentaires = getString(R.string.a_controler);
                }
                pointageActuel.setCommentaires(commentaires);
            }
//        }else{
//
//        }

        if (lieuDePointage == 1) {
            Lieu lieuRecup = tableauLieuPointage.get(idPointage);
            pointageActuel.setReferenceLieuDePointage(lieuRecup.getId());
        } else {
            validationPointage.setClickable(false);
            validationPointage.setOnClickListener(null);
        }
        enregistrementDonneesPointage();
    }

    Pointage recupDernierPointage;

    public void enregistrementDonneesPointage() {
        ExecutorService enregistrement = Executors.newSingleThreadExecutor();
        enregistrement.execute(() -> {
// <<<<<<<<<<<<<<<<<<<<<<<<           TRAITEMENT DE LA 2ND THREAD          >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            PersoDatabase BDD = AccesBDD.getConnexionBDD();

            ////      CONTROLE DE LA BONNE SAISIE DES POINTAGES        \\\\
            recupDernierPointage = BDD.DaoPointage().findLast();

            //  --- --- --- Controle de la bonne suite entre DEBUT et FIN --- --- ---
//            ArrayList<Pointage> pointagesAEnregistre = new ArrayList<>();
            if (recupDernierPointage != null) {
                dernierEnregistrement = recupDernierPointage;
                if (pointageActuel.getEtatPointage() == 2) {
                    commentaires = dernierEnregistrement.getCommentaires() + " \n" + pointageActuel.getCommentaires();
                    if (pointageActuel.getLongitude() != 0 && pointageActuel.getLatitude() != 0) {
                        dernierEnregistrement.setLongitude(pointageActuel.getLongitude());
                        dernierEnregistrement.setLatitude(pointageActuel.getLatitude());
                    }
                    dernierEnregistrement.setDateFin(pointageActuel.getDateDebut());
                    dernierEnregistrement.setCommentaires(commentaires);
                    BDD.DaoPointage().update(dernierEnregistrement);
                } else {
                    if (pointageActuel.getEtatPointage() != 1) {
                        pointageActuel.setDateFin(pointageActuel.getDateDebut() + 1);
                    }
                    BDD.DaoPointage().insertPointageEntity(pointageActuel);
                }
            } else {
                BDD.DaoPointage().insertPointageEntity(pointageActuel);
            }
            BDD.close();

            majInfoBdd = new CountDownLatch(1);
            new Thread(majInfoAppli).start();
            try {
                majInfoBdd.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//
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

            int indice = listeDePointages.size() - 1; // mise en place d'un indice pour contrôle sur les pointages suivants
//            SimpleDateFormat formatagedate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);
//            SimpleDateFormat formatagedate2 = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);

            // *****************           LECTURE DES INFOS BDD           ****************************
            for (int i = 0; i <= indice; i++) {
                pointagePrecedent.append(listeDePointages.get(i).toString());
                if (listeDePointages.get(i).getEtatJournee() == 1) {
                    pointagePrecedent.append("< - - - - - - - - - - - - - >\n");
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
                if (listeDePointages.size() > 0) {
                    toastExterne.setText(pointagePrecedent.toString());
                } else
                    toastExterne.setText(getString(R.string.pas_de_pointage));
            });
            new Thread(toastCompteur).start();
        });
    }

    CountDownLatch fermetureFenetreToast;

    Thread majInfoAppli = new Thread(() -> {
        CountDownLatch majAttente = new CountDownLatch(6);
        ImportBDDInfos majInfosAppli = new ImportBDDInfos(majAttente);
        new Thread(majInfosAppli).start();
        utilisateur = DonneesDeLApplication.getUtilisateur();
        societe = DonneesDeLApplication.getSociete();
        ConfigAppli confAppli = DonneesDeLApplication.getConfAppli();
        ArrayList<User> listeDesUtilisateurs = DonneesDeLApplication.getListeDesUtilisateurs();
        listeDePointages = DonneesDeLApplication.getListeDePointages();
        ArrayList<Lieu> listeDeLieux = DonneesDeLApplication.getListeDeLieux();
        try {
            majAttente.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        majInfoBdd.countDown();
    });

    //Thread durée affichage pointage
    Thread toastCompteur = new Thread(() -> {
//        int compteur = 20;
//        boolean fin = false;
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        Log.i(getString(R.string.TAG), "\n lat: " + latitude + " \n long: " + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
//        LocationListener.super.onProviderDisabled(provider);
//        Log.i(getString(R.string.TAG), "onProviderDisabled: ");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
//        LocationListener.super.onProviderEnabled(provider);
//        Log.i(getString(R.string.TAG), "onProviderEnabled: ");
    }
    //Géo-localisation

}






