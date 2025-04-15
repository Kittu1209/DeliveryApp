// Fragment_HomeStudent.java
package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fragment_HomeStudent extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private TextView noResultsText;
    private EditText searchBox;

    private RecyclerView horizontalRecyclerView;
    private HorizontalAdapter horizontalAdapter;
    private List<HorizontalModel> categoryList;

    private static final String TAG = "Fragment_HomeStudent";
    private Handler handler = new Handler();

    public Fragment_HomeStudent() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home_student, container, false);

        db = FirebaseFirestore.getInstance();
        productRecyclerView = view.findViewById(R.id.pop_rec);
        horizontalRecyclerView = view.findViewById(R.id.hor_rec);
        searchBox = view.findViewById(R.id.search_box);
        noResultsText = view.findViewById(R.id.no_results_text);

        setupRecyclerView();
        setupHorizontalRecyclerView();
        loadProducts();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> filterProducts(s.toString()), 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void setupRecyclerView() {
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList, product -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            startActivity(intent);
        });
        productRecyclerView.setAdapter(productAdapter);
    }

    private void setupHorizontalRecyclerView() {
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        horizontalAdapter = new HorizontalAdapter(getContext(), categoryList, categoryName -> {
            Log.d(TAG, "Category clicked: " + categoryName);
            filterByCategory(categoryName);
        });
        horizontalRecyclerView.setAdapter(horizontalAdapter);
    }

    private void loadProducts() {
        db.collection("products").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Toast.makeText(getContext(), "No products found", Toast.LENGTH_SHORT).show();
                return;
            }

            productList.clear();
            Set<String> categorySet = new HashSet<>();

            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Product product = document.toObject(Product.class);
                if (product != null) {
                    product.setId(document.getId());
                    productList.add(product);

                    if (product.getCategory() != null) {
                        categorySet.add(product.getCategory());
                    }
                }
            }

            productAdapter.filterList(productList);

            categoryList.clear();
            categoryList.add(new HorizontalModel("All", "", ""));
            for (String cat : categorySet) {
                categoryList.add(new HorizontalModel(cat, "", ""));
            }
            horizontalAdapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: ", e);
        });
    }

    private void filterProducts(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product item : productList) {
            if ((item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                    (item.getCategory() != null && item.getCategory().toLowerCase().contains(text.toLowerCase())) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                filteredList.add(item);
            }
        }

        noResultsText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        productAdapter.filterList(filteredList);
    }

    private void filterByCategory(String category) {
        Log.d(TAG, "Filtering by category: " + category);
        if (category.equals("All")) {
            productAdapter.filterList(productList);
            noResultsText.setVisibility(View.GONE);
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(product);
            }
        }

        noResultsText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        productAdapter.filterList(filteredList);
    }
}
