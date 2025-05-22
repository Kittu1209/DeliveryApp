package com.example.fooddeliveryapp_student;

import android.content.Context;
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

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    private final Context context;
    private final List<AdminProductModel> productList;

    public AdminProductAdapter(Context context, List<AdminProductModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public AdminProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductAdapter.ViewHolder holder, int position) {
        AdminProductModel product = productList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText("₹" + product.getPrice());
        holder.category.setText(product.getCategory());

        String imageUrl = product.getImageUrl();

        if (imageUrl != null && imageUrl.startsWith("/9j/")) {
            // Base64-encoded image
            try {
                byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.image.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.image.setImageResource(R.drawable.heartlogo);
                e.printStackTrace();
            }
        } else if (imageUrl != null && imageUrl.startsWith("https://")) {
            // Firebase Storage URL - use Glide
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.heartlogo)
                    .error(R.drawable.heartlogo)
                    .into(holder.image);
        } else {
            // Fallback image
            holder.image.setImageResource(R.drawable.heartlogo);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, category;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adminProductName);
            price = itemView.findViewById(R.id.adminProductPrice);
            category = itemView.findViewById(R.id.adminProductCategory);
            image = itemView.findViewById(R.id.adminProductImage);
        }
    }
}
