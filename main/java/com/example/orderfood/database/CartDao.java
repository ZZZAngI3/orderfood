package com.example.orderfood.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.orderfood.entity.CartItem;
import java.util.List;

@Dao
public interface CartDao {
    @Query("SELECT * FROM cart_item WHERE userId = :userId")
    List<CartItem> getCartItemsByUserId(int userId);

    @Query("SELECT * FROM cart_item WHERE userId = :userId AND dishId = :dishId")
    CartItem getCartItemByUserIdAndDishId(int userId, int dishId);

    @Insert
    void addToCart(CartItem cartItem);

    @Update
    void updateCartItem(CartItem cartItem);

    @Delete
    void deleteCartItem(CartItem cartItem);

    @Query("DELETE FROM cart_item WHERE userId = :userId")
    void clearCart(int userId);
}
