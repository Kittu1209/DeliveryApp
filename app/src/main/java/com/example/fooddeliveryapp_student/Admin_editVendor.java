package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Admin_editVendor extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminEditVendorAdapter adapter;
    private final List<AdminEditVendorModel> vendorList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_vendor);

        recyclerView = findViewById(R.id.recyclerViewEditVendor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminEditVendorAdapter(this, vendorList);
        recyclerView.setAdapter(adapter);

        fetchVendors();
    }

    private void fetchVendors() {
        FirebaseFirestore.getInstance().collection("Vendors")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Failed to load vendors.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) {
                        Toast.makeText(this, "No vendor data found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    vendorList.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        // Extract fields
                        String docId = doc.getId();
                        String vendorName = doc.getString("vendorName");
                        String vendorEmail = doc.getString("vendorEmail");
                        String vendorPhone = doc.getString("vendorPhone");
                        String vendorId = doc.getString("vendorId");
                        String shopName = doc.getString("shopName");

                        // Apply validations
                        if (isNullOrEmpty(vendorName)) continue;
                        if (isNullOrEmpty(vendorEmail)) continue;
                        if (isNullOrEmpty(vendorPhone)) continue;
                        if (isNullOrEmpty(vendorId)) continue;
                        if (isNullOrEmpty(shopName)) continue;

                        // (Optional) Phone validation
                        if (!vendorPhone.matches("^[0-9]{10}$")) continue;

                        // Create model and add to list
                        AdminEditVendorModel vendor = new AdminEditVendorModel(
                                docId,
                                vendorName,
                                vendorEmail,
                                vendorPhone,
                                vendorId,
                                shopName
                        );
                        vendorList.add(vendor);
                    }

                    if (vendorList.isEmpty()) {
                        Toast.makeText(this, "No valid vendors found.", Toast.LENGTH_SHORT).show();
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
