package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminUpdateCategoryAdapter extends RecyclerView.Adapter<AdminUpdateCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<AdminUpdateCategoryModel> categoryList;

    public AdminUpdateCategoryAdapter(Context context, List<AdminUpdateCategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_edit_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        AdminUpdateCategoryModel model = categoryList.get(position);

        // === Validations ===
        String name = model.getName();
        String description = model.getDescription();
        String imageBase64 = model.getImage();

        if (name == null || name.trim().isEmpty()) {
            holder.tvCategoryName.setText("No Name");
        } else {
            holder.tvCategoryName.setText(name);
        }

        if (description == null || description.trim().isEmpty()) {
            holder.tvCategoryDescription.setText("No Description");
        } else {
            holder.tvCategoryDescription.setText(description);
        }

        // Decode Base64 image safely
        if (imageBase64 != null && !imageBase64.trim().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.imgCategory.setImageBitmap(decodedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                holder.imgCategory.setImageResource(R.drawable.heartlogo); // fallback image if decode fails
            }
        } else {
            holder.imgCategory.setImageResource(R.drawable.heartlogo); // fallback for missing image
        }

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCategoryPage.class);
            intent.putExtra("categoryId", model.getId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("categories")
                    .document(model.getId())
                    .delete();
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvCategoryName, tvCategoryDescription;
        Button btnEdit, btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
