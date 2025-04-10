package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvAddress, tvItem, tvStatus;
    private Button btnStartDelivery, btnMarkDelivered;
    private CheckBox checkboxPicked;

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
        checkboxPicked = findViewById(R.id.checkboxPicked);

        btnMarkDelivered.setOnClickListener(v -> {
            if (!checkboxPicked.isChecked()) {
                Toast.makeText(this, "Please confirm all items picked.", Toast.LENGTH_SHORT).show();
                return;
            }
            showOtpDialog(orderId);
        });


        btnStartDelivery.setOnClickListener(v -> updateOrderStatus("Out for Delivery"));

        btnMarkDelivered.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveryDetailsActivity.this, Delivery_OTP.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("phoneNumber", phone); // to send OTP
            startActivity(intent);
        });
    }
    private void showOtpDialog(String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter OTP from Student");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredOtp = input.getText().toString().trim();
            if (!enteredOtp.isEmpty()) {
                verifyOtpAndDeliver(orderId, enteredOtp);
            } else {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void verifyOtpAndDeliver(String orderId, String enteredOtp) {
        db.collection("delivery_otp").document(orderId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String storedOtp = String.valueOf(snapshot.get("otp"));
                        if (enteredOtp.equals(storedOtp)) {
                            db.collection("orders").document(orderId)
                                    .update("status", "delivered");

                            db.collection("delivery_assignments").document(orderId)
                                    .update("status", "Delivered");

                            db.collection("delivery_otp").document(orderId).delete();

                            Toast.makeText(this, "Order marked as delivered!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "OTP not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to verify OTP", Toast.LENGTH_SHORT).show());
    }

    private void loadOrderDetails() {
        db.collection("orders").document(orderId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        HashMap<String, Object> addressMap = (HashMap<String, Object>) document.get("deliveryAddress");
                        String name = (String) addressMap.get("name");
                        phone = (String) addressMap.get("phone");
                        String hostel = (String) addressMap.get("hostel");
                        String room = (String) addressMap.get("room");

                        ArrayList<HashMap<String, Object>> items = (ArrayList<HashMap<String, Object>>) document.get("items");

                        StringBuilder itemDetails = new StringBuilder();
                        ArrayList<String> shopIdsFetched = new ArrayList<>();

                        for (HashMap<String, Object> item : items) {
                            String itemName = (String) item.get("name");
                            int quantity = ((Long) item.get("quantity")).intValue();
                            String shopId = (String) item.get("shopId");

                            itemDetails.append("• ").append(itemName).append(" x").append(quantity);

                            // Fetch shop details only once per shopId
                            if (!shopIdsFetched.contains(shopId)) {
                                shopIdsFetched.add(shopId);
                                db.collection("shops").document(shopId)
                                        .get()
                                        .addOnSuccessListener(shopDoc -> {
                                            if (shopDoc.exists()) {
                                                String shopName = shopDoc.getString("shopName");
                                                String shopAddress = shopDoc.getString("shopAddress");
                                                itemDetails.append("\n   from: ").append(shopName)
                                                        .append("\n   at: ").append(shopAddress).append("\n\n");

                                                // Update UI each time a shop detail loads
                                                tvItem.setText("Items:\n" + itemDetails.toString().trim());
                                            }
                                        });
                            } else {
                                itemDetails.append("\n\n");
                            }
                        }

                        String status = document.getString("status");

                        tvCustomerName.setText("Customer: " + name);
                        tvAddress.setText("Address: Hostel " + hostel + ", Room " + room + "\nPhone: " + phone);
                        tvItem.setText("Items:\n" + itemDetails.toString().trim());
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
