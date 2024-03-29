package fr.LCB.pointageperso3.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import fr.LCB.pointageperso3.Entity.Lieu;

import java.util.List;

@Dao
public interface DaoLieu {
    @Insert
    void insert(Lieu lieu);

    @Query("SELECT * FROM Lieu ")
    List<Lieu> findAll();

    @Query("SELECT name FROM Lieu WHERE id = :idLieu")
    String findNameLieu(int idLieu);

    @Delete
    void deletePointageEntity(Lieu... entity);

    @Query("DELETE FROM Lieu")
    void truncateSansRAZId();
}
