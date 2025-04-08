package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHomeActivity extends AppCompatActivity {

    private TextView statusText;
    private Button toggleStatusButton;
    private RecyclerView ordersRecyclerView;
    private ImageView profileImage;

    private OrderAdapterDeliver orderAdapter;
    private List<order_model_vendor> orderList;

    private FirebaseFirestore db;
    private String deliveryManId;
    private String currentStatus;
    private ListenerRegistration orderListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_home);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // User is NOT logged in, redirect to LoginActivity
            Intent intent = new Intent(DeliveryHomeActivity.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        statusText = findViewById(R.id.statusText);
        toggleStatusButton = findViewById(R.id.toggleStatusButton);
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        profileImage = findViewById(R.id.profileImage); // 🔹 Initialize profile image

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        deliveryManId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapterDeliver(orderList, this::openOrderDetails);
        ordersRecyclerView.setAdapter(orderAdapter);

        fetchDeliveryManStatus();

        toggleStatusButton.setOnClickListener(v -> showStatusConfirmationDialog());

        // 🔹 Open profile page on profile image click
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveryHomeActivity.this, DeliveryProfileActivity.class);
            startActivity(intent);
        });
    }

    private void fetchDeliveryManStatus() {
        DocumentReference ref = db.collection("delivery_man").document(deliveryManId);
        ref.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                currentStatus = documentSnapshot.getString("current_duty");
                updateStatusUI();
                listenForAssignedOrders();
            } else {
                Toast.makeText(this, "Delivery man data not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStatusConfirmationDialog() {
        String newStatus = "Available".equals(currentStatus) ? "Not Available" : "Available";
        new AlertDialog.Builder(this)
                .setTitle("Change Status")
                .setMessage("Are you sure you want to set your status to \"" + newStatus + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> updateDeliveryManStatus(newStatus))
                .setNegativeButton("No", null)
                .show();
    }

    private void updateDeliveryManStatus(String newStatus) {
        db.collection("delivery_man").document(deliveryManId)
                .update("current_duty", newStatus)
                .addOnSuccessListener(aVoid -> {
                    currentStatus = newStatus;
                    updateStatusUI();
                    listenForAssignedOrders();
                    Toast.makeText(this, "Status Updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateStatusUI() {
        statusText.setText("Current Status: " + currentStatus);
        toggleStatusButton.setText("Available".equals(currentStatus) ? "Set Not Available" : "Set Available");
    }

    private void listenForAssignedOrders() {
        if (orderListener != null) orderListener.remove();

        if (!"Available".equals(currentStatus)) {
            orderList.clear();
            orderAdapter.notifyDataSetChanged();
            showEmptyState(true);
            return;
        }

        orderListener = db.collection("orders")
                .whereEqualTo("assignedTo", deliveryManId)
                .whereEqualTo("status", "assigned")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading orders.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        orderList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            order_model_vendor order = doc.toObject(order_model_vendor.class);
                            orderList.add(order);
                        }
                        orderAdapter.notifyDataSetChanged();
                        showEmptyState(orderList.isEmpty());
                    }
                });
    }

    private void showEmptyState(boolean show) {
        // Add visibility logic if using emptyOrdersText
    }

    private void openOrderDetails(order_model_vendor order) {
        Intent intent = new Intent(DeliveryHomeActivity.this, OrderDetailActivity.class);
        intent.putExtra("orderId", order.getOrderId());
        intent.putExtra("name", order.getName());
        intent.putExtra("hostel", order.getHostel());
        intent.putExtra("room", order.getRoom());
        intent.putExtra("phone", order.getPhone());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderListener != null) {
            orderListener.remove();
        }
    }
}
