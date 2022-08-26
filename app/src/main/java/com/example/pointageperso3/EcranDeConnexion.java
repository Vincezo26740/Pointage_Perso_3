package com.example.pointageperso3;

import static android.text.Html.escapeHtml;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.ConfigAppli;
import Entity.Lieu;
import Entity.Pointage;
import Entity.Societe;
import Entity.User;
import Threads.ImportBDDInfos;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EcranDeConnexion extends AppCompatActivity {
    private final String TAG = MonAppContext.context.getString(R.string.TAG);

    private boolean erreurConnexion = false, premierCo = false;
    private String messageSnackbar = "Erreur identifiant/mot de passe";
    //AUTORISATIONS
    private ConfigAppli configInitiale;

    private CountDownLatch CDLAttenteAutorisation, CDLAjoutUser;

    // Gestion de la vue connexion utilisateur
    private EditText name, passWord;
    private TextView oubliePassWord;
    private boolean nameUserOk, passWordUserOk;
    private Button validationUser, viewMdp;
    private String nameUser, passWordUser;
    private User utilisateurRecupImportBDD;

    // Gestion de la vue création nouvel utilisateur
    private TextView nameNewUser, emailNewUser, adresseNewUser, villeNewUser, CPNewUser;
    private boolean nameOk, emailOK, adresseOk, CPOk, villeOk;
    private Button validationNewUser;

    User utilisateur;
    Societe societe;
    ConfigAppli confAppli;
    ArrayList<User> listeDesUtilisateurs;
    ArrayList<Pointage> listeDePointages;
    ArrayList<Lieu> listeDeLieux;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CDLAttenteAutorisation = new CountDownLatch(1);
        CDLAjoutUser = new CountDownLatch(1);
// ***** ////  récupération de données récupérées par le chargement de l'application et infos appliTest  /// ***
        utilisateur = infosAppliTest.getUtilisateur();
        societe = infosAppliTest.getSociete();
        confAppli = infosAppliTest.getConfAppli();
        listeDesUtilisateurs = infosAppliTest.getListeDesUtilisateurs();
        listeDePointages = infosAppliTest.getListeDePointages();
        listeDeLieux = infosAppliTest.getListeDeLieux();
// ***** ////  récupération de données récupérées par le chargement de l'application et infos appliTest  /// ***

    }

    @Override
    public void onResume() {
        super.onResume();
        // contrôle de lancement de la 1ère config
        if (confAppli == null) {// contrôle de l'application déjà configurée
            firstConfig(); // lancement de la page de première connexion
        } else {
            // lancement de l'application normale
            //initialisation de variables
            setContentView(R.layout.page_de_connexion);
            oubliePassWord = findViewById(R.id.tv_oublieMdp);
            passWord = findViewById(R.id.et_mdp);
            name = findViewById(R.id.et_username);
            validationUser = findViewById(R.id.btn_validation_connexion);
            viewMdp = findViewById(R.id.btn_view_mdp);

            nameUser = escapeHtml(name.getAutofillValue().getTextValue().toString());
            passWordUser = escapeHtml(passWord.getAutofillValue().getTextValue().toString());

            // afffectation des fonction
            validationUser.setOnClickListener(this::ValidationUser);
            oubliePassWord.setOnClickListener(this::OublieMdp);
            // affichage ou pas passWord
            viewMdp.setOnClickListener(v -> {
                Log.i(TAG, "onResume: " + passWord.getInputType());
                if (passWord.getInputType() == 129) {
                    passWord.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    viewMdp.setBackground(getDrawable(R.drawable.visibility_mdp_rouge));
                    viewMdp.setForeground(getDrawable(R.drawable.visibility_mdp_rouge));

                } else {
                    passWord.setInputType(129);
                    viewMdp.setBackground(getDrawable(R.drawable.invisibility_mdp_noir));
                    viewMdp.setForeground(getDrawable(R.drawable.invisibility_mdp_noir));
                }
            });

            // Contrôle de présence valeur dans l'édite text
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    nameUserOk = !s.toString().isEmpty();
                }
            });

            // Contrôle de présence valeur dans l'édite text
            passWord.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    passWordUserOk = !s.toString().isEmpty();
                }
            });

            do {
                validationUser.setVisibility(View.INVISIBLE);
            } while (passWordUserOk && nameUserOk);
            validationUser.setVisibility(View.VISIBLE);
            validationUser.setOnClickListener(this::ValidationUser);
        }
    }

    private void OublieMdp(View view) {
        Intent intent = new Intent(this, MajMDP.class);

        intent.putExtra("user", nameUser);
        intent.putExtra("password", "");
        intent.putExtra("class", getClass().getName());
        intent.putExtra("classEchec", getClass().getName());
        intent.putExtra("classReussite", PointageFunction.class.getName());
        intent.putExtra("fonction", "OublieMdp");

        startActivity(intent);
    }

    //insertion nouvel utilisateur dans la BDD / mot de passe pas encore configuré
    Thread ajoutNewUser = new Thread(() -> {

        User nouvelUtilisateur = new User(escapeHtml(nameNewUser.getAutofillValue().getTextValue().toString()),
                escapeHtml(emailNewUser.getAutofillValue().getTextValue().toString()),
                escapeHtml(adresseNewUser.getAutofillValue().getTextValue().toString()),
                Integer.parseInt(escapeHtml(CPNewUser.getAutofillValue().getTextValue().toString())),
                escapeHtml(villeNewUser.getAutofillValue().getTextValue().toString()),
                "");
        nouvelUtilisateur.setFirstName(escapeHtml(findViewById(R.id.insert_prenom).getAutofillValue().getTextValue().toString()));
        PersoDatabase insertUser = AccesBDD.getConnexionBDD();
        insertUser.DaoUser().insert(nouvelUtilisateur);
        insertUser.DaoConfigAppli().majFirstUse(false);
        insertUser.close();
        CDLAjoutUser.countDown();
    });

    //ecran de première connexion
    private void firstConfig() {
        setContentView(R.layout.creation_first_user);

        nameNewUser = findViewById(R.id.insert_nom);
        emailNewUser = findViewById(R.id.insert_email);
        adresseNewUser = findViewById(R.id.insert_adresse);
        CPNewUser = findViewById(R.id.insert_CP);
        villeNewUser = findViewById(R.id.insert_town);
        validationNewUser = findViewById(R.id.btn_validation_new_user);

        validationNewUser.setVisibility(View.INVISIBLE);

        nameNewUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    nameOk = true;
                    controleModifText();
                }
            }
        });

        emailNewUser.addTextChangedListener(new TextWatcher() {
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
                        emailNewUser.setTextColor(getColor(R.color.black));
                        emailOK = true;
                        controleModifText();
                    } else {
                        emailNewUser.setTextColor(getColor(R.color.red));
                    }

                }
            }
        });

        adresseNewUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    adresseOk = true;
                    controleModifText();
                }
            }
        });

        CPNewUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern paternRegexMail = Pattern.compile("[a-zA-Z0-9_-]{5,}");
                Matcher matcher;
                matcher = paternRegexMail.matcher(s);
                if (!s.toString().isEmpty()) {
                    if (matcher.matches()) {
                        CPNewUser.setTextColor(getColor(R.color.black));
                        CPOk = true;
                        controleModifText();
                    } else {
                        CPNewUser.setTextColor(getColor(R.color.red));
                    }
                }
            }
        });

        villeNewUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    villeOk = true;
                    controleModifText();
                }
            }
        });

        //si tout les champs sont remplis alors valider affiché

    }

    private void controleModifText() {
        if (nameOk && emailOK && adresseOk && CPOk & villeOk) {
            validationNewUser.setVisibility(View.VISIBLE);
            validationNewUser.setOnClickListener(this::ajout1User);
        } else {
            validationNewUser.setVisibility(View.INVISIBLE);
            validationNewUser.setOnClickListener(null);
        }
    }

    // fonction ajout BDD user et renvoie sur la page de saisie des mot de passe
    private void ajout1User(View view) {
        new Thread(ajoutNewUser).start();
        try {
            CDLAjoutUser.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent envoieDonnee = new Intent(this, MajMDP.class);
        nameUser = nameNewUser.getAutofillValue().getTextValue().toString();
        envoieDonnee.putExtra("user", nameUser);
        envoieDonnee.putExtra("password", "");
        envoieDonnee.putExtra("class", getClass().getName());
        envoieDonnee.putExtra("classEchec", getClass().getName());
        envoieDonnee.putExtra("classReussite", PointageFunction.class.getName());
        envoieDonnee.putExtra("fonction", "ajout1User");

        startActivity(envoieDonnee);
    }

    public void ValidationUser(View view) {
        validationUser.setClickable(false);
        validationUser.setOnClickListener(null);
        nameUser = escapeHtml(findViewById(R.id.et_username).getAutofillValue().getTextValue().toString());
        passWordUser = findViewById(R.id.et_mdp).getAutofillValue().getTextValue().toString();

        if (nameUser.isEmpty() || passWordUser.isEmpty()) {
            erreurConnexion = true;
            messageSnackbar = "Utilisateur/Mot de passe non remplis !!";
            onResume();
        }

        Intent demandeAttenteEtControle = new Intent(this, Attente_affichage.class);

        demandeAttenteEtControle.putExtra("user", nameUser);
        demandeAttenteEtControle.putExtra("password", passWordUser);
        demandeAttenteEtControle.putExtra("class", getClass().getName());
        demandeAttenteEtControle.putExtra("classEchec", getClass().getName());
        demandeAttenteEtControle.putExtra("classReussite", PointageFunction.class.getName());
        demandeAttenteEtControle.putExtra("fonction", "");

        startActivity(demandeAttenteEtControle);
    }
}