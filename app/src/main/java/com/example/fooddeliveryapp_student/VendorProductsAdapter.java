package com.example.fooddeliveryapp_student;

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

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.List;

public class VendorProductsAdapter extends RecyclerView.Adapter<VendorProductsAdapter.ProductViewHolder> {

    private final List<VendorProduct> productList;
    private final OnProductActionListener listener;

    public interface OnProductActionListener {
        void onToggleAvailability(VendorProduct product);
    }

    public VendorProductsAdapter(OnProductActionListener listener, List<VendorProduct> productList) {
        this.listener = listener;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vendor_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        VendorProduct product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.tvDescription.setText(product.getDescription());
        holder.tvStock.setText(String.format("Stock: %d", product.getStockQuantity()));

        // Set availability button text
        holder.btnToggleAvailability.setText(product.isAvailable() ? "In Stock" : "Out of Stock");

        // Check if imageUrl is a valid URL or a base64 string
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            if (isBase64(product.getImageUrl())) {
                // If base64, decode and display
                byte[] decodedString = Base64.decode(product.getImageUrl(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imgProduct.setImageBitmap(decodedBitmap);
            } else {
                // If it's a URL, load it using Glide
                Glide.with(holder.itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_image_placeholder)  // Placeholder if image is not available
                        .into(holder.imgProduct);
            }
        } else {
            // If no image URL is available, set the placeholder
            holder.imgProduct.setImageResource(R.drawable.heartlogo);
        }

        // Open EditProductActivity on Edit button click
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditProductActivity.class);
            intent.putExtra("product", (Serializable) product); // Ensure VendorProduct is Serializable
            holder.itemView.getContext().startActivity(intent);
        });

        holder.btnToggleAvailability.setOnClickListener(v -> listener.onToggleAvailability(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvDescription, tvStock;
        Button btnToggleAvailability, btnEdit;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStock = itemView.findViewById(R.id.tvStock);
            btnToggleAvailability = itemView.findViewById(R.id.btnToggleAvailability);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    // Helper function to check if the string is base64 encoded
    private boolean isBase64(String str) {
        try {
            // Try to decode it
            Base64.decode(str, Base64.DEFAULT);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
