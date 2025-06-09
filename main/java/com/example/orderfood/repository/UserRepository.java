package com.example.orderfood.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import com.example.orderfood.database.AppDatabase;
import com.example.orderfood.database.UserDao;
import com.example.orderfood.entity.User;
import java.lang.ref.WeakReference;

public class UserRepository {
    private UserDao userDao;
    private MutableLiveData<User> currentUser;

    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        currentUser = new MutableLiveData<>();
    }

    public void login(String username, String password, LoginCallback callback) {
        new LoginTask(this, callback).execute(username, password);
    }

    public void register(User user, RegisterCallback callback) {
        new RegisterTask(this, callback).execute(user);
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    // 新增：通过 userId 获取用户
    public interface GetUserCallback {
        void onUserLoaded(User user);
        void onDataNotAvailable();
    }

    public void getUserById(int userId, GetUserCallback callback) {
        new AsyncTask<Integer, Void, User>() {
            @Override
            protected User doInBackground(Integer... params) {
                return userDao.getUserById(params[0]);
            }

            @Override
            protected void onPostExecute(User user) {
                if (user != null) {
                    callback.onUserLoaded(user);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.execute(userId);
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    public interface RegisterCallback {
        void onSuccess();
        void onFailure(String message);
    }

    private static class LoginTask extends AsyncTask<String, Void, User> {
        private final WeakReference<UserRepository> repositoryReference;
        private final LoginCallback callback;

        LoginTask(UserRepository repository, LoginCallback callback) {
            this.repositoryReference = new WeakReference<>(repository);
            this.callback = callback;
        }

        @Override
        protected User doInBackground(String... params) {
            UserRepository repository = repositoryReference.get();
            if (repository != null) {
                String username = params[0];
                String password = params[1];
                User user = repository.userDao.login(username, password);
                Log.d("LoginTask", "Query result: " + user);
                return user;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            UserRepository repository = repositoryReference.get();
            if (repository != null) {
                if (user != null) {
                    repository.currentUser.setValue(user);
                    Log.d("LoginTask", "Current user updated: " + user);
                    callback.onSuccess(user);
                } else {
                    callback.onFailure("用户名或密码错误");
                }
            }
        }
    }

    private static class RegisterTask extends AsyncTask<User, Void, Boolean> {
        private final WeakReference<UserRepository> repositoryReference;
        private final RegisterCallback callback;

        RegisterTask(UserRepository repository, RegisterCallback callback) {
            this.repositoryReference = new WeakReference<>(repository);
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(User... users) {
            UserRepository repository = repositoryReference.get();
            if (repository != null) {
                User user = users[0];
                User existingUser = repository.userDao.getUserByUsername(user.getUsername());
                if (existingUser != null) {
                    return false;
                }
                repository.userDao.register(user);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            UserRepository repository = repositoryReference.get();
            if (repository != null) {
                if (success) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("用户名已存在");
                }
            }
        }
    }
}
