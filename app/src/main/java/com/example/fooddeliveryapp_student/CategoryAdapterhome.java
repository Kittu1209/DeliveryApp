package com.example.fooddeliveryapp_student;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapterhome extends RecyclerView.Adapter<CategoryAdapterhome.CategoryViewHolder> {
    private List<Category> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapterhome(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_home, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories != null ? newCategories : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryIcon;
        private final TextView categoryName;
        private final View selectionIndicator;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.iv_category_icon);
            categoryName = itemView.findViewById(R.id.tv_category_name);
            selectionIndicator = itemView.findViewById(R.id.view_selection_indicator);
        }

        public void bind(final Category category, final OnCategoryClickListener listener) {
            // Change getIconUrl() to getImageUrl() if you renamed it
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(category.getImageUrl(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    categoryIcon.setImageBitmap(decodedByte);
                } catch (IllegalArgumentException e) {
                    categoryIcon.setImageResource(R.drawable.heartlogo);
                }
            } else {
                categoryIcon.setImageResource(R.drawable.heartlogo);
            }

            categoryName.setText(category.getName());
            selectionIndicator.setVisibility(category.isSelected() ? View.VISIBLE : View.INVISIBLE);
            itemView.setOnClickListener(v -> listener.onCategoryClick(category));
        }
    }}