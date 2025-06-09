package com.example.orderfood.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.orderfood.entity.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    User login(String username, String password);

    @Query("SELECT * FROM user WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM user WHERE id = :userId")
    User getUserById(int userId);

    @Insert
    void register(User user);
}
