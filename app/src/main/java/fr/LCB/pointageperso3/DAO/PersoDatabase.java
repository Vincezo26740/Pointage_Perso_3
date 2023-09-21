package fr.LCB.pointageperso3.DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import fr.LCB.pointageperso3.Entity.ConfigAppli;
import fr.LCB.pointageperso3.Entity.Lieu;
import fr.LCB.pointageperso3.Entity.Pointage;
import fr.LCB.pointageperso3.Entity.Societe;
import fr.LCB.pointageperso3.Entity.User;

@Database(entities = {ConfigAppli.class, Lieu.class, Societe.class, User.class, Pointage.class},
        version = 1, exportSchema = true)

public abstract class PersoDatabase extends RoomDatabase {
    public abstract DaoConfiAppli DaoConfigAppli();

    public abstract DaoUser DaoUser();

    public abstract DaoPointage DaoPointage();

    public abstract DaoLieu DaoLieu();

    public abstract DaoSociete DaoSociete();

}

