package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VendorProductsActivity extends AppCompatActivity implements VendorProductsAdapter.OnProductActionListener {

    private static final String TAG = "VendorProducts";
    private RecyclerView recyclerView;
    private VendorProductsAdapter adapter;
    private List<VendorProduct> productList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private String shopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_products);

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView = findViewById(R.id.recyclerViewProducts);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Setup RecyclerView
        productList = new ArrayList<>();
        adapter = new VendorProductsAdapter(this, productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load data
        fetchShopId();
    }

    private void fetchShopId() {
        showLoading(true);

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("shops")
                .whereEqualTo("ownerId", mAuth.getCurrentUser().getUid())
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        shopId = task.getResult().getDocuments().get(0).getId();
                        loadProducts();
                    } else {
                        showLoading(false);
                        Toast.makeText(this, "No shop found for this user", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error fetching shop", task.getException());
                        finish();
                    }
                });
    }

    private void loadProducts() {
        showLoading(true);

        db.collection("products")
                .whereEqualTo("shopId", shopId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                VendorProduct product = document.toObject(VendorProduct.class);
                                product.setId(document.getId());
                                productList.add(product);
                                Log.d(TAG, "Loaded product: " + product.getName());
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing product", e);
                            }
                        }

                        if (productList.isEmpty()) {
                            showEmptyState(true);
                        } else {
                            showEmptyState(false);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        showEmptyState(true);
                        Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading products", task.getException());
                    }
                });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showEmptyState(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    public void onEditProduct(VendorProduct product) {
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("product", (Serializable) product); // Explicitly cast to Serializable
        startActivity(intent);
    }


    @Override
    public void onToggleAvailability(VendorProduct product) {
        boolean newAvailability = !product.isAvailable();
        db.collection("products").document(product.getId())
                .update("available", newAvailability)
                .addOnSuccessListener(aVoid -> {
                    product.setAvailable(newAvailability);
                    adapter.notifyItemChanged(productList.indexOf(product));
                    Toast.makeText(this,
                            product.getName() + " is now " +
                                    (newAvailability ? "in stock" : "out of stock"),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update availability", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shopId != null) {
            loadProducts();
        }
    }
}