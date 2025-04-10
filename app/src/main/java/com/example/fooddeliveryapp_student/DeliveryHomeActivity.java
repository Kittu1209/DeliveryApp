package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeliveryHomeActivity extends AppCompatActivity {

    private Button toggleStatusButton;
    private Button completeOrderButton;
    private TextView statusText;
    private TextView orderDetailsText;
    private ImageView profileImage;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentDutyStatus = "Not Available";
    private String assignedOrderId = "";  // Variable to store the assigned order ID

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
        completeOrderButton = findViewById(R.id.completeOrderButton);
        statusText = findViewById(R.id.statusText); // Text to show status
        orderDetailsText = findViewById(R.id.orderDetailsText); // Text to show order id and status
        profileImage = findViewById(R.id.profileImage); // Profile image to open profile page

        db = FirebaseFirestore.getInstance();

        // Set click listener for the profile image to open the profile page
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveryHomeActivity.this, DeliveryProfileActivity.class); // Open profile page
            startActivity(intent);
        });

        // Set click listener for the toggle status button
        toggleStatusButton.setOnClickListener(v -> {
            if (currentDutyStatus.equals("Not Available")) {
                currentDutyStatus = "Available";
                statusText.setText("Current Status: Available");
                fetchAssignedOrder();  // Fetch the assigned order when status is available
            } else {
                currentDutyStatus = "Not Available";
                statusText.setText("Current Status: Not Available");
                orderDetailsText.setText("Order ID: - \nStatus: -");
                assignedOrderId = "";  // Reset assigned order ID when not available
            }
        });

        // Set click listener for the complete order button
        completeOrderButton.setOnClickListener(v -> {
            if (!assignedOrderId.isEmpty()) {
                Intent intent = new Intent(DeliveryHomeActivity.this, DeliveryDetailsActivity.class);
                intent.putExtra("orderId", assignedOrderId); // Pass the correct orderId to details page
                startActivity(intent);
            } else {
                Toast.makeText(this, "No order assigned", Toast.LENGTH_SHORT).show();
            }
        });

        // Initial data fetch when the activity is created
        fetchAssignedOrder();
    }

    private void fetchAssignedOrder() {
        // Get the logged-in delivery man ID (using Firebase UID)
        String loggedInDeliveryManId = mAuth.getCurrentUser().getUid();

        db.collection("orders")
                .whereEqualTo("assignedDeliveryManId", loggedInDeliveryManId)
                .limit(1) // Fetching the first assigned order
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first order document
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String orderId = doc.getString("orderId");
                        String status = doc.getString("status");

                        // Set order details in the TextView
                        assignedOrderId = orderId;  // Store the assigned order ID
                        orderDetailsText.setText("Order ID: " + orderId + "\nStatus: " + status);

                    } else {
                        orderDetailsText.setText("Order ID: - \nStatus: No orders assigned");
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
