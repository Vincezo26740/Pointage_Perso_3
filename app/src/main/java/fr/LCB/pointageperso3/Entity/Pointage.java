package fr.LCB.pointageperso3.Entity;

import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import fr.LCB.pointageperso3.DAO.AccesBDD;
import fr.LCB.pointageperso3.function.MonAppContext;
import fr.LCB.pointageperso3.R;

import java.util.Locale;

@Entity
public class Pointage {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int idUser, etatPointage, etatJournee, lieuDePointage, referenceLieuDePointage;
    private String nomUtilisateur, commentaires;
    private double latitude;
    private double longitude;
    private long dateDebut;
    private long dateFin;

    public Pointage(int idUser, String nomUtilisateur,
                    long dateDebut, String commentaires,
                    int etatPointage) {
        this.idUser = idUser;
        this.nomUtilisateur = nomUtilisateur;
        this.dateDebut = dateDebut;
        this.commentaires = commentaires;
        this.etatPointage = etatPointage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public long getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(long dateDebut) {
        this.dateDebut = dateDebut;
    }

    public long getDateFin() {
        return dateFin;
    }

    public void setDateFin(long dateFin) {
        this.dateFin = dateFin;
    }

    public int getLieuDePointage() {
        return lieuDePointage;
    }

    public void setLieuDePointage(int lieuDePointage) {
        this.lieuDePointage = lieuDePointage;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    public int getEtatPointage() {
        return etatPointage;
    }

    public void setEtatPointage(int etatPointage) {
        this.etatPointage = etatPointage;
    }

    public int getEtatJournee() {
        return etatJournee;
    }

    public void setEtatJournee(int etatJournee) {
        this.etatJournee = etatJournee;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getReferenceLieuDePointage() {
        return referenceLieuDePointage;
    }

    public void setReferenceLieuDePointage(int referenceLieuDePointage) {
        this.referenceLieuDePointage = referenceLieuDePointage;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat formatagedate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);

        StringBuilder pointageToString = new StringBuilder();
        String testNomLieu = AccesBDD.getConnexionBDD().DaoLieu().findNameLieu(this.getReferenceLieuDePointage());
        AccesBDD.getConnexionBDD().close();
        String dateDebutString = formatagedate.format(this.getDateDebut());
        String dateFinString;
        pointageToString.append("\n").append(this.id).append("\n");
        if (this.dateDebut > this.dateFin) {
            dateFinString = MonAppContext.context.getString(R.string.pas_fin_pointe);
        } else {
            dateFinString = formatagedate.format(this.getDateFin());
        }
        if (this.getEtatPointage() == 1 || this.getEtatPointage() == 2) {
            pointageToString.append(MonAppContext.context.getString(R.string.lieux_de_pointage));
            switch (this.getLieuDePointage()) {
                case 0:
                    pointageToString.append(MonAppContext.context.getString(R.string.entreprise));
                    break;
                case 1:
                    pointageToString.append(testNomLieu);
                    break;
                case 2:
                    break;
                case 3:
                    pointageToString.append(MonAppContext.context.getString(R.string.en_pause));
                    break;
                case 4:
                    pointageToString.append(MonAppContext.context.getString(R.string.fin_de_journee));
                    break;
                case 5:
                    pointageToString.append(MonAppContext.context.getString(R.string.autres_raisons));
                    pointageToString.append("\n");
                    break;
            }
            pointageToString.append("\n").append(MonAppContext.context.getString(R.string.debut)).append(" ").append(dateDebutString).append("\n");
            pointageToString.append(MonAppContext.context.getString(R.string.fin)).append(" ").append(dateFinString).append("\n");
        } else if (this.getEtatPointage() == 3) {
            pointageToString.append(MonAppContext.context.getString(R.string.maladie_ou_congee)).append("\n");
        } else {
            pointageToString.append(MonAppContext.context.getString(R.string.a_controler)).append("\n");
        }
        if (!this.getCommentaires().isEmpty()) {
            pointageToString.append(MonAppContext.context.getString(R.string.commentaires)).append("\n");
            pointageToString.append(this.getCommentaires()).append("\n");
        }
        pointageToString.append("---").append("\n");
//        Log.i(MonAppContext.context.getString(R.string.TAG), pointageToString.toString());
        return pointageToString.toString();
    }

    @NonNull
    public String toStringForCsv(String separateur) {
        SimpleDateFormat formatagedate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);

        StringBuilder pointageToString = new StringBuilder();
        String testNomLieu = "";
        String dateDebutString = formatagedate.format(this.getDateDebut());
        String dateFinString;

        if (this.dateDebut > this.dateFin) {
            dateFinString = MonAppContext.context.getString(R.string.pas_fin_pointe);
        } else {
            dateFinString = formatagedate.format(this.getDateFin());
        }

        if (this.getEtatPointage() == 1 || this.getEtatPointage() == 2) {
            switch (this.getLieuDePointage()) {
                case 0:
                    testNomLieu = MonAppContext.context.getString(R.string.entreprise);
                    break;
                case 1:
                    testNomLieu = AccesBDD.getConnexionBDD().DaoLieu().findNameLieu(this.getReferenceLieuDePointage());
                    AccesBDD.getConnexionBDD().close();
                    break;
                case 2:
                    break;
                case 3:
                    testNomLieu = MonAppContext.context.getString(R.string.en_pause);
                    break;
                case 4:
                    testNomLieu = MonAppContext.context.getString(R.string.fin_de_journee);
                    break;
                case 5:
                    testNomLieu = MonAppContext.context.getString(R.string.autres_raisons);
                    break;
            }
        } else if (this.getEtatPointage() == 3) {
            testNomLieu = MonAppContext.context.getString(R.string.maladie_ou_congee);
        } else {
            testNomLieu = MonAppContext.context.getString(R.string.a_controler);
        }

        pointageToString.append(this.getNomUtilisateur()).append(separateur);
        pointageToString.append(testNomLieu).append(separateur);
        pointageToString.append("\"").append(this.getCommentaires()).append("\"").append(separateur);
        pointageToString.append(dateDebutString).append(separateur);
        pointageToString.append(dateFinString).append(separateur);
        pointageToString.append(this.getLongitude()).append(separateur);
        pointageToString.append(this.getLatitude()).append(separateur);
//        Log.i(MonAppContext.context.getString(R.string.TAG), pointageToString.toString());
        return pointageToString.toString();
    }
}
