package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        selectedCategory = getIntent().getStringExtra("category");

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

        // Query Firestore to get products that belong to the selected category
        firestore.collection("products")
                .whereEqualTo("category", selectedCategory) // Filter by category
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Clear the existing product list and add the new products
                    productList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Convert document snapshot to CategoryProductModel object
                        CategoryProductModel model = doc.toObject(CategoryProductModel.class);
                        // Set the product ID (optional but useful for updates or deletions)
                        productList.add(model);
                    }
                    // Notify adapter that the data has changed and should be refreshed
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle failure case
                    Toast.makeText(CategoryShowProduct.this, "Error fetching products", Toast.LENGTH_SHORT).show();
                });
    }
}
