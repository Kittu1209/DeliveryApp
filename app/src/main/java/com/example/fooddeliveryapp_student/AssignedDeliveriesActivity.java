package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssignedDeliveriesActivity extends AppCompatActivity {

    private RecyclerView recyclerDeliveries;
    private List<AssignedDeliveryModel> deliveryList;
    private AssignedDeliveryAdapter adapter;
    private FirebaseFirestore db;
    private String deliveryManId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_deliveries);

        recyclerDeliveries = findViewById(R.id.recyclerDeliveries);
        recyclerDeliveries.setLayoutManager(new LinearLayoutManager(this));
        deliveryList = new ArrayList<>();
        adapter = new AssignedDeliveryAdapter(this, deliveryList);
        recyclerDeliveries.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        deliveryManId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchAssignedDeliveries();
    }

    private void fetchAssignedDeliveries() {
        db.collection("orders")
                .whereEqualTo("deliveryManId", deliveryManId)
                .whereEqualTo("status", "Assigned") // status instead of deliveryStatus
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    deliveryList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String orderId = doc.getId();

                        // Get delivery address map
                        Map<String, Object> addressMap = (Map<String, Object>) doc.get("deliveryAddress");
                        String customerName = addressMap != null ? (String) addressMap.get("name") : "Unknown";
                        String hostel = addressMap != null ? (String) addressMap.get("hostel") : "";
                        String room = addressMap != null ? (String) addressMap.get("room") : "";
                        String address = "Hostel " + hostel + ", Room " + room;

                        // Get items list
                        List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                        StringBuilder itemNames = new StringBuilder();
                        if (items != null) {
                            for (Map<String, Object> item : items) {
                                String itemName = (String) item.get("name");
                                Long quantity = (Long) item.get("quantity");
                                itemNames.append(itemName)
                                        .append(" x").append(quantity != null ? quantity : 1)
                                        .append("\n");
                            }
                        }

                        // Status field
                        String deliveryStatus = doc.getString("status");

                        // Create model and add to list
                        AssignedDeliveryModel model = new AssignedDeliveryModel(
                                orderId,
                                customerName,
                                address,
                                itemNames.toString().trim(),
                                deliveryStatus
                        );
                        deliveryList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load deliveries", Toast.LENGTH_SHORT).show()
                );
    }

}
