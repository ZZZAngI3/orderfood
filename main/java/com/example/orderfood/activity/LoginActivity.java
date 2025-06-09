package com.example.orderfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.orderfood.entity.User;
import com.google.android.material.textfield.TextInputEditText;
import com.example.orderfood.R;
import com.example.orderfood.viewmodel.UserViewModel;
import com.example.orderfood.viewmodel.CartViewModel;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        // 使用 Application 作用域，确保和主界面一致
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> openRegisterActivity());
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        userViewModel.login(username, password, new UserViewModel.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                // 一定要同步CartViewModel的userId
                CartViewModel cartViewModel = new ViewModelProvider(LoginActivity.this).get(CartViewModel.class);
                cartViewModel.initCart(user.getId());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openRegisterActivity() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
