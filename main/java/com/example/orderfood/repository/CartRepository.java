package com.example.orderfood.repository;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.MutableLiveData;
import com.example.orderfood.database.AppDatabase;
import com.example.orderfood.database.CartDao;
import com.example.orderfood.entity.CartItem;
import java.util.List;

public class CartRepository {
    private CartDao cartDao;
    private MutableLiveData<List<CartItem>> cartItemsLiveData;

    public CartRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        cartDao = database.cartDao();
        cartItemsLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<CartItem>> getCartItemsLiveData(int userId) {
        loadCartItems(userId);
        return cartItemsLiveData;
    }

    private void loadCartItems(int userId) {
        new AsyncTask<Void, Void, List<CartItem>>() {
            @Override
            protected List<CartItem> doInBackground(Void... voids) {
                return cartDao.getCartItemsByUserId(userId);
            }

            @Override
            protected void onPostExecute(List<CartItem> cartItems) {
                cartItemsLiveData.setValue(cartItems);
            }
        }.execute();
    }

    public void addToCart(CartItem cartItem, AddToCartCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                CartItem existingItem = cartDao.getCartItemByUserIdAndDishId(
                        cartItem.getUserId(), cartItem.getDishId());
                if (existingItem != null) {
                    existingItem.setCount(existingItem.getCount() + cartItem.getCount());
                    cartDao.updateCartItem(existingItem);
                } else {
                    cartDao.addToCart(cartItem);
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    loadCartItems(cartItem.getUserId());
                    callback.onSuccess();
                } else {
                    callback.onFailure("添加失败");
                }
            }
        }.execute();
    }

    public void updateCartItem(CartItem cartItem) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cartDao.updateCartItem(cartItem);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loadCartItems(cartItem.getUserId());
            }
        }.execute();
    }

    public void deleteCartItem(CartItem cartItem) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cartDao.deleteCartItem(cartItem);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loadCartItems(cartItem.getUserId());
            }
        }.execute();
    }

    public void clearCart(int userId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cartDao.clearCart(userId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loadCartItems(userId);
            }
        }.execute();
    }

    public interface AddToCartCallback {
        void onSuccess();
        void onFailure(String message);
    }
}