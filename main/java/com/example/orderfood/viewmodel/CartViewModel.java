package com.example.orderfood.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import com.example.orderfood.entity.CartItem;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.repository.CartRepository;
import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private CartRepository cartRepository;
    private MutableLiveData<List<CartItem>> cartItemsLiveData;
    private SavedStateHandle savedStateHandle;
    private static final String KEY_CURRENT_USER_ID = "currentUserId";

    public CartViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        cartRepository = new CartRepository(application);
        cartItemsLiveData = new MutableLiveData<>();
    }

    /**
     * 初始化购物车数据
     * @param userId 当前用户的 ID
     */
    public void initCart(int userId) {
        savedStateHandle.set(KEY_CURRENT_USER_ID, userId);
        Log.d("CartViewModel", "Current user ID set: " + userId);
        cartItemsLiveData = cartRepository.getCartItemsLiveData(userId);
    }

    /**
     * 获取购物车商品的 LiveData 对象
     * @return 购物车商品的 LiveData 对象
     */
    public MutableLiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }

    /**
     * 获取当前用户的 ID
     * @return 当前用户的 ID
     */
    private int getCurrentUserId() {
        int userId = savedStateHandle.get(KEY_CURRENT_USER_ID) != null ? savedStateHandle.get(KEY_CURRENT_USER_ID) : -1;
        Log.d("CartViewModel", "Current user ID retrieved: " + userId);
        return userId;
    }

    /**
     * 将菜品添加到购物车
     * @param dish 要添加的菜品
     * @param count 要添加的菜品数量
     * @param callback 添加结果的回调接口
     */
    public void addToCart(Dish dish, int count, AddToCartCallback callback) {
        // 检查用户是否登录
        int currentUserId = getCurrentUserId();
        Log.d("CartViewModel", "Current user ID: " + currentUserId);
        if (currentUserId == -1) {
            callback.onFailure("请先登录");
            return;
        }

        // 创建 CartItem 对象
        CartItem cartItem = new CartItem(
                currentUserId,
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                dish.getImageUrl(),
                count,
                true,
                dish.getStock()
        );

        // 将 CartViewModel.AddToCartCallback 转换为 CartRepository.AddToCartCallback
        CartRepository.AddToCartCallback repoCallback = new CartRepository.AddToCartCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(String message) {
                callback.onFailure(message);
            }
        };

        // 调用仓库的添加方法
        cartRepository.addToCart(cartItem, repoCallback);
    }

    /**
     * 更新购物车中的商品信息
     * @param cartItem 要更新的购物车商品
     */
    public void updateCartItem(CartItem cartItem) {
        cartRepository.updateCartItem(cartItem);
    }

    /**
     * 从购物车中删除指定商品
     * @param cartItem 要删除的购物车商品
     */
    public void deleteCartItem(CartItem cartItem) {
        cartRepository.deleteCartItem(cartItem);
    }

    /**
     * 清空当前用户的购物车
     */
    public void clearCart() {
        int currentUserId = getCurrentUserId();
        if (currentUserId != -1) {
            cartRepository.clearCart(currentUserId);
        }
    }

    /**
     * 计算购物车中已选中商品的总价
     * @return 已选中商品的总价
     */
    public double calculateTotalPrice() {
        double total = 0;
        List<CartItem> cartItems = cartItemsLiveData.getValue();
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    total += item.getDishPrice() * item.getCount();
                }
            }
        }
        return total;
    }

    /**
     * 获取购物车中已选中商品的总数量
     * @return 已选中商品的总数量
     */
    public int getSelectedItemCount() {
        int count = 0;
        List<CartItem> cartItems = cartItemsLiveData.getValue();
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    count += item.getCount();
                }
            }
        }
        return count;
    }

    /**
     * 添加商品到购物车的回调接口
     */
    public interface AddToCartCallback {
        void onSuccess();
        void onFailure(String message);
    }
}
