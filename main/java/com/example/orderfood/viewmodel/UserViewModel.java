package com.example.orderfood.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.orderfood.entity.User;
import com.example.orderfood.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private MutableLiveData<User> currentUser;

    public UserViewModel(@NonNull Application application) {
        super(application);
        currentUser = new MutableLiveData<>();
        userRepository = new UserRepository(application);
    }

    public void login(String username, String password, LoginCallback callback) {
        userRepository.login(username, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.setValue(user);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(String message) {
                callback.onFailure(message);
            }
        });
    }

    public void register(String username, String password, String phone, String address, RegisterCallback callback) {
        User user = new User(username, password, phone, address, "https://picsum.photos/200/200?random=user");
        UserRepository.RegisterCallback repoCallback = new UserRepository.RegisterCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(String message) {
                callback.onFailure(message);
            }
        };
        userRepository.register(user, repoCallback);
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser.setValue(null);
    }

    // 添加 setCurrentUser 方法
    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    public interface RegisterCallback {
        void onSuccess();
        void onFailure(String message);
    }
}
