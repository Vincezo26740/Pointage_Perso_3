package fr.LCB.pointageperso3.DAO;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import fr.LCB.pointageperso3.function.MonAppContext;

public class AccesBDD {
    public static PersoDatabase getConnexionBDD(){
       return Room.databaseBuilder(MonAppContext.context, PersoDatabase.class,"connexionBDD").build();
    }

    public static void close(){
        if (Room.inMemoryDatabaseBuilder(MonAppContext.context, PersoDatabase.class).build().isOpen()) {
            getConnexionBDD().close();
        }
    }
}
