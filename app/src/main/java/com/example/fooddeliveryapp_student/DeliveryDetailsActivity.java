package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvAddress, tvItem, tvStatus;
    private Button btnStartDelivery, btnMarkDelivered;

    private FirebaseFirestore db;
    private String orderId, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvAddress = findViewById(R.id.tvAddress);
        tvItem = findViewById(R.id.tvItem);
        tvStatus = findViewById(R.id.tvStatus);
        btnStartDelivery = findViewById(R.id.btnStartDelivery);
        btnMarkDelivered = findViewById(R.id.btnMarkDelivered);

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("orderId");

        loadOrderDetails();

        btnStartDelivery.setOnClickListener(v -> updateOrderStatus("Out for Delivery"));

        btnMarkDelivered.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveryDetailsActivity.this, Delivery_OTP.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("phoneNumber", phone); // to send OTP
            startActivity(intent);
        });
    }

    private void loadOrderDetails() {
        db.collection("orders").document(orderId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Extract deliveryAddress map
                        HashMap<String, Object> addressMap = (HashMap<String, Object>) document.get("deliveryAddress");
                        String name = (String) addressMap.get("name");
                        phone = (String) addressMap.get("phone");
                        String hostel = (String) addressMap.get("hostel");
                        String room = (String) addressMap.get("room");

                        // Extract items
                        ArrayList<HashMap<String, Object>> items = (ArrayList<HashMap<String, Object>>) document.get("items");

                        // Concatenate all item names
                        StringBuilder itemNames = new StringBuilder();
                        for (HashMap<String, Object> item : items) {
                            String itemName = (String) item.get("name");
                            int quantity = ((Long) item.get("quantity")).intValue();
                            itemNames.append(itemName).append(" x").append(quantity).append("\n");
                        }

                        String status = document.getString("status");

                        // Set to UI
                        tvCustomerName.setText("Customer: " + name);
                        tvAddress.setText("Address: Hostel " + hostel + ", Room " + room);
                        tvItem.setText("Items:\n" + itemNames.toString().trim());
                        tvStatus.setText("Status: " + status);

                    } else {
                        Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading order", Toast.LENGTH_SHORT).show());
    }

    private void updateOrderStatus(String newStatus) {
        db.collection("orders").document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    tvStatus.setText("Status: " + newStatus);
                    Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }
}
