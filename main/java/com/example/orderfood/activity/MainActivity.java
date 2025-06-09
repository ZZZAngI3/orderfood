package com.example.orderfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.orderfood.R;
import com.example.orderfood.fragment.CartFragment;
import com.example.orderfood.fragment.CategoryFragment;
import com.example.orderfood.fragment.HomeFragment;
import com.example.orderfood.fragment.ProfileFragment;
import com.example.orderfood.viewmodel.UserViewModel;
import com.example.orderfood.viewmodel.CartViewModel;
import com.example.orderfood.util.UserSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private UserViewModel userViewModel;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // 只做购物车初始化，不跳转
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                UserSession.saveUserId(MainActivity.this, user.getId());
                cartViewModel.initCart(user.getId());
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = UserSession.getUserId(this);
        if (userId != -1) {
            cartViewModel.initCart(userId);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                    } else if (itemId == R.id.nav_category) {
                        selectedFragment = new CategoryFragment();
                    } else if (itemId == R.id.nav_cart) {
                        selectedFragment = new CartFragment();
                    } else if (itemId == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                        return true;
                    }
                    return false;
                }
            };
}
