package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ShopProductActivity extends AppCompatActivity {
    private RecyclerView productsRecyclerView;
    private ProgressBar progressBar;
    private ShopProductAdapter productAdapter;
    private List<Product> allProducts = new ArrayList<>();
    private String selectedShopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product);

        // Get shopId from Intent
        selectedShopId = getIntent().getStringExtra("id");

        // Initialize views
        initViews();

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Setup RecyclerView
        setupRecyclerView();

        // Load products from Firestore
        loadProducts();
    }

    private void initViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        productAdapter = new ShopProductAdapter(new ArrayList<>(), product -> {
            // Handle product click to show details
            Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("shopId", selectedShopId) // Filter products by shopId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allProducts.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        String description = doc.getString("description");
                        double price = doc.getDouble("price") != null ? doc.getDouble("price") : 0.0;
                        String imageUrl = doc.getString("imageUrl");
                        boolean available = doc.getBoolean("available") != null && doc.getBoolean("available");
                        String category = doc.getString("category");

                        // Add the product object to the list
                        Product product = new Product(id, name, category, price, description, imageUrl, available, selectedShopId);
                        allProducts.add(product);
                    }

                    // Update the adapter with the product list
                    productAdapter.updateList(allProducts);
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ShopProductActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                });
    }
}
