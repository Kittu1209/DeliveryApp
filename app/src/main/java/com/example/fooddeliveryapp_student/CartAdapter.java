package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import com.example.fooddeliveryapp_student.CartItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private FirebaseFirestore db;
    private TextView totalPriceText;
    private Context context;

    public CartAdapter(Context context, List<CartItem> cartItems, FirebaseFirestore db, TextView totalPriceText) {
        this.context = context;
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

        // Load image from base64 string
        try {
            byte[] decodedString = Base64.decode(item.getImageUrl(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.productImage.setImageBitmap(decodedByte);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
        }

        holder.increaseQuantity.setOnClickListener(v -> {
            if (item.getQuantity() < 5) {
                updateQuantity(item, 1, position);
            } else {
                Toast.makeText(context, "You can only order up to 5 items.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.decreaseQuantity.setOnClickListener(v -> updateQuantity(item, -1, position));
        holder.removeItem.setOnClickListener(v -> removeItem(item, position));
    }

    private void updateQuantity(CartItem item, int change, int position) {
        int newQuantity = item.getQuantity() + change;
        if (newQuantity > 0 && newQuantity <= 5) {
            item.setQuantity(newQuantity);
            notifyItemChanged(position);
            updateFirestore(item);
        }
    }

    private void updateFirestore(CartItem item) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference itemRef = db.collection("carts").document(userId).collection("items").document(item.getDocumentId());

        itemRef.update("quantity", item.getQuantity()).addOnSuccessListener(aVoid -> updateTotalPrice());
    }

    private void removeItem(CartItem item, int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference itemRef = db.collection("carts").document(userId).collection("items").document(item.getDocumentId());

        itemRef.delete().addOnSuccessListener(aVoid -> {
            cartItems.remove(position);
            notifyItemRemoved(position);
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
}
