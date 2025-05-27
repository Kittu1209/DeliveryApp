package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHomeActivity extends AppCompatActivity {

    private Button toggleStatusButton;
    private TextView statusText;
    private TextView orderDetailsText;
    private ImageView profileImage;
    private ImageButton account;
    private RecyclerView ordersRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentDutyStatus = "Not Available";
    private List<Order> orderList = new ArrayList<>();
    private OrderDeliveryHomeAdapter orderAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_home);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(DeliveryHomeActivity.this, LoginPage.class); // Redirect to login page
            startActivity(intent);
            finish();
            return;
        }

        // Find views by their IDs
        toggleStatusButton = findViewById(R.id.toggleStatusButton);
        statusText = findViewById(R.id.statusText); // Text to show status
        orderDetailsText = findViewById(R.id.orderDetailsText); // Text to show order id and status
        profileImage = findViewById(R.id.profileImage); // Profile image to open profile page
        account = findViewById(R.id.accountbtn);

        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       // orderAdapter = new OrderDeliveryHomeAdapter(orderList);
        orderAdapter = new OrderDeliveryHomeAdapter(orderList, DeliveryHomeActivity.this);

        ordersRecyclerView.setAdapter(orderAdapter);

        // Open settings page on account button click
        account.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveryHomeActivity.this, SettingPageDelivery.class);
            startActivity(intent);
        });

        // Set click listener for the profile image to open the profile page
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveryHomeActivity.this, DeliveryProfileActivity.class); // Open profile page
            startActivity(intent);
        });

        // Set click listener for the toggle status button
//        toggleStatusButton.setOnClickListener(v -> {
//            if (currentDutyStatus.equals("Not Available")) {
//                currentDutyStatus = "Available";
//                statusText.setText("Current Status: Available");
//                fetchAssignedOrders();  // Fetch the assigned orders when status is available
//            } else {
//                currentDutyStatus = "Not Available";
//                statusText.setText("Current Status: Not Available");
//                orderDetailsText.setText("Order ID: - \nStatus: -");
//                orderList.clear();  // Clear the order list when not available
//                orderAdapter.notifyDataSetChanged();
//            }
//        });
        toggleStatusButton.setOnClickListener(v -> {
            if (currentDutyStatus.equals("Not Available")) {
                currentDutyStatus = "Available";

                if (statusText != null) {
                    statusText.setText("Current Status: Available");
                } else {
                    Log.e("DeliveryHomeActivity", "statusText is null!");
                }

                fetchAssignedOrders();  // Fetch the assigned orders when status is available
            } else {
                currentDutyStatus = "Not Available";

                if (statusText != null) {
                    statusText.setText("Current Status: Not Available");
                } else {
                    Log.e("DeliveryHomeActivity", "statusText is null!");
                }

                if (orderDetailsText != null) {
                    orderDetailsText.setText("Order ID: - \nStatus: -");
                }

                orderList.clear();  // Clear the order list when not available
                orderAdapter.notifyDataSetChanged();
            }
        });


        // Initial data fetch when the activity is created
        fetchAssignedOrders();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchAssignedOrders() {
        // Get the logged-in delivery man ID (using Firebase UID)
        String loggedInDeliveryManId = mAuth.getCurrentUser().getUid();

        db.collection("orders")
                .whereEqualTo("assignedDeliveryManId", loggedInDeliveryManId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear(); // Clear previous data

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            String status = doc.getString("status");
                            if (status != null && !status.equalsIgnoreCase("delivered")) {
                                String orderId = doc.getString("orderId");

                                // Create an Order object and add to the list
                                Order assignedOrder = new Order(orderId, status, loggedInDeliveryManId);
                                orderList.add(assignedOrder);
                            }
                        }
                        orderAdapter.notifyDataSetChanged(); // Update the RecyclerView
                    } else {
                        Toast.makeText(this, "No active orders assigned", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    Log.e("ORDER_FETCH_ERROR", e.getMessage(), e);
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Ensure the user is logged in when the app starts
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(DeliveryHomeActivity.this, LoginPage.class);
            startActivity(intent);
            finish();
        }
    }
}
