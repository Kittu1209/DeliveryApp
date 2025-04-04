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

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewCategory.setText(product.getCategory());
        holder.textViewPrice.setText("₹" + product.getPrice());
        holder.textViewDescription.setText(product.getDescription());

        // Decode Base64 image and set to ImageView
        try {
            String base64Image = product.getImageUrl(); // contains Base64 string
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.imageViewProduct.setImageBitmap(bitmap);
            } else {
                // Optional: Set a placeholder image
                holder.imageViewProduct.setImageResource(android.R.drawable.ic_menu_report_image);

            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.imageViewProduct.setImageResource(android.R.drawable.ic_menu_report_image);
//holder.imageViewProduct.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewCategory, textViewPrice, textViewDescription;
        ImageView imageViewProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewProductName);
            textViewCategory = itemView.findViewById(R.id.textViewProductCategory);
            textViewPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewDescription = itemView.findViewById(R.id.textViewProductDescription);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}
