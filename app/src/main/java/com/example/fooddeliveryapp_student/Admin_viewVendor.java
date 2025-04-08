package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class Admin_viewVendor extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminViewVendorAdapter adapter;
    private List<AdminViewVendorModel> vendorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_vendor);

        recyclerView = findViewById(R.id.recyclerViewVendors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vendorList = new ArrayList<>();
        adapter = new AdminViewVendorAdapter(vendorList);
        recyclerView.setAdapter(adapter);

        fetchVendors();
    }

    private void fetchVendors() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Vendors").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    vendorList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        AdminViewVendorModel vendor = doc.toObject(AdminViewVendorModel.class);
                        vendorList.add(vendor);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching vendors", e));
    }
}
