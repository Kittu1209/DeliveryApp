package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Fragment_HomeStudent extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private TextView noResultsText;

    private EditText searchBox;
    private static final String TAG = "Fragment_HomeStudent";

    public Fragment_HomeStudent() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home_student, container, false);

        db = FirebaseFirestore.getInstance();
        productRecyclerView = view.findViewById(R.id.pop_rec);
        searchBox = view.findViewById(R.id.search_box);
        noResultsText = view.findViewById(R.id.no_results_text);


        setupRecyclerView();
        loadProducts();

        // 👇 Real-time search logic
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
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

    private void loadProducts() {
        db.collection("products").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Toast.makeText(getContext(), "No products found", Toast.LENGTH_SHORT).show();
                return;
            }

            productList.clear();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Product product = document.toObject(Product.class);
                if (product != null) {
                    product.setId(document.getId());
                    productList.add(product);
                }
            }

            productAdapter.filterList(productList);  // Load full list first

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: ", e);
        });
    }

    private void filterProducts(String text) {
        List<Product> filteredList = new ArrayList<>();

        for (Product item : productList) {
            if (
                    (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                            (item.getCategory() != null && item.getCategory().toLowerCase().contains(text.toLowerCase())) ||
                            (item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))
            ) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            noResultsText.setVisibility(View.GONE);
        }

        productAdapter.filterList(filteredList);
    }


}
