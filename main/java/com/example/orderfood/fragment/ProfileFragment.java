package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.orderfood.R;
import com.example.orderfood.activity.LoginActivity;
import com.example.orderfood.entity.User;
import com.example.orderfood.viewmodel.UserViewModel;
import com.example.orderfood.util.UserSession;

public class ProfileFragment extends Fragment {
    private TextView tvUsername, tvPhone, tvAddress;
    private Button btnLogout, btnLogin;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        setupViewModel();
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvAddress = view.findViewById(R.id.tv_address);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogin = view.findViewById(R.id.btn_login);
    }

    private void setupViewModel() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // 自动恢复用户
        if (userViewModel.getCurrentUser().getValue() == null) {
            userViewModel.restoreUserFromSession(requireContext());
        }

        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    // 已登录
                    updateUserInfo(user);
                    setLoginUI(false);
                } else {
                    // 未登录
                    setLoginUI(true);
                }
            }
        });
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.logout();
                UserSession.clear(requireContext());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到登录页面
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        });
    }

    private void updateUserInfo(User user) {
        tvUsername.setText(user.getUsername());
        tvPhone.setText(user.getPhone());
        tvAddress.setText(user.getAddress());
    }

    /**
     * 控制登录/未登录时UI显示
     * @param isNotLoggedIn 是否未登录
     */
    private void setLoginUI(boolean isNotLoggedIn) {
        if (isNotLoggedIn) {
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
            tvUsername.setVisibility(View.GONE);
            tvPhone.setVisibility(View.GONE);
            tvAddress.setVisibility(View.GONE);
        } else {
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.VISIBLE);
            tvPhone.setVisibility(View.VISIBLE);
            tvAddress.setVisibility(View.VISIBLE);
        }
    }
}
