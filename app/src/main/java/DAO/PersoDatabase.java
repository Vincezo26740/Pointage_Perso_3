package DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;



import Entity.ConfigAppli;
import Entity.Lieu;
import Entity.Pointage;
import Entity.Societe;
import Entity.User;

@Database(entities = {ConfigAppli.class, Lieu.class, Societe.class, User.class, Pointage.class},
        version = 1, exportSchema = true)

public abstract class PersoDatabase extends RoomDatabase {
    public abstract DaoConfiAppli DaoConfigAppli();

    public abstract DaoUser DaoUser();

    public abstract DaoPointage DaoPointage();

    public abstract DaoLieu DaoLieu();

    public abstract DaoSociete DaoSociete();

}

