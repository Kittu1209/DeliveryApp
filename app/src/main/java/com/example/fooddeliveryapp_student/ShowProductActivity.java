package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FilterProductAdapter adapter;
    private List<FilterProductModel> productList;
    private List<FilterProductModel> originalProductList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private ChipGroup chipGroup;
    private TextInputEditText searchView;
    private Toolbar toolbar;
    private int currentFilter = R.id.chipAll; // Track current filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        // Initialize views
        recyclerView = findViewById(R.id.productsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        chipGroup = findViewById(R.id.filterChipGroup);
        searchView = findViewById(R.id.searchView);
        toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();
        originalProductList = new ArrayList<>();
        adapter = new FilterProductAdapter(productList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set default selection
        Chip defaultChip = findViewById(R.id.chipAll);
        defaultChip.setChecked(true);

        fetchAllProducts();

        // Setup chip group listener
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                currentFilter = checkedId;
                applyFilterAndSearch();
            }
        });


        // Setup search functionality
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchAllProducts() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        originalProductList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            Double priceValue = document.getDouble("price");
                            double price = priceValue != null ? priceValue : 0.0;
                            String imageUrl = document.getString("imageUrl");
                            Timestamp createdAt = document.getTimestamp("timestamp");
                            String productId = document.getId(); // Get productId

                            FilterProductModel product = new FilterProductModel(name, price, createdAt, imageUrl,productId);
                            productList.add(product);
                            originalProductList.add(product);
                        }

                        applyFilterAndSearch();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ShowProductActivity.this, "Error fetching products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyFilterAndSearch() {
        List<FilterProductModel> filteredList = new ArrayList<>(originalProductList);

        // Apply search filter first
        String searchQuery = searchView.getText().toString().trim().toLowerCase();
        if (!searchQuery.isEmpty()) {
            filteredList = filterBySearch(filteredList, searchQuery);
        }

        // Then apply the selected sorting filter
        applyCurrentFilter(filteredList);

        // Update UI
        emptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.updateList(filteredList);
    }

    private List<FilterProductModel> filterBySearch(List<FilterProductModel> list, String query) {
        List<FilterProductModel> filtered = new ArrayList<>();
        for (FilterProductModel product : list) {
            if (product.getName() != null && product.getName().toLowerCase().contains(query)) {
                filtered.add(product);
            }
        }
        return filtered;
    }

    private void applyCurrentFilter(List<FilterProductModel> list) {
        if (currentFilter == R.id.chipNew) {
            sortByNewArrivals(list);
        } else if (currentFilter == R.id.chiphighlow) {
            sortByPriceHighToLow(list);
        } else if (currentFilter == R.id.chiplowhigh) {
            sortByPriceLowToHigh(list);
        } else if (currentFilter == R.id.chipaz) {
            sortByNameAZ(list);
        }
        // For "All" we don't need to sort
    }

    private void sortByNewArrivals(List<FilterProductModel> list) {
        Collections.sort(list, (p1, p2) -> {
            Timestamp t1 = p1.getCreatedAt();
            Timestamp t2 = p2.getCreatedAt();
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1);
        });
    }

    private void sortByPriceHighToLow(List<FilterProductModel> list) {
        Collections.sort(list, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
    }

    private void sortByPriceLowToHigh(List<FilterProductModel> list) {
        Collections.sort(list, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
    }


    private void sortByNameAZ(List<FilterProductModel> list) {
        Collections.sort(list, (p1, p2) -> {
            if (p1.getName() == null && p2.getName() == null) return 0;
            if (p1.getName() == null) return 1;
            if (p2.getName() == null) return -1;
            return p1.getName().compareToIgnoreCase(p2.getName());
        });
    }
}