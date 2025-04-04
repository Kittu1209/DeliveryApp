package com.example.fooddeliveryapp_student;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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

        db = FirebaseFirestore.getInstance();
        productId = getIntent().getStringExtra("productId");

        productImage = findViewById(R.id.imageViewProduct);
        productName = findViewById(R.id.textViewProductName);
        productPrice = findViewById(R.id.textViewProductPrice);
        productDescription = findViewById(R.id.textViewProductDescription);
        addToCartButton = findViewById(R.id.buttonAddToCart);

        loadProductDetails();

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
                String imageBase64 = documentSnapshot.getString("imageUrl"); // Contains base64 string

                productName.setText(name);
                productDescription.setText(description);
                productPrice.setText("₹" + price);

                // Decode Base64 and display image
                if (imageBase64 != null && !imageBase64.isEmpty()) {
                    try {
                        byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        productImage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error decoding image", Toast.LENGTH_SHORT).show();
                    }
                }
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
                String imageBase64 = documentSnapshot.getString("imageUrl");
                String shopId = documentSnapshot.getString("shopId");

                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("productId", productId);
                cartItem.put("name", name);
                cartItem.put("price", price);
                cartItem.put("imageUrl", imageBase64); // storing Base64 again
                cartItem.put("shopId", shopId);
                cartItem.put("quantity", 1);
                cartItem.put("updatedAt", FieldValue.serverTimestamp());

                db.collection("carts").document(userId).collection("items").document(productId)
                        .set(cartItem)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show()
                        )
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
}
