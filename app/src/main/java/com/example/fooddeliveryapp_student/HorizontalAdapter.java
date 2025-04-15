package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private Context context;
    private List<HorizontalModel> categoryList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnCategoryClickListener listener;

    // ✅ Interface for category click callback
    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }

    // ✅ Updated constructor with listener
    public HorizontalAdapter(Context context, List<HorizontalModel> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HorizontalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_horizontal_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HorizontalModel category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryDescription.setText(category.getDescription());

        // Highlight selected category
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_orange));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Handle image decoding
        if (category.getImageBase64() != null && !category.getImageBase64().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(category.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.categoryImage.setImageBitmap(decodedByte);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.categoryImage.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            }
        } else {
            holder.categoryImage.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }

        // Handle click
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoryClick(category.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, categoryDescription;
        ImageView categoryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.cat_name);
            categoryDescription = itemView.findViewById(R.id.cat_description);
            categoryImage = itemView.findViewById(R.id.cat_image);
        }
    }
}
