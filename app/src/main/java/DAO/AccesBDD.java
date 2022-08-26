package DAO;

import androidx.room.Room;

import com.example.pointageperso3.MonAppContext;

public class AccesBDD {
    public static PersoDatabase getConnexionBDD(){
       return Room.databaseBuilder(MonAppContext.context, PersoDatabase.class,"connexionBDD").fallbackToDestructiveMigrationOnDowngrade().build();
    }
}
