package DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import Entity.Lieu;

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
