package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Two_Factor_Authentication extends AppCompatActivity {

    private Switch switch2FA;
    private EditText etVerificationCode;
    private Button btnVerifyCode, btnBack;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String userId, generatedOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_authentication);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userId = user.getUid(); // Use user ID for storing data in Realtime Database
            dbRef = FirebaseDatabase.getInstance().getReference("Student_Registration").child(userId);
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        switch2FA = findViewById(R.id.switch_2fa);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        btnBack = findViewById(R.id.btnBack);

        // Check current 2FA status from Firebase
        dbRef.child("2FA_Enabled").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                boolean is2FAEnabled = Boolean.TRUE.equals(task.getResult().getValue(Boolean.class));
                switch2FA.setChecked(is2FAEnabled);
            }
        });

        switch2FA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sendVerificationEmail();
                etVerificationCode.setVisibility(View.VISIBLE);
                btnVerifyCode.setVisibility(View.VISIBLE);
            } else {
                disable2FA();
            }
        });

        btnVerifyCode.setOnClickListener(v -> verifyCode());

        btnBack.setOnClickListener(v -> finish());
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "User email not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a 6-digit random OTP
        generatedOTP = String.valueOf(new Random().nextInt(900000) + 100000);

        // Store OTP in Firebase Realtime Database
        Map<String, Object> otpData = new HashMap<>();
        otpData.put("OTP", generatedOTP);
        otpData.put("Timestamp", System.currentTimeMillis()); // Store time to expire OTP later

        dbRef.child("2FA_OTP").setValue(otpData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "OTP sent to your email!", Toast.LENGTH_SHORT).show();

                    // Simulating email sending (Replace with Firebase Functions for real email sending)
                    System.out.println("OTP sent to email: " + user.getEmail() + " Code: " + generatedOTP);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show());
    }

    private void verifyCode() {
        String enteredOTP = etVerificationCode.getText().toString().trim();

        if (TextUtils.isEmpty(enteredOTP)) {
            Toast.makeText(this, "Enter the OTP!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve OTP from Firebase Database
        dbRef.child("2FA_OTP").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String storedOTP = task.getResult().child("OTP").getValue(String.class);

                if (enteredOTP.equals(storedOTP)) {
                    enable2FA();
                } else {
                    Toast.makeText(this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "OTP Expired or Not Found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enable2FA() {
        dbRef.child("2FA_Enabled").setValue(true)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "2FA Enabled Successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error enabling 2FA", Toast.LENGTH_SHORT).show());
    }

    private void disable2FA() {
        dbRef.child("2FA_Enabled").setValue(false)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "2FA Disabled", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error disabling 2FA", Toast.LENGTH_SHORT).show());
    }
}
