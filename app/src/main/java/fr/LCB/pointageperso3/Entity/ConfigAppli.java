package fr.LCB.pointageperso3.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ConfigAppli {
    private boolean parametrerUser = false;
    private boolean firstUse = true;
    private double dateDernierEnvoi;

    @PrimaryKey
    private int primaryKey;

    public ConfigAppli() {}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Boolean getParametrerUser() {
        return parametrerUser;
    }

    public void setParametrerUser(Boolean parametrerUser) {
        this.parametrerUser = parametrerUser;
    }

    public Boolean getFirstUse() {
        return firstUse;
    }

    public void setFirstUse(Boolean firstUse) {
        this.firstUse = firstUse;
    }

    public double getDateDernierEnvoi() {
        return dateDernierEnvoi;
    }

    public void setDateDernierEnvoi(double dateDernierEnvoi) {
        this.dateDernierEnvoi = dateDernierEnvoi;
    }
}
