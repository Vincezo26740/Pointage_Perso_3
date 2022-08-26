package Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    public long getDateFin() { return dateFin;}

    public void setDateFin(long dateFin) { this.dateFin = dateFin;}

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

    @Override
    public String toString() {
        return "Pointage{" +
                "id=" + id +
                ",\n etatPointage=" + etatPointage +
                ",\n etatJournee=" + etatJournee +
                ",\n lieuDePointage=" + lieuDePointage +
                ",\n referenceLieuDePointage=" + referenceLieuDePointage +
                ",\n date=" + dateDebut +
                '}';
    }
}
