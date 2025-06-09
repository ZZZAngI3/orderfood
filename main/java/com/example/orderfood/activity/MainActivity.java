package com.example.orderfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.orderfood.R;
import com.example.orderfood.fragment.CartFragment;
import com.example.orderfood.fragment.CategoryFragment;
import com.example.orderfood.fragment.HomeFragment;
import com.example.orderfood.fragment.ProfileFragment;
import com.example.orderfood.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // 检查用户是否已登录
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                // 用户未登录，跳转到登录页面
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        // 修复：使用正确的资源ID (R.id.bottom_navigation)
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // 默认显示首页
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    // 修复：将switch-case替换为if-else以避免常量表达式错误
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