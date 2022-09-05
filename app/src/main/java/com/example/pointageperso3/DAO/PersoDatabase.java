package com.example.pointageperso3.DAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.pointageperso3.Entity.ConfigAppli;
import com.example.pointageperso3.Entity.Lieu;
import com.example.pointageperso3.Entity.Pointage;
import com.example.pointageperso3.Entity.Societe;
import com.example.pointageperso3.Entity.User;

@Database(entities = {ConfigAppli.class, Lieu.class, Societe.class, User.class, Pointage.class},
        version = 1, exportSchema = true)

public abstract class PersoDatabase extends RoomDatabase {
    public abstract DaoConfiAppli DaoConfigAppli();

    public abstract DaoUser DaoUser();

    public abstract DaoPointage DaoPointage();

    public abstract DaoLieu DaoLieu();

    public abstract DaoSociete DaoSociete();

}

