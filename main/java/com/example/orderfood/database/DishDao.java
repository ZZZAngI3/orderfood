package com.example.orderfood.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.orderfood.entity.Dish;
import java.util.List;

@Dao
public interface DishDao {
    @Query("SELECT * FROM dish")
    List<Dish> getAllDishes();

    @Query("SELECT * FROM dish WHERE category = :category")
    List<Dish> getDishesByCategory(String category);

    @Query("SELECT * FROM dish WHERE id = :id")
    Dish getDishById(int id);

    @Insert
    void insertDishes(List<Dish> dishes);

    @Update
    void updateDish(Dish dish); // 如果需要更新 stock 字段，可以添加更新方法
}
