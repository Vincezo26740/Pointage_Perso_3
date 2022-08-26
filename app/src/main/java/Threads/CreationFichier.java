package Threads;

import android.icu.text.SimpleDateFormat;
import android.os.Environment;

import com.example.pointageperso3.MonAppContext;
import com.example.pointageperso3.R;

import DAO.AccesBDD;
import DAO.PersoDatabase;
import Entity.Pointage;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;


public class CreationFichier extends Thread {

    private final CountDownLatch travailFini;
    private CountDownLatch donneesRecupere;

    private List<Pointage> pointage;
    private final StringBuilder donnesATransferer = new StringBuilder();

    private Long dernierEnvoi;
    private final Long dateJourDouble;
    private final SimpleDateFormat formatagedate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);

    private final Date dateJour;

    private final String separateur = MonAppContext.context.getString(R.string.separateur_fichier);
    private final String miseALaLigne = MonAppContext.context.getString(R.string.mise_a_la_ligne_fichier);
    private PersoDatabase bddAcces;
    private File newPath;

    public CreationFichier(CountDownLatch travailFini) {

        this.travailFini = travailFini;
        bddAcces = AccesBDD.getConnexionBDD();
        donneesRecupere = new CountDownLatch(1);
        new Thread(newThreadDonnees).start();

        try {
            donneesRecupere.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dateJour = new Date();
        dateJourDouble = dateJour.getTime();
        pointage = bddAcces.DaoPointage().findByDate(dernierEnvoi, dateJourDouble);
        donnesATransferer.append(MonAppContext.context.getString(R.string.prenom_utilisateur)).append(separateur)
                .append(MonAppContext.context.getString(R.string.titre_lieux)).append(separateur)
                .append(MonAppContext.context.getString(R.string.commentaires)).append(separateur)
                .append(MonAppContext.context.getString(R.string.debut)).append(separateur)
                .append(MonAppContext.context.getString(R.string.fin)).append(separateur)
                .append("longitude").append(separateur)
                .append("latitude").append(separateur).append(miseALaLigne);
        if (pointage.size() == 0) {
            pointage = bddAcces.DaoPointage().findByDate(dernierEnvoi, dateJourDouble);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        bddAcces.close();
        if (pointage.size() > 0) {
//            for (Pointage recupPointage : pointage) {
//                donnesATransferer.append(transformationDesDonnes(recupPointage));
//            }
            PersoDatabase BDD = AccesBDD.getConnexionBDD();
            for (int i = 0; i < pointage.size(); i++) {

                String testNomLieu = BDD.DaoLieu().findNameLieu(pointage.get(i).getReferenceLieuDePointage());
                String dateInit = formatagedate.format(pointage.get(i).getDateDebut()); // Date BDD formatage long
                donnesATransferer.append(pointage.get(i).getNomUtilisateur()).append(separateur);

//                pointagePrecedent.append(" Pointé le ").append(dateInit).append("\n");

                switch (pointage.get(i).getEtatPointage()) {
                    case 1:
                        switch (pointage.get(i).getLieuDePointage()) {
                            case 0:
                                donnesATransferer.append(MonAppContext.context.getString(R.string.entreprise));
                                break;
                            case 1:
                                donnesATransferer.append(testNomLieu);
                                break;
                            case 2:
                                break;
                            case 3:
                                donnesATransferer.append(MonAppContext.context.getString(R.string.en_pause));
                                break;
                            case 4:
                                donnesATransferer.append(MonAppContext.context.getString(R.string.debut));
                                break;
                            case 5:
                                donnesATransferer.append(MonAppContext.context.getString(R.string.autre));
                                break;
                        }
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(pointage.get(i).getCommentaires());
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(formatagedate.format(pointage.get(i).getDateDebut())).append(separateur);
                        donnesATransferer.append(formatagedate.format(pointage.get(i + 1).getDateDebut())).append(separateur);
                        donnesATransferer.append(pointage.get(i).getLongitude()).append(separateur).append(pointage.get(i).getLatitude());
                        i++;
                        break;
                    case 2:

                        switch (pointage.get(i).getLieuDePointage()) {
                            case 0:
                                donnesATransferer.append(MonAppContext.context.getString(R.string.entreprise));
                                break;
                            case 1:
                                donnesATransferer.append(testNomLieu);
                                break;
                            case 2:
                                break;
                            case 3:
                                donnesATransferer.append(MonAppContext.context.getString(R.string.en_pause));
                                break;
                            case 4:
                                donnesATransferer.append(MonAppContext.context.getString(R.string.fin_de_journee));
                                break;
                            case 5:
                                donnesATransferer.append(MonAppContext.context.getString(R.string.autre));

                                break;
                        }

//                        if (i + 1 < pointage.size()) {
//                            i++;
//                        }
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(pointage.get(i).getCommentaires());
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(formatagedate.format(pointage.get(i - 1).getDateDebut()));
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(dateInit);
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(pointage.get(i).getLongitude()).append(separateur).append(pointage.get(i).getLatitude());
                        break;
                    case 3:
                        donnesATransferer.append(MonAppContext.context.getString(R.string.maladie_ou_congee));
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(pointage.get(i).getCommentaires());
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(formatagedate.format(pointage.get(i).getDateDebut()));
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(dateInit);
                        donnesATransferer.append(separateur);
                        donnesATransferer.append(pointage.get(i).getLongitude()).append(separateur).append(pointage.get(i).getLatitude());
                        break;
                    default:
                        donnesATransferer.append(MonAppContext.context.getString(R.string.a_controler));
                        break;
                }
                donnesATransferer.append(miseALaLigne);
            }
        }

        try {
            File stockage = Environment.getExternalStoragePublicDirectory(Environment.MEDIA_SHARED);
            File nouveauFichier = new File(stockage, "/DossierExportPointage/Pointages.csv");
            newPath = nouveauFichier;

            File testExistDossier = new File(stockage, "/DossierExportPointage/");

            if (!testExistDossier.exists()) {
                testExistDossier.mkdirs();
            }
            if (!nouveauFichier.exists()) {
                nouveauFichier.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(nouveauFichier);
            fos.write(donnesATransferer.toString().getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();

        } catch (
                Exception e) {
            e.printStackTrace();
        }
        travailFini.countDown();
    }

    Thread newThreadDonnees = new Thread(() -> {
        bddAcces = AccesBDD.getConnexionBDD();
//        dernierEnvoi = Double.doubleToLongBits(bddAcces.DaoDateDernierEnvoie().recupeDerniereDateDouble());
        bddAcces.close();
        donneesRecupere.countDown();
    });

    public File getNewPath() {
        return newPath;
    }

    private String transformationDesDonnes(Pointage recupPointage) {
        int idLieu = recupPointage.getLieuDePointage();
        int idEtatPointage = recupPointage.getEtatPointage();
        String user = recupPointage.getNomUtilisateur();
        String dateTransformee = formatagedate.format(recupPointage.getDateDebut());
        String etatPointage, lieuDePointage;
        String longitude = String.valueOf(recupPointage.getLongitude());
        String latitude = String.valueOf(recupPointage.getLatitude());
        String commentaires = recupPointage.getCommentaires();

        switch (idEtatPointage) {
            case 1:
                etatPointage = String.valueOf(MonAppContext.context.getString(R.string.pointage_debut));
                break;
            case 2:
                etatPointage = String.valueOf(MonAppContext.context.getString(R.string.fin_de_journee));
                break;
            case 3:
                etatPointage = String.valueOf(MonAppContext.context.getString(R.string.absence_raison_maladie));
                break;
            default:
                etatPointage = String.valueOf(MonAppContext.context.getString(R.string.a_controler));
                break;
        }
        switch (idLieu) {
            case 1:
                bddAcces = AccesBDD.getConnexionBDD();
                lieuDePointage = bddAcces.DaoLieu().findNameLieu(recupPointage.getReferenceLieuDePointage());
                bddAcces.close();
                break;
            case 3:
                lieuDePointage = String.valueOf(MonAppContext.context.getString(R.string.en_pause));
                break;
            case 4:
                lieuDePointage = String.valueOf(MonAppContext.context.getString(R.string.fin_de_journee));
                break;
            case 5:
                lieuDePointage = String.valueOf(MonAppContext.context.getString(R.string.a_controler));
                break;
            default:
                lieuDePointage = String.valueOf(MonAppContext.context.getString(R.string.entreprise));
                break;
        }

        return user + separateur +
                dateTransformee + separateur +
                etatPointage + separateur +
                lieuDePointage + separateur +
                commentaires + separateur +
                longitude + separateur +
                latitude + separateur +
                miseALaLigne;
    }
}


