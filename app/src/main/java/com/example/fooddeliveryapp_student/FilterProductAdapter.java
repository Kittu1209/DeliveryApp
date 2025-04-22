package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilterProductAdapter extends RecyclerView.Adapter<FilterProductAdapter.ProductViewHolder> {

    private List<FilterProductModel> productList;

    public FilterProductAdapter(List<FilterProductModel> productList) {
        this.productList = productList;
    }

    public void updateList(List<FilterProductModel> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        FilterProductModel product = productList.get(position);
        holder.bind(product);

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, ProductDetailActivity.class);

            // Pass product details to the ProductDetailActivity
            intent.putExtra("productName", product.getName());
            intent.putExtra("productPrice", product.getPrice());
            intent.putExtra("productImageBase64", product.getImageUrl());
            intent.putExtra("productId",product.getProductId());

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewProduct);
            productName = itemView.findViewById(R.id.textViewProductName);
            productPrice = itemView.findViewById(R.id.textViewProductPrice);
        }

        void bind(FilterProductModel product) {
            productName.setText(product.getName());
            productPrice.setText("₹" + product.getPrice());

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Bitmap bitmap = decodeBase64ToBitmap(product.getImageUrl());
                if (bitmap != null) {
                    productImage.setImageBitmap(bitmap);
                } else {
                    Log.e("ProductAdapter", "Image decoding failed for: " + product.getName());
                    productImage.setImageResource(R.drawable.heartlogo); // fallback image
                }
            } else {
                productImage.setImageResource(R.drawable.heartlogo); // fallback if no image
            }
        }


        private Bitmap decodeBase64ToBitmap(String base64Str) {
            try {
                // Remove data URI prefix if present
                if (base64Str.startsWith("data:image")) {
                    base64Str = base64Str.substring(base64Str.indexOf(",") + 1);
                }

                byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
