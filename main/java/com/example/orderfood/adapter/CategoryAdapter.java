package com.example.orderfood.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderfood.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<String> categoryList;
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;
    private Context context;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category, int position);
    }

    public CategoryAdapter(Context context, List<String> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categoryList.get(position);
        holder.tvCategory.setText(category);
        // 选中项高亮
        if (selectedPosition == position) {
            holder.tvCategory.setBackgroundResource(R.drawable.bg_category_selected);
            holder.tvCategory.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.tvCategory.setBackgroundResource(R.drawable.bg_category_normal);
            holder.tvCategory.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        holder.tvCategory.setOnClickListener(v -> {
            int old = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(old);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onCategoryClick(category, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setSelectedPosition(int pos) {
        int old = selectedPosition;
        selectedPosition = pos;
        notifyItemChanged(old);
        notifyItemChanged(selectedPosition);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category_item);
        }
    }
}
