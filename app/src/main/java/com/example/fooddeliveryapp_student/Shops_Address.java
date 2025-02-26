package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Shops_Address extends AppCompatActivity {
    TextView name_vendor;
    EditText address_vendor;
    Button save_address_btn, update_address_btn;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String ownerId;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shops_address);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name_vendor = findViewById(R.id.name_shops_add);
        address_vendor = findViewById(R.id.address_vendor);
        save_address_btn = findViewById(R.id.save_address_vendor);
        update_address_btn = findViewById(R.id.update_address_vendor);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            ownerId = user.getUid();

            // Fetch vendor name
            db.collection("Vendors").document(ownerId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("vendorName");
                    if (userName != null) {
                        name_vendor.setText(userName);
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show());

            // Fetch shop address
            fetchShopAddress();
        }

        save_address_btn.setOnClickListener(v -> saveOrUpdateAddress());
        update_address_btn.setOnClickListener(v -> enableEditing());
    }

    private void fetchShopAddress() {
        DocumentReference shopRef = db.collection("shops").document(ownerId);
        shopRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("address")) {
                String address = documentSnapshot.getString("address");
                address_vendor.setText(address);
                address_vendor.setEnabled(false); // Initially disable editing

                save_address_btn.setVisibility(View.GONE); // Hide save button
                update_address_btn.setVisibility(View.VISIBLE); // Show update button
            } else {
                save_address_btn.setVisibility(View.VISIBLE); // Show save button
                update_address_btn.setVisibility(View.GONE); // Hide update button
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load address", Toast.LENGTH_SHORT).show());
    }

    private void enableEditing() {
        address_vendor.setEnabled(true);
        save_address_btn.setVisibility(View.VISIBLE);
        update_address_btn.setVisibility(View.GONE);
    }

    private void saveOrUpdateAddress() {
        String name = name_vendor.getText().toString().trim();
        String address = address_vendor.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> shopData = new HashMap<>();
        shopData.put("name", name);
        shopData.put("address", address);
        shopData.put("ownerId", ownerId);
        shopData.put("createdAt", com.google.firebase.Timestamp.now());

        DocumentReference shopRef = db.collection("shops").document(ownerId);
        shopRef.set(shopData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Shop address saved!", Toast.LENGTH_SHORT).show();
                    address_vendor.setEnabled(false);
                    save_address_btn.setVisibility(View.GONE);
                    update_address_btn.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save address", Toast.LENGTH_SHORT).show());
    }
}
