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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderfood.R;
import com.example.orderfood.activity.DishDetailActivity;
import com.example.orderfood.adapter.DishAdapter;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.viewmodel.DishViewModel;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements DishAdapter.OnDishClickListener {
    private RecyclerView rvDishes;
    private DishAdapter dishAdapter;
    private DishViewModel dishViewModel;
    private TextView tvCategoryName;
    private String category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        loadData();
        return view;
    }

    private void initViews(View view) {
        rvDishes = view.findViewById(R.id.rv_dishes);
        tvCategoryName = view.findViewById(R.id.tv_category_name);
    }

    private void setupViewModel() {
        dishViewModel = new ViewModelProvider(this).get(DishViewModel.class);
    }

    private void setupRecyclerView() {
        dishAdapter = new DishAdapter(requireContext(), new ArrayList<>(), this);
        rvDishes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvDishes.setAdapter(dishAdapter);
    }

    private void loadData() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("category")) {
            category = args.getString("category");
            tvCategoryName.setText(category);

            dishViewModel.getDishesByCategory(category).observe(getViewLifecycleOwner(), new Observer<List<Dish>>() {
                @Override
                public void onChanged(List<Dish> dishes) {
                    dishAdapter.setData(dishes);
                }
            });
        }
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
