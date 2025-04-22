package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private TextView restaurantName, ratingText, deliveryTime, restaurantDescription, restaurantAddress;

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
        fetchShopDetails(); // fetch shop details from Firestore

    }

    private void initViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        restaurantName = findViewById(R.id.restaurantName);
        ratingText = findViewById(R.id.ratingText);
        deliveryTime = findViewById(R.id.deliveryTime);
        restaurantDescription = findViewById(R.id.restaurantDescription);
        restaurantAddress = findViewById(R.id.restaurantAddress);
    }

    private void setupToolbar() {
        toolbarr = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarr);
        toolbarr.setNavigationOnClickListener(v -> finish());
    }
    private void setupRecyclerView() {
        productAdapter = new ShopProductAdapter(new ArrayList<>(), product -> {
            Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void fetchShopDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shops").document(selectedShopId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        restaurantName.setText(documentSnapshot.getString("name"));
                        ratingText.setText(String.valueOf(documentSnapshot.getDouble("rating")));
                        deliveryTime.setText(documentSnapshot.getString("deliveryTime"));  // e.g. "25-30 mins"
                        restaurantDescription.setText(documentSnapshot.getString("description"));
                        restaurantAddress.setText(documentSnapshot.getString("address"));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load shop details", Toast.LENGTH_SHORT).show();
                });
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
                        Boolean available = doc.getBoolean("available");
                        if (available != null && available) {
                            String productId = doc.getId();  // Fetch the product ID
                            Product product = new Product(
                                    productId,  // Use the product ID here
                                    doc.getString("name"),
                                    doc.getString("category"),
                                    doc.getDouble("price"),
                                    doc.getString("description"),
                                    doc.getString("imageUrl"),
                                    available,
                                    doc.getString("shopId")
                            );
                            allProducts.add(product);
                        }
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
