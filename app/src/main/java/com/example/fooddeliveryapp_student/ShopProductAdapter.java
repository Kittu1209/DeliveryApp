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

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private List<Product> productList;
    private OnProductClickListener listener;

    public ShopProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public void updateList(List<Product> updatedList) {
        this.productList = updatedList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.setProductDetails(product);
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;
        Product product;
        Bitmap decodedBitmap;
        Context context;

        public ProductViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            productImage = itemView.findViewById(R.id.imageViewProduct);
            productName = itemView.findViewById(R.id.textViewProductName);
            productPrice = itemView.findViewById(R.id.textViewProductPrice);
        }

        void setProductDetails(Product product) {
            this.product = product;
            String imageUrl = product.getImageUrl();

            if (imageUrl != null) {
                if (imageUrl.startsWith("https://")) {
                    // Firebase Storage URL
                    decodedBitmap = null; // Handled via Glide in bind()
                } else if (imageUrl.startsWith("/9j/")) {
                    // Base64 encoded
                    try {
                        byte[] decodedBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                        decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    } catch (Exception e) {
                        decodedBitmap = null;
                    }
                } else {
                    // Treat as drawable name (e.g., "heartlogo.png")
                    decodedBitmap = null; // Handled via resource ID in bind()
                }
            } else {
                decodedBitmap = null;
            }
        }

        void bind() {
            productName.setText(product.getName());
            productPrice.setText("Rs. " + product.getPrice());
            String imageUrl = product.getImageUrl();

            if (decodedBitmap != null) {
                productImage.setImageBitmap(decodedBitmap);
            } else if (imageUrl != null && imageUrl.startsWith("https://")) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.heartlogo)
                        .into(productImage);
            } else if (imageUrl != null) {
                int resId = context.getResources().getIdentifier(
                        imageUrl.replace(".png", ""), "drawable", context.getPackageName());
                if (resId != 0) {
                    productImage.setImageResource(resId);
                } else {
                    productImage.setImageResource(R.drawable.heartlogo);
                }
            } else {
                productImage.setImageResource(R.drawable.heartlogo);
            }

            itemView.setOnClickListener(v -> listener.onProductClick(product));
        }
    }
}
