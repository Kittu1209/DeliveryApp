package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Admin_Profilepage extends AppCompatActivity {

    private EditText adminName, adminEmail, adminPhone, adminId;
    private Button btnUpdateProfile, btnSaveProfile;
    private FirebaseFirestore db;
    private String adminDocId = "gBfoYGTXU9We4oJrkadZk7Kmcte2"; // Use dynamic value if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profilepage);

        adminName = findViewById(R.id.adminName);
        adminEmail = findViewById(R.id.adminEmail);
        adminPhone = findViewById(R.id.adminPhone);
        adminId = findViewById(R.id.adminId);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        db = FirebaseFirestore.getInstance();

        loadAdminData();

        btnUpdateProfile.setOnClickListener(view -> {
            setFieldsEditable(true);
            btnUpdateProfile.setVisibility(View.GONE);
            btnSaveProfile.setVisibility(View.VISIBLE);
        });

        btnSaveProfile.setOnClickListener(view -> saveChanges());
    }

    private void loadAdminData() {
        DocumentReference docRef = db.collection("Admins").document(adminDocId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                adminName.setText(documentSnapshot.getString("name"));
                adminEmail.setText(documentSnapshot.getString("email"));
                adminPhone.setText(documentSnapshot.getString("phone_number"));
                adminId.setText(documentSnapshot.getString("admin_id"));
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }

    private void saveChanges() {
        String name = adminName.getText().toString().trim();
        String email = adminEmail.getText().toString().trim();
        String phone = adminPhone.getText().toString().trim();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", name);
        updatedData.put("email", email);
        updatedData.put("phone_number", phone);
        updatedData.put("lastUpdatedAt", Timestamp.now());

        db.collection("Admins").document(adminDocId)
                .update(updatedData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    setFieldsEditable(false);
                    btnSaveProfile.setVisibility(View.GONE);
                    btnUpdateProfile.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show());
    }

    private void setFieldsEditable(boolean editable) {
        adminName.setEnabled(editable);
        adminEmail.setEnabled(editable);
        adminPhone.setEnabled(editable);
        // ID is not editable
    }
}
