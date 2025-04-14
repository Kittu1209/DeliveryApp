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

    private EditText etOtp;
    private Button btnVerifyOtp;
    private String orderId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_otp);

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        db = FirebaseFirestore.getInstance();

        orderId = getIntent().getStringExtra("orderId");

        btnVerifyOtp.setOnClickListener(v -> verifyOtp());
    }

    private void verifyOtp() {
        String enteredOtp = etOtp.getText().toString().trim();
        if (enteredOtp.isEmpty()) {
            Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("delivery_otp").document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String correctOtp = documentSnapshot.getString("otp");

                        if (correctOtp != null) {
                            if (enteredOtp.equals(correctOtp)) {
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
                            Toast.makeText(this, "OTP not found. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No OTP record found for this order.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OTP_VERIFY", "Error verifying OTP", e);
                    Toast.makeText(this, "Error verifying OTP", Toast.LENGTH_SHORT).show();
                });

    }
}
