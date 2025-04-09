package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Delivery_OTP extends AppCompatActivity {

    private TextView tvPhoneNumber;
    private EditText etOtp;
    private Button btnSendOtp, btnVerifyOtp;
    private String phoneNumber, generatedOtp, orderId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_otp);

        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        etOtp = findViewById(R.id.etOtp);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        db = FirebaseFirestore.getInstance();

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        orderId = getIntent().getStringExtra("orderId");

        tvPhoneNumber.setText("Phone Number: " + phoneNumber);

        btnSendOtp.setOnClickListener(v -> sendOtp());
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());
    }

    private void sendOtp() {
        generatedOtp = String.valueOf(new Random().nextInt(899999) + 100000); // 6-digit OTP

        Map<String, Object> otpData = new HashMap<>();
        otpData.put("orderId", orderId);
        otpData.put("otp", generatedOtp);
        otpData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Delivery_Otp").document(orderId)
                .set(otpData)
                .addOnSuccessListener(unused -> {
                    sendOtpToPhone(phoneNumber, generatedOtp);
                    Toast.makeText(this, "OTP sent to user", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show());
    }

    private void sendOtpToPhone(String phoneNumber, String otp) {
        // Replace this with your SMS or email sending logic
        Log.d("SEND_OTP", "Sending OTP " + otp + " to " + phoneNumber);
    }

    private void verifyOtp() {
        String enteredOtp = etOtp.getText().toString().trim();

        db.collection("Delivery_Otp").document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String correctOtp = documentSnapshot.getString("otp");
                        Timestamp otpTimestamp = documentSnapshot.getTimestamp("timestamp");

                        if (correctOtp != null && otpTimestamp != null) {
                            long otpTimeMillis = otpTimestamp.toDate().getTime();
                            long currentTimeMillis = System.currentTimeMillis();

                            // Check if OTP is within 5 minutes
                            if ((currentTimeMillis - otpTimeMillis) <= 5 * 60 * 1000) {
                                if (enteredOtp.equals(correctOtp)) {
                                    // OTP correct → update order status
                                    db.collection("orders").document(orderId)
                                            .update("status", "Delivered")
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Delivery Done", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, DeliveryHomeActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show());
                                } else {
                                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "OTP expired. Please resend.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "OTP not found. Please resend.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No OTP record found. Please send OTP first.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to verify OTP", Toast.LENGTH_SHORT).show());
    }
}
