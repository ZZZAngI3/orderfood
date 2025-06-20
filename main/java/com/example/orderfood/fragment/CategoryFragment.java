package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderfood.R;
import com.example.orderfood.activity.DishDetailActivity;
import com.example.orderfood.adapter.CategoryAdapter;
import com.example.orderfood.adapter.DishAdapter;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.viewmodel.DishViewModel;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CategoryFragment extends Fragment implements DishAdapter.OnDishClickListener {

    private RecyclerView rvCategoryTabs, rvDishes;
    private CategoryAdapter categoryAdapter;
    private DishAdapter dishAdapter;
    private TextView tvCategoryName;
    private DishViewModel dishViewModel;

    private List<String> categoryList = new ArrayList<>();
    private String currentCategory = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        rvCategoryTabs = view.findViewById(R.id.rv_category_tabs);
        rvDishes = view.findViewById(R.id.rv_dishes);
        tvCategoryName = view.findViewById(R.id.tv_category_name);

        // 初始化菜品列表
        dishAdapter = new DishAdapter(requireContext(), new ArrayList<>(), this);
        rvDishes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDishes.setAdapter(dishAdapter);

        // 初始化分类标签
        rvCategoryTabs.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList, (category, position) -> {
            currentCategory = category;
            tvCategoryName.setText(category);
            loadDishesByCategory(category);
            categoryAdapter.setSelectedPosition(position);
        });
        rvCategoryTabs.setAdapter(categoryAdapter);

        // 获取所有菜品的分类
        dishViewModel = new ViewModelProvider(requireActivity()).get(DishViewModel.class);
        dishViewModel.getAllDishes().observe(getViewLifecycleOwner(), new Observer<List<Dish>>() {
            @Override
            public void onChanged(List<Dish> dishes) {
                Set<String> set = new LinkedHashSet<>();
                for (Dish dish : dishes) {
                    set.add(dish.getCategory());
                }
                categoryList.clear();
                categoryList.addAll(set);
                categoryAdapter.notifyDataSetChanged();

                // 默认选中第一个分类
                if (!categoryList.isEmpty()) {
                    if (currentCategory.isEmpty()) {
                        currentCategory = categoryList.get(0);
                        tvCategoryName.setText(currentCategory);
                        loadDishesByCategory(currentCategory);
                        categoryAdapter.setSelectedPosition(0);
                    }
                }
            }
        });

        return view;
    }

    /** 加载某分类下的菜品 */
    private void loadDishesByCategory(String category) {
        dishViewModel.getDishesByCategory(category).observe(getViewLifecycleOwner(), new Observer<List<Dish>>() {
            @Override
            public void onChanged(List<Dish> dishes) {
                dishAdapter.setData(dishes);
            }
        });
    }

    /** 点击菜品，跳转到详情 */
    @Override
    public void onDishClick(Dish dish) {
        Intent intent = new Intent(requireContext(), DishDetailActivity.class);
        intent.putExtra("dish", dish);
        startActivity(intent);
    }

    /** 点击加购物车（可选实现） */
    @Override
    public void onAddToCartClick(Dish dish) {
        // 可实现弹窗、或跳转购物车等
    }
}
