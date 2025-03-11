package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, productDescription;
    private Button addToCartButton;
    private FirebaseFirestore db;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get product ID from Intent
        productId = getIntent().getStringExtra("productId");

        // Initialize UI elements
        productImage = findViewById(R.id.imageViewProduct);
        productName = findViewById(R.id.textViewProductName);
        productPrice = findViewById(R.id.textViewProductPrice);
        productDescription = findViewById(R.id.textViewProductDescription);
        addToCartButton = findViewById(R.id.buttonAddToCart);

        // Load product details
        loadProductDetails();

        // Add to cart button click
        addToCartButton.setOnClickListener(v -> addToCart());
    }

    private void loadProductDetails() {
        if (productId == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DocumentReference productRef = db.collection("products").document(productId);
        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String description = documentSnapshot.getString("description");
                Double price = documentSnapshot.getDouble("price");
                String imageUrl = documentSnapshot.getString("imageUrl");

                productName.setText(name);
                productDescription.setText(description);
                productPrice.setText("₹" + price);

                // Load image using Picasso
                Picasso.get().load(imageUrl).into(productImage);
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load product", Toast.LENGTH_SHORT).show()
        );
    }

    private void addToCart() {
        if (productId == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference productRef = db.collection("products").document(productId);

        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                Double price = documentSnapshot.getDouble("price");
                String imageUrl = documentSnapshot.getString("imageUrl");

                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("productId", productId);
                cartItem.put("name", name);
                cartItem.put("price", price);
                cartItem.put("imageUrl", imageUrl);
                cartItem.put("quantity", 1);

                // Fixing the Firestore Path
                db.collection("carts").document(userId).collection("items").document(productId)
                        .set(cartItem)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
                            openCartFragment();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                        );
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error retrieving product", Toast.LENGTH_SHORT).show()
        );
    }


    // Function to open Fragment_CartStudent
    private void openCartFragment() {
        Fragment_CartStudent cartFragment = new Fragment_CartStudent();

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, cartFragment)  // Replaces current content with the fragment
                .addToBackStack(null)  // Allows going back to previous activity
                .commit();
    }



}
