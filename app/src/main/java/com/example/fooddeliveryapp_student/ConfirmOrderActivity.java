package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ConfirmOrderActivity extends AppCompatActivity {

    private TextView tvEstimatedTime;
    private Button btnGoHome, btnGoOrders;
    private FirebaseFirestore db;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        tvEstimatedTime = findViewById(R.id.tv_estimated_time);
        btnGoHome = findViewById(R.id.btn_go_home);
        btnGoOrders = findViewById(R.id.btn_go_orders);
        db = FirebaseFirestore.getInstance();

        // ✅ Get order ID from Intent and log it
        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId == null || orderId.trim().isEmpty()) {
            Toast.makeText(this, "Error: ORDER_ID not passed.", Toast.LENGTH_LONG).show();
            tvEstimatedTime.setText("Error: Order ID not found.");
            return;
        }

        // ✅ Log for debugging
        android.util.Log.d("ConfirmOrder", "Received ORDER_ID: " + orderId);

        // ✅ Fetch order from Firestore
        fetchOrderTime();

        btnGoHome.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmOrderActivity.this, HomePage_Student.class));
            finish();
        });

        btnGoOrders.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmOrderActivity.this, HomePage_Student.class));
            // Should be HomePage_Student (not fragment)
            finish();
        });
}

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage_Student.class); // Change if needed
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close current activity
    }


    private void fetchOrderTime() {
        DocumentReference orderRef = db.collection("orders").document(orderId);

        android.util.Log.d("ConfirmOrder", "Fetching order: " + orderId);

        orderRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Timestamp createdAt = documentSnapshot.getTimestamp("createdAt");
                if (createdAt != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(createdAt.toDate());
                    calendar.add(Calendar.HOUR, 1);

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault());
                    String estimatedTime = sdf.format(calendar.getTime());

                    tvEstimatedTime.setText("Estimated Delivery Time: " + estimatedTime);
                } else {
                    tvEstimatedTime.setText("Error: Order time not found.");
                }
            } else {
                android.util.Log.e("ConfirmOrder", "Order not found in Firestore");
                tvEstimatedTime.setText("Error: Order not found.");
            }
        }).addOnFailureListener(e -> {
            android.util.Log.e("ConfirmOrder", "Firestore error: " + e.getMessage());
            tvEstimatedTime.setText("Error fetching order time.");
            Toast.makeText(ConfirmOrderActivity.this, "Failed to fetch order details.", Toast.LENGTH_SHORT).show();
        });
    }

}
