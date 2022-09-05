package com.example.pointageperso3.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pointageperso3.Entity.ConfigAppli;

@Dao
public interface DaoConfiAppli {

    @Query("Select * from ConfigAppli limit 1")
    ConfigAppli presenceConfig();

    @Query("SELECT firstUse FROM ConfigAppli")
    boolean appliConfigFirtUse();

    @Query("update ConfigAppli set firstUse = :firstUse")
    void majFirstUse(boolean firstUse);

    @Insert
    void insertConfig(ConfigAppli configInit);
}
