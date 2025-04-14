package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

public class DeliveryProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etDuty, etLicense;
    private Button btnSave;
    private ImageButton logout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentDeliveryManId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_profile);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etDuty = findViewById(R.id.etDuty);
        etLicense = findViewById(R.id.etLicense);
        btnSave = findViewById(R.id.btnSave);
        logout = findViewById(R.id.logout_button_delivery);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentDeliveryManId = mAuth.getCurrentUser().getUid();

        loadProfile();

        // Save button to update driving license
        btnSave.setOnClickListener(view -> updateLicense());

        // Logout button functionality
        logout.setOnClickListener(v -> {
            mAuth.signOut();

            // Check if the user is logged out successfully
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                // If the user is logged out, redirect to login activity
                Intent intent = new Intent(DeliveryProfileActivity.this, LoginPage.class); // Replace with your LoginActivity
                startActivity(intent);
                finish(); // Finish current activity to prevent going back
                Toast.makeText(DeliveryProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            } else {
                // If there's an error in logout
                Toast.makeText(DeliveryProfileActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load the profile data
    private void loadProfile() {
        db.collection("delivery_man").document(currentDeliveryManId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etName.setText(documentSnapshot.getString("name"));
                        etPhone.setText(documentSnapshot.getString("phone"));
                        etEmail.setText(documentSnapshot.getString("email"));
                        etDuty.setText(documentSnapshot.getString("current_duty"));
                        etLicense.setText(documentSnapshot.getString("driving_license_no"));
                    } else {
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show());
    }

    // Method to update the driving license number
    private void updateLicense() {
        String license = etLicense.getText().toString().trim();

        if (license.isEmpty()) {
            etLicense.setError("Enter license number");
            return;
        }

        db.collection("delivery_man").document(currentDeliveryManId)
                .update("driving_license_no", license, "updated_at", FieldValue.serverTimestamp())
                .addOnSuccessListener(unused -> Toast.makeText(this, "License updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show());
    }
}
