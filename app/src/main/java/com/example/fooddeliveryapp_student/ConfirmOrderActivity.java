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

        // Retrieve orderId from Intent
        orderId = getIntent().getStringExtra("ORDER_ID");

        if (orderId != null && !orderId.isEmpty()) {
            fetchOrderTime();
        } else {
            tvEstimatedTime.setText("Error: Order ID not found.");
        }

        btnGoHome.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmOrderActivity.this, HomePage_Student.class));
            finish();
        });

        btnGoOrders.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmOrderActivity.this, Fragment_myorderStudent.class));
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
        DocumentReference orderRef = db.collection("Orders").document(orderId);
        orderRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Timestamp createdAt = documentSnapshot.getTimestamp("createdAt");
                if (createdAt != null) {
                    // Calculate estimated delivery time (1 hour later)
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(createdAt.toDate());
                    calendar.add(Calendar.HOUR, 1);

                    // Format time
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault());
                    String estimatedTime = sdf.format(calendar.getTime());

                    tvEstimatedTime.setText("Estimated Delivery Time: " + estimatedTime);
                } else {
                    tvEstimatedTime.setText("Error: Order time not found.");
                }
            } else {
                tvEstimatedTime.setText("Error: Order not found.");
            }
        }).addOnFailureListener(e -> {
            tvEstimatedTime.setText("Error fetching order time.");
            Toast.makeText(ConfirmOrderActivity.this, "Failed to fetch order details.", Toast.LENGTH_SHORT).show();
        });
    }
}
