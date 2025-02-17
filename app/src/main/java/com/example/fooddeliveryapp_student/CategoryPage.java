package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryPage extends AppCompatActivity {
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categoryList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_page);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerViewCategories = findViewById(R.id.recyclerView);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));

        // Initialize List
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, this, category -> {
            // Handle category click event if needed
            Toast.makeText(this, "Clicked: " + category.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerViewCategories.setAdapter(categoryAdapter);

        // Fetch Categories from Firebase
        loadCategories();
    }

    private void loadCategories() {
        CollectionReference categoryRef = db.collection("categories");

        categoryRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("FirebaseError", "Error loading categories", error);
                Toast.makeText(CategoryPage.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                return;
            }

            categoryList.clear();
            if (value != null) {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    CategoryModel category = doc.toObject(CategoryModel.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });
    }
}
