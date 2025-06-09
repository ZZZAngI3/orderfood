package com.example.orderfood.repository;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.orderfood.database.AppDatabase;
import com.example.orderfood.database.DishDao;
import com.example.orderfood.entity.Dish;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

public class DishRepository {
    private static final String TAG = "DishRepository";
    private DishDao dishDao;
    private MutableLiveData<List<Dish>> allDishesLiveData;
    private MutableLiveData<List<Dish>> categoryDishesLiveData;
    private ExecutorService executorService;

    public DishRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        dishDao = database.dishDao();
        allDishesLiveData = new MutableLiveData<>();
        categoryDishesLiveData = new MutableLiveData<>();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Dish>> getAllDishes() {
        executorService.execute(() -> {
            try {
                List<Dish> dishes = dishDao.getAllDishes();
                allDishesLiveData.postValue(dishes);
            } catch (Exception e) {
                Log.e(TAG, "Error getting all dishes: " + e.getMessage());
            }
        });
        return allDishesLiveData;
    }

    public LiveData<List<Dish>> getDishesByCategory(String category) {
        executorService.execute(() -> {
            try {
                List<Dish> dishes = dishDao.getDishesByCategory(category);
                categoryDishesLiveData.postValue(dishes);
            } catch (Exception e) {
                Log.e(TAG, "Error getting dishes by category: " + e.getMessage());
            }
        });
        return categoryDishesLiveData;
    }

    public void insertDishes(List<Dish> dishes) {
        executorService.execute(() -> {
            try {
                dishDao.insertDishes(dishes);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting dishes: " + e.getMessage());
            }
        });
    }
}
