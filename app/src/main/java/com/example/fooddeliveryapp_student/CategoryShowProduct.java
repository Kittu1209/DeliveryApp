package com.example.fooddeliveryapp_student;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CategoryShowProduct extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryProductAdapter adapter;
    private List<CategoryProductModel> productList;
    private FirebaseFirestore firestore;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_show_product);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get selected category from Intent
        selectedCategory = getIntent().getStringExtra("name");

        // Setup RecyclerView
        recyclerView = findViewById(R.id.menuCategoryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new CategoryProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        // Fetch products by category
        fetchProductsByCategory();
    }

    private void fetchProductsByCategory() {
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("products")
                .whereEqualTo("category", selectedCategory) // Filter by category
                .whereEqualTo("available", true) // Optional: Only fetch available products
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Fetch product details
                        String productId = doc.getId(); // Get product ID
                        String name = doc.getString("name"); // Get product name
                        double price = doc.getDouble("price"); // Get product price
                        String base64Image = doc.getString("imageUrl"); // Get image URL (base64 encoded)

                        // Decode base64 image to Bitmap
                        Bitmap productImage = decodeBase64ToBitmap(base64Image);

                        // Convert Bitmap to Base64 String
                        String productImageString = encodeBitmapToBase64(productImage);

                        // Create CategoryProductModel object and add it to list
                        CategoryProductModel model = new CategoryProductModel(productId, name, price, productImageString);
                        productList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle failure case
                    Toast.makeText(CategoryShowProduct.this, "Error fetching products", Toast.LENGTH_SHORT).show();
                });
    }

    // Utility method to decode base64 to Bitmap
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if decoding fails
        }
    }

    // Utility method to convert Bitmap to Base64 String
    private String encodeBitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
