package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Admin_UpdateCategory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminUpdateCategoryAdapter adapter;
    private List<AdminUpdateCategoryModel> categoryList;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_category);

        recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();
        adapter = new AdminUpdateCategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);

        fetchCategories();
    }

    private void fetchCategories() {
        CollectionReference ref = db.collection("categories");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(Admin_UpdateCategory.this, "Error loading categories", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value == null) return;

                categoryList.clear();

                for (QueryDocumentSnapshot doc : value) {
                    String name = doc.getString("name");
                    String imageUrl = doc.getString("imageUrl");

                    // === Validations ===
                    if (name == null || name.trim().isEmpty()) continue;
                    if (imageUrl == null || imageUrl.trim().isEmpty()) continue;


                    AdminUpdateCategoryModel model = doc.toObject(AdminUpdateCategoryModel.class);
                    model.setId(doc.getId());
                    categoryList.add(model);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }
}
