package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.entity.Dish;
import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    private Context context;
    private List<Dish> dishList;
    private OnDishClickListener listener;

    public interface OnDishClickListener {
        void onDishClick(Dish dish);
        void onAddToCartClick(Dish dish);
    }

    public DishAdapter(Context context, List<Dish> dishList, OnDishClickListener listener) {
        this.context = context;
        this.dishList = dishList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dish, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.dishName.setText(dish.getName());
        holder.dishPrice.setText("¥" + String.format("%.2f", dish.getPrice()));
        holder.dishDescription.setText(dish.getDescription());
        holder.dishSales.setText("销量: " + dish.getSales());
        holder.dishRating.setText("评分: " + dish.getRating());

        if (dish.isSpicy()) {
            holder.dishSpicy.setVisibility(View.VISIBLE);
        } else {
            holder.dishSpicy.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(dish.getImageUrl())
                .placeholder(R.drawable.ic_food_placeholder)
                .into(holder.dishImage);

        holder.itemView.setOnClickListener(v -> listener.onDishClick(dish));
        holder.addToCartBtn.setOnClickListener(v -> listener.onAddToCartClick(dish));
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public void setData(List<Dish> newData) {
        this.dishList = newData;
        notifyDataSetChanged();
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImage, dishSpicy, addToCartBtn;
        TextView dishName, dishPrice, dishDescription, dishSales, dishRating;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            dishImage = itemView.findViewById(R.id.dish_image);
            dishSpicy = itemView.findViewById(R.id.dish_spicy);
            dishName = itemView.findViewById(R.id.dish_name);
            dishPrice = itemView.findViewById(R.id.dish_price);
            dishDescription = itemView.findViewById(R.id.dish_description);
            dishSales = itemView.findViewById(R.id.dish_sales);
            dishRating = itemView.findViewById(R.id.dish_rating);
            addToCartBtn = itemView.findViewById(R.id.add_to_cart_btn);
        }
    }
}
