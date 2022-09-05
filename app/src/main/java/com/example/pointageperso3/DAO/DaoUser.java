package com.example.pointageperso3.DAO;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.pointageperso3.Entity.User;

@Dao
public interface DaoUser {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM User WHERE name LIKE :name OR firstName LIKE :name")
    @ColumnInfo
    User rechercheUser(String name);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM User")
    List<User> findAll();
}
