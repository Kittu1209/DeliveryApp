package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Admin_viewProducts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<AdminProductModel> productList;
    private AdminProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_products);

        recyclerView = findViewById(R.id.recyclerViewAdminProducts);
        productList = new ArrayList<>();
        adapter = new AdminProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchProducts();
    }

    private void fetchProducts() {
        FirebaseFirestore.getInstance().collection("products")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    productList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        AdminProductModel product = new AdminProductModel(
                                doc.getId(),
                                doc.getString("name"),
                                doc.getString("description"),
                                doc.getString("category"),
                                doc.getString("imageUrl"),
                                doc.getString("shopId"),
                                doc.getDouble("price")
                        );
                        productList.add(product);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
