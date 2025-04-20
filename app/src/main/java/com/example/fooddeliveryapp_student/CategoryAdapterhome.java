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
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapterhome extends RecyclerView.Adapter<CategoryAdapterhome.CategoryViewHolder> {
    private List<Category> categories;
    private OnCategoryClickListener listener;
    private static final String ALL_CATEGORY_ID = "all";
    private static final String DEFAULT_ALL_IMAGE_URL = "https://cdn-icons-png.flaticon.com/512/16955/16955062.png";

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapterhome(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories != null ? categories : new ArrayList<>();
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
        return categories.size();
    }

    public void updateCategories(List<Category> newCategories) {
        this.categories = new ArrayList<>(newCategories);
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
            categoryName.setText(category.getName());
            selectionIndicator.setVisibility(category.isSelected() ? View.VISIBLE : View.INVISIBLE);
            loadCategoryImage(category);
            itemView.setOnClickListener(v -> listener.onCategoryClick(category));
        }

        private void loadCategoryImage(Category category) {
            if (category.getId().equals(ALL_CATEGORY_ID)) {
                loadImageFromUrl(DEFAULT_ALL_IMAGE_URL);
            } else if (category.getImage() == null || category.getImage().isEmpty()) {
                setDefaultImage();
            } else if (isBase64(category.getImage())) {
                loadBase64Image(category.getImage());
            } else {
                loadImageFromUrl(category.getImage());
            }
        }

        private boolean isBase64(String str) {
            return str.startsWith("data:image") || str.startsWith("/9j/") || str.length() > 1000;
        }

        private void loadBase64Image(String base64Image) {
            try {
                String cleanBase64 = base64Image.replaceAll("data:image/[^;]+;base64,", "").trim();
                byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) {
                    categoryIcon.setImageBitmap(bitmap);
                } else {
                    setDefaultImage();
                }
            } catch (Exception e) {
                setDefaultImage();
            }
        }

        private void loadImageFromUrl(String imageUrl) {
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.heartlogo)
                    .error(R.drawable.heartlogo)
                    .into(categoryIcon);
        }

        private void setDefaultImage() {
            categoryIcon.setImageResource(R.drawable.heartlogo);
        }
    }
}