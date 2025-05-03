package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminApproveVendor extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AdminApproveVendorAdapter adapter;
    private final List<AdminApproveVendorModel> vendorList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_vendor);
        initializeViews();
        setupRecyclerView();
        loadPendingVendors();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.rvVendors);
        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminApproveVendorAdapter(vendorList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void loadPendingVendors() {
        progressBar.setVisibility(View.VISIBLE);
        vendorList.clear();

        db.collection("shops")
                .whereEqualTo("isActive", false)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        processVendorDocuments(task.getResult().getDocuments());
                    } else {
                        handleLoadError(task.getException());
                    }
                });
    }

    private void processVendorDocuments(List<DocumentSnapshot> documents) {
        for (DocumentSnapshot document : documents) {
            try {
                if (shouldSkipDocument((QueryDocumentSnapshot) document)) continue;

                AdminApproveVendorModel vendor = new AdminApproveVendorModel(document.getData());
                vendor.setId(document.getId());
                vendorList.add(vendor);
            } catch (Exception e) {
                Log.e("AdminApproveVendor", "Error processing document", e);
            }
        }

        updateUIAfterLoad();
    }

    private boolean shouldSkipDocument(QueryDocumentSnapshot document) {
        String name = document.getString("name");
        return name != null && name.toLowerCase().contains("rejected");
    }

    private void updateUIAfterLoad() {
        adapter.notifyDataSetChanged();
        if (vendorList.isEmpty()) {
            Toast.makeText(this, "No vendors pending approval", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLoadError(Exception exception) {
        Log.e("AdminApproveVendor", "Load error", exception);
        Toast.makeText(this, "Failed to load vendors", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}