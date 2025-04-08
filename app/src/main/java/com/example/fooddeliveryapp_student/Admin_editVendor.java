package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
                    if (error != null || value == null) return;

                    vendorList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        AdminEditVendorModel vendor = new AdminEditVendorModel(
                                doc.getId(),
                                doc.getString("vendorName"),
                                doc.getString("vendorEmail"),
                                doc.getString("vendorPhone"),
                                doc.getString("vendorId"),
                                doc.getString("shopName")
                        );
                        vendorList.add(vendor);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
