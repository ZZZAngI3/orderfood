package com.example.orderfood.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.viewmodel.CartViewModel;
import com.example.orderfood.viewmodel.UserViewModel;
import com.example.orderfood.util.UserSession;

public class DishDetailActivity extends AppCompatActivity {
    private ImageView ivDishImage, ivMinus, ivPlus;
    private TextView tvDishName, tvDishPrice, tvDishCategory, tvDishSpicy, tvDishSales, tvDishRating, tvDishDescription, tvQuantity;
    private Button btnAddToCart;
    private Dish dish;
    private int quantity = 1;
    private CartViewModel cartViewModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initViews();
        setupListeners();
        getDishFromIntent();
        updateUI();
    }

    private void initViews() {
        ivDishImage = findViewById(R.id.iv_dish_image);
        ivMinus = findViewById(R.id.iv_minus);
        ivPlus = findViewById(R.id.iv_plus);
        tvDishName = findViewById(R.id.tv_dish_name);
        tvDishPrice = findViewById(R.id.tv_dish_price);
        tvDishCategory = findViewById(R.id.tv_dish_category);
        tvDishSpicy = findViewById(R.id.tv_dish_spicy);
        tvDishSales = findViewById(R.id.tv_dish_sales);
        tvDishRating = findViewById(R.id.tv_dish_rating);
        tvDishDescription = findViewById(R.id.tv_dish_description);
        tvQuantity = findViewById(R.id.tv_quantity);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
    }

    private void setupListeners() {
        ivMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        ivPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void getDishFromIntent() {
        dish = (Dish) getIntent().getSerializableExtra("dish");
    }

    private void updateUI() {
        if (dish != null) {
            Glide.with(this)
                    .load(dish.getImageUrl())
                    .placeholder(R.drawable.ic_food_placeholder)
                    .into(ivDishImage);
            tvDishName.setText(dish.getName());
            tvDishPrice.setText("¥" + String.format("%.2f", dish.getPrice()));
            tvDishCategory.setText(dish.getCategory());
            tvDishSpicy.setText(dish.isSpicy() ? "是" : "否");
            tvDishSales.setText("销量: " + dish.getSales());
            tvDishRating.setText("评分: " + dish.getRating());
            tvDishDescription.setText(dish.getDescription());
        }
    }

    private void addToCart() {
        int userId = UserSession.getUserId(this);
        if (userId != -1) {
            cartViewModel.initCart(userId); // 确保CartViewModel有正确userId
            cartViewModel.addToCart(dish, quantity, new CartViewModel.AddToCartCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(DishDetailActivity.this, "已添加到购物车", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onFailure(String message) {
                    Toast.makeText(DishDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(DishDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
        }
    }
}
