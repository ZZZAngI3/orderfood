package com.example.orderfood.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.repository.DishRepository;
import java.util.List;

public class DishViewModel extends AndroidViewModel {
    private DishRepository dishRepository;
    private LiveData<List<Dish>> allDishesLiveData;
    private LiveData<List<Dish>> categoryDishesLiveData;

    public DishViewModel(@NonNull Application application) {
        super(application);
        dishRepository = new DishRepository(application);
        allDishesLiveData = dishRepository.getAllDishes();
    }

    public LiveData<List<Dish>> getAllDishes() {
        return allDishesLiveData;
    }

    public LiveData<List<Dish>> getDishesByCategory(String category) {
        categoryDishesLiveData = dishRepository.getDishesByCategory(category);
        return categoryDishesLiveData;
    }

    public void insertDishes(List<Dish> dishes) {
        dishRepository.insertDishes(dishes);
    }
}
