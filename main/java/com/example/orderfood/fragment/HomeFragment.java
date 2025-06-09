package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderfood.R;
import com.example.orderfood.activity.DishDetailActivity;
import com.example.orderfood.adapter.DishAdapter;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.viewmodel.DishViewModel;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements DishAdapter.OnDishClickListener {
    private RecyclerView rvPopularDishes, rvRecommendedDishes;
    private DishAdapter popularAdapter, recommendedAdapter;
    private DishViewModel dishViewModel;
    private List<Dish> allDishes = new ArrayList<>();
    private LinearLayout llCategory1, llCategory2, llCategory3;
    private TextView tvCategory1, tvCategory2, tvCategory3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setupViewModels();
        setupRecyclerViews();
        setupCategoryButtons();
        return view;
    }

    private void initViews(View view) {
        rvPopularDishes = view.findViewById(R.id.rv_popular_dishes);
        rvRecommendedDishes = view.findViewById(R.id.rv_recommended_dishes);
        llCategory1 = view.findViewById(R.id.ll_category_1);
        llCategory2 = view.findViewById(R.id.ll_category_2);
        llCategory3 = view.findViewById(R.id.ll_category_3);
        tvCategory1 = view.findViewById(R.id.tv_category_1);
        tvCategory2 = view.findViewById(R.id.tv_category_2);
        tvCategory3 = view.findViewById(R.id.tv_category_3);
    }

    private void setupViewModels() {
        dishViewModel = new ViewModelProvider(this).get(DishViewModel.class);
        dishViewModel.getAllDishes().observe(getViewLifecycleOwner(), new Observer<List<Dish>>() {
            @Override
            public void onChanged(List<Dish> dishes) {
                allDishes = dishes;
                updateAdapters();
                setupCategoryNames();
            }
        });
    }

    private void setupRecyclerViews() {
        popularAdapter = new DishAdapter(requireContext(), new ArrayList<>(), this);
        recommendedAdapter = new DishAdapter(requireContext(), new ArrayList<>(), this);

        rvPopularDishes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvRecommendedDishes.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        rvPopularDishes.setAdapter(popularAdapter);
        rvRecommendedDishes.setAdapter(recommendedAdapter);
    }

    private void setupCategoryButtons() {
        llCategory1.setOnClickListener(v -> navigateToCategory(tvCategory1.getText().toString()));
        llCategory2.setOnClickListener(v -> navigateToCategory(tvCategory2.getText().toString()));
        llCategory3.setOnClickListener(v -> navigateToCategory(tvCategory3.getText().toString()));
    }

    private void setupCategoryNames() {
        if (allDishes.isEmpty()) return;

        List<String> categories = new ArrayList<>();
        for (Dish dish : allDishes) {
            if (!categories.contains(dish.getCategory())) {
                categories.add(dish.getCategory());
            }
            if (categories.size() >= 3) break;
        }

        if (categories.size() > 0) tvCategory1.setText(categories.get(0));
        if (categories.size() > 1) tvCategory2.setText(categories.get(1));
        if (categories.size() > 2) tvCategory3.setText(categories.get(2));
    }

    private void updateAdapters() {
        // 按销量排序获取热门菜品
        List<Dish> popularDishes = new ArrayList<>(allDishes);
        popularDishes.sort((d1, d2) -> d2.getSales() - d1.getSales());
        if (popularDishes.size() > 4) {
            popularDishes = popularDishes.subList(0, 4);
        }
        popularAdapter.setData(popularDishes);

        // 按评分排序获取推荐菜品
        List<Dish> recommendedDishes = new ArrayList<>(allDishes);
        recommendedDishes.sort((d1, d2) -> Double.compare(d2.getRating(), d1.getRating()));
        if (recommendedDishes.size() > 4) {
            recommendedDishes = recommendedDishes.subList(0, 4);
        }
        recommendedAdapter.setData(recommendedDishes);
    }

    private void navigateToCategory(String category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDishClick(Dish dish) {
        Intent intent = new Intent(requireContext(), DishDetailActivity.class);
        intent.putExtra("dish", dish);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(Dish dish) {
        CartFragment cartFragment = new CartFragment();
        Bundle args = new Bundle();
        args.putSerializable("dish", dish);
        cartFragment.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cartFragment)
                .addToBackStack(null)
                .commit();
    }
}
