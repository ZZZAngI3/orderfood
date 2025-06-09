package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.entity.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onItemCheckedChanged(CartItem cartItem, boolean isChecked);
        void onQuantityChanged(CartItem cartItem);
        void onDeleteItem(CartItem cartItem);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.checkBox.setChecked(cartItem.isSelected());
        holder.dishName.setText(cartItem.getDishName());
        holder.dishPrice.setText("¥" + String.format("%.2f", cartItem.getDishPrice()));
        holder.quantity.setText(String.valueOf(cartItem.getCount()));
        holder.subtotal.setText("¥" + String.format("%.2f", cartItem.getDishPrice() * cartItem.getCount()));

        Glide.with(context)
                .load(cartItem.getDishImageUrl())
                .placeholder(R.drawable.ic_food_placeholder)
                .into(holder.dishImage);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked);
            if (listener != null) {
                listener.onItemCheckedChanged(cartItem, isChecked);
            }
        });

        holder.minusBtn.setOnClickListener(v -> {
            int count = cartItem.getCount();
            if (count > 1) {
                cartItem.setCount(count - 1);
                holder.quantity.setText(String.valueOf(count - 1));
                holder.subtotal.setText("¥" + String.format("%.2f", cartItem.getDishPrice() * (count - 1)));
                if (listener != null) {
                    listener.onQuantityChanged(cartItem);
                }
            }
        });

        holder.plusBtn.setOnClickListener(v -> {
            int count = cartItem.getCount();
            cartItem.setCount(count + 1);
            holder.quantity.setText(String.valueOf(count + 1));
            holder.subtotal.setText("¥" + String.format("%.2f", cartItem.getDishPrice() * (count + 1)));
            if (listener != null) {
                listener.onQuantityChanged(cartItem);
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteItem(cartItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void setData(List<CartItem> newData) {
        this.cartItems = newData;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView dishImage, minusBtn, plusBtn, deleteBtn;
        TextView dishName, dishPrice, quantity, subtotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            dishImage = itemView.findViewById(R.id.dish_image);
            dishName = itemView.findViewById(R.id.dish_name);
            dishPrice = itemView.findViewById(R.id.dish_price);
            minusBtn = itemView.findViewById(R.id.minus_btn);
            plusBtn = itemView.findViewById(R.id.plus_btn);
            quantity = itemView.findViewById(R.id.quantity);
            subtotal = itemView.findViewById(R.id.subtotal);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
    }
}
