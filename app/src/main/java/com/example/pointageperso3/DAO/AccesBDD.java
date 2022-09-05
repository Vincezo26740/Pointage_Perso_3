package com.example.pointageperso3.DAO;

import androidx.room.Room;

import com.example.pointageperso3.function.MonAppContext;

public class AccesBDD {
    public static PersoDatabase getConnexionBDD(){
       return Room.databaseBuilder(MonAppContext.context, PersoDatabase.class,"connexionBDD").fallbackToDestructiveMigrationOnDowngrade().build();
    }
}
