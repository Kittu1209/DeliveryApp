package com.example.fooddeliveryapp_student;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private FirebaseFirestore db;
    private TextView totalPriceText;

    public CartAdapter(List<CartItem> cartItems, FirebaseFirestore db, TextView totalPriceText) {
        this.cartItems = cartItems;
        this.db = db;
        this.totalPriceText = totalPriceText;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.productName.setText(item.getName());
        holder.productPrice.setText("₹" + item.getPrice());
        holder.productQuantity.setText(String.valueOf(item.getQuantity()));

        // Load image from URL without using Glide
        new ImageLoader(holder.productImage).execute(item.getImageUrl());

        // Handle quantity increase
        holder.increaseQuantity.setOnClickListener(v -> updateQuantity(item, 1));

        // Handle quantity decrease
        holder.decreaseQuantity.setOnClickListener(v -> updateQuantity(item, -1));

        // Handle product removal
        holder.removeItem.setOnClickListener(v -> removeItem(item));
    }

    private void updateQuantity(CartItem item, int change) {
        int newQuantity = item.getQuantity() + change;
        if (newQuantity > 0) {
            item.setQuantity(newQuantity);
            updateFirestore(item);
        }
    }

    private void updateFirestore(CartItem item) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference itemRef = db.collection("Carts").document(userId).collection("Items").document(item.getId());
        itemRef.update("quantity", item.getQuantity()).addOnSuccessListener(aVoid -> updateTotalPrice());
    }

    private void removeItem(CartItem item) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Carts").document(userId).collection("Items").document(item.getId())
                .delete().addOnSuccessListener(aVoid -> {
                    cartItems.remove(item);
                    notifyDataSetChanged();
                    updateTotalPrice();
                });
    }

    private void updateTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText("Total: ₹" + total);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage;
        Button increaseQuantity, decreaseQuantity, removeItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textViewCartProductName);
            productPrice = itemView.findViewById(R.id.textViewCartProductPrice);
            productQuantity = itemView.findViewById(R.id.textViewCartProductQuantity);
            productImage = itemView.findViewById(R.id.imageViewCartProduct);
            increaseQuantity = itemView.findViewById(R.id.buttonIncreaseQuantity);
            decreaseQuantity = itemView.findViewById(R.id.buttonDecreaseQuantity);
            removeItem = itemView.findViewById(R.id.buttonRemoveItem);
        }
    }

    // AsyncTask to load image without Glide
    private static class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public ImageLoader(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}
