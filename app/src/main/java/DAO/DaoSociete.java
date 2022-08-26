package DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import Entity.Societe;

@Dao
public interface DaoSociete {
    @Insert
    void insert(Societe societe);

    @Query("Delete from Societe")
    void suppressionSociete();

    @Query("Select * from Societe limit 1")
    Societe findSociete();
}
