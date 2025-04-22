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
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.setProductDetails(product);  // Fetch and prepare data
        holder.bind();                      // Bind data to UI
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

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewProduct);
            productName = itemView.findViewById(R.id.textViewProductName);
            productPrice = itemView.findViewById(R.id.textViewProductPrice);
        }

        // This method is responsible for preparing data
        void setProductDetails(Product product) {
            this.product = product;

            // Decode the image
            try {
                byte[] decodedBytes = Base64.decode(product.getImageUrl(), Base64.DEFAULT);
                decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            } catch (Exception e) {
                decodedBitmap = null;
            }
        }

        // This method binds data to the UI (CardView)
        void bind() {
            productName.setText(product.getName());
            productPrice.setText("Rs. " + product.getPrice());

            if (decodedBitmap != null) {
                productImage.setImageBitmap(decodedBitmap);
            } else {
                productImage.setImageResource(R.drawable.heartlogo); // fallback image
            }

            itemView.setOnClickListener(v -> listener.onProductClick(product));
        }
    }
}
