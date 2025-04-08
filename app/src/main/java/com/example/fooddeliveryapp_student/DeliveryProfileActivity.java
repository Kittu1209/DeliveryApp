package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

public class DeliveryProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etDuty, etLicense;
    private Button btnSave;

    private FirebaseFirestore db;
    private String currentDeliveryManId;

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

        db = FirebaseFirestore.getInstance();
        currentDeliveryManId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadProfile();

        btnSave.setOnClickListener(view -> updateLicense());
    }

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
