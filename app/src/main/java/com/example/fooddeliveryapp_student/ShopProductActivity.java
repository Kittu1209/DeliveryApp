package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // ✅ Correct Toolbar import
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShopProductActivity extends AppCompatActivity {
    private RecyclerView productsRecyclerView;
    private ProgressBar progressBar;
    private ShopProductAdapter productAdapter;
    private List<Product> allProducts = new ArrayList<>();
    private String selectedShopId;

    private Toolbar toolbarr; // ✅ Correct type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product);

        selectedShopId = getIntent().getStringExtra("id");

        initViews();

        toolbarr = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarr); // ✅ Works now
        toolbarr.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        loadProducts();
    }

    private void initViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        productAdapter = new ShopProductAdapter(new ArrayList<>(), product -> {
            Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("shopId", selectedShopId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allProducts.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = new Product(
                                doc.getId(),
                                doc.getString("name"),
                                doc.getString("category"),
                                doc.getDouble("price"),
                                doc.getString("description"),
                                doc.getString("imageUrl"),
                                doc.getBoolean("available"),
                                doc.getString("shopId")
                        );
                        allProducts.add(product);
                    }
                    productAdapter.updateList(allProducts);
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show();
                });
    }
}
