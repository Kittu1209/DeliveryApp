package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfirmOrderActivity extends AppCompatActivity {

    private TextView tvEstimatedTime, tvOtp;
    private Button btnGoHome, btnGoOrders;
    private FirebaseFirestore db;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        tvEstimatedTime = findViewById(R.id.tv_estimated_time);
        tvOtp = findViewById(R.id.tv_otp); // 🔴 New TextView for OTP
        btnGoHome = findViewById(R.id.btn_go_home);
        btnGoOrders = findViewById(R.id.btn_go_orders);
        db = FirebaseFirestore.getInstance();

        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId == null || orderId.trim().isEmpty()) {
            Toast.makeText(this, "Error: ORDER_ID not passed.", Toast.LENGTH_LONG).show();
            tvEstimatedTime.setText("Error: Order ID not found.");
            return;
        }

        Log.d("ConfirmOrder", "Received ORDER_ID: " + orderId);

        fetchOrderTime();
        generateAndStoreOtp(orderId);      // 🔴 Generate and save OTP
        fetchOtpFromFirestore(orderId);    // 🔴 Display OTP to student

        btnGoHome.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmOrderActivity.this, HomePage_Student.class));
            finish();
        });

        btnGoOrders.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmOrderActivity.this, HomePage_Student.class));
            finish();
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage_Student.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchOrderTime() {
        DocumentReference orderRef = db.collection("orders").document(orderId);

        Log.d("ConfirmOrder", "Fetching order: " + orderId);

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
                Log.e("ConfirmOrder", "Order not found in Firestore");
                tvEstimatedTime.setText("Error: Order not found.");
            }
        }).addOnFailureListener(e -> {
            Log.e("ConfirmOrder", "Firestore error: " + e.getMessage());
            tvEstimatedTime.setText("Error fetching order time.");
            Toast.makeText(ConfirmOrderActivity.this, "Failed to fetch order details.", Toast.LENGTH_SHORT).show();
        });
    }

    private void generateAndStoreOtp(String orderId) {
        String otp = String.valueOf(new SecureRandom().nextInt(9000) + 1000); // 4-digit OTP

        Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        data.put("timestamp", Timestamp.now());

        db.collection("delivery_otp").document(orderId)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("ConfirmOrder", "OTP stored: " + otp))
                .addOnFailureListener(e -> Log.e("ConfirmOrder", "Failed to store OTP: " + e.getMessage()));
    }

    private void fetchOtpFromFirestore(String orderId) {
        db.collection("delivery_otp").document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String otp = documentSnapshot.getString("otp");
                        if (otp != null) {
                            tvOtp.setText("OTP for Delivery: " + otp);
                        } else {
                            tvOtp.setText("OTP: Not available.");
                        }
                    } else {
                        tvOtp.setText("OTP: Not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    tvOtp.setText("Failed to load OTP.");
                    Log.e("ConfirmOrder", "Failed to fetch OTP: " + e.getMessage());
                });
    }
}
