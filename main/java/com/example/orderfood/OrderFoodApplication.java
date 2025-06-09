package com.example.orderfood;

import android.app.Application;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.viewmodel.DishViewModel;
import java.util.ArrayList;
import java.util.List;

public class OrderFoodApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 清除登录session
        UserSession.clear(this);
        DishViewModel dishViewModel = new DishViewModel(this);
        List<Dish> dishes = createMockDishes();
        dishViewModel.insertDishes(dishes);
    }

    private List<Dish> createMockDishes() {
        List<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("宫保鸡丁", "经典川菜，鸡肉鲜嫩，花生酥脆", 38.0, "川菜", "https://picsum.photos/200/200?random=1", true, 120, 4.8, 50));
        dishes.add(new Dish("鱼香肉丝", "酸甜可口，下饭神器", 32.0, "川菜", "https://picsum.photos/200/200?random=2", false, 98, 4.7, 30));
        dishes.add(new Dish("麻婆豆腐", "麻辣鲜香，豆腐嫩滑", 22.0, "川菜", "https://picsum.photos/200/200?random=3", true, 156, 4.9, 40));
        dishes.add(new Dish("糖醋排骨", "酸甜开胃，肉质鲜嫩", 42.0, "粤菜", "https://picsum.photos/200/200?random=4", false, 87, 4.6, 20));
        dishes.add(new Dish("白切鸡", "皮黄肉白，肥嫩鲜美", 58.0, "粤菜", "https://picsum.photos/200/200?random=5", false, 76, 4.5, 15));
        dishes.add(new Dish("叉烧肉", "肉质软嫩多汁，色泽鲜明", 46.0, "粤菜", "https://picsum.photos/200/200?random=6", false, 65, 4.4, 25));
        dishes.add(new Dish("北京烤鸭", "肉质细嫩，肥而不腻", 168.0, "鲁菜", "https://picsum.photos/200/200?random=7", false, 45, 4.9, 10));
        dishes.add(new Dish("糖醋鲤鱼", "外焦内嫩，酸甜可口", 68.0, "鲁菜", "https://picsum.photos/200/200?random=8", false, 34, 4.3, 8));
        dishes.add(new Dish("葱烧海参", "葱香浓郁，软糯滑嫩", 98.0, "鲁菜", "https://picsum.photos/200/200?random=9", false, 23, 4.7, 12));
        return dishes;
    }
}
