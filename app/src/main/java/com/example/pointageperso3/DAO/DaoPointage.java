package com.example.pointageperso3.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pointageperso3.Entity.Pointage;

import java.util.ArrayList;
import java.util.List;

@Dao
public  interface DaoPointage {

    @Query("SELECT * FROM Pointage ORDER BY id DESC")
    List<Pointage> findAll();

    @Query("SELECT * FROM Pointage ORDER BY id DESC LIMIT :limit")
    List<Pointage> findAllLimit(int limit);

    @Query("SELECT * FROM Pointage ORDER BY id DESC LIMIT 1")
    Pointage findLast();

    @Query("SELECT * FROM Pointage WHERE id LIKE :id")
    Pointage findById(int id);

    @Query("SELECT * FROM Pointage WHERE idUser LIKE :id ORDER BY dateDebut DESC LIMIT :limit" )
    List<Pointage> findListById(int id,int limit);

    @Query("SELECT * FROM Pointage WHERE idUser LIKE :id")
    List<Pointage> findListByIdNoLimit(int id);

    @Query("SELECT * FROM Pointage WHERE dateDebut BETWEEN :date1 AND :date2")
    List<Pointage> findByDate(long date1, long date2);

    @Query("SELECT * FROM Pointage WHERE etatPointage LIKE :etatPointage")
    List<Pointage> findByEtatChantier(int etatPointage);

    @Insert
    void insertPointageEntity(Pointage... entity);

    @Insert
    void insertPointageEntityTableau(ArrayList<Pointage> pointages);

    @Delete
    void deletePointageEntity(Pointage... entity);

    @Query("DELETE FROM Pointage")
    void truncateSansRAZId();

    @Update
    void update(Pointage dernierEnregistrement);
}
