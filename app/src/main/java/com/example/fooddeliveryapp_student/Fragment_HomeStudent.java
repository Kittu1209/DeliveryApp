package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fragment_HomeStudent extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private EditText searchBox;
    private static final String TAG = "Fragment_HomeStudent";

    public Fragment_HomeStudent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home_student, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        productRecyclerView = view.findViewById(R.id.pop_rec);
        searchBox = view.findViewById(R.id.search_box);

        // Setup RecyclerView
        setupRecyclerView();

        // Load products from Firestore
        loadProducts();

        return view;
    }

    private void setupRecyclerView() {
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList, product -> {
            // Handle product click - Open Product Detail Page
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            startActivity(intent);
        });
        productRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        CollectionReference productsRef = db.collection("products");

        productsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Toast.makeText(getContext(), "No products found", Toast.LENGTH_SHORT).show();
                return;
            }

            productList.clear(); // Clear previous data before adding new items
            int count = 0; // Debugging: Count fetched products

            for (DocumentSnapshot document : queryDocumentSnapshots) {
                if (document.exists()) {
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        productList.add(product);
                        count++;
                        Log.d(TAG, "Loaded Product: " + product.getName() + " | ID: " + product.getId());
                    }
                }
            }

            Log.d(TAG, "Total products loaded: " + count);
            productAdapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error loading products: " + e.getMessage(), e);
        });
    }
}
