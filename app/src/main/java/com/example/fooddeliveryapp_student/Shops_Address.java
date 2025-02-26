package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
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
    TextView name_vendor, address_vendor;
    Button save_address_btn;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
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

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Fetch the user's name from Firestore and set it in name_vendor
            db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("name"); // Assuming the name field is "name"
                    if (userName != null) {
                        name_vendor.setText(userName);
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show());
        }

        save_address_btn.setOnClickListener(v -> saveAddressToFirestore());
    }

    private void saveAddressToFirestore() {
        String name = name_vendor.getText().toString().trim();
        String address = address_vendor.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String ownerId = user.getUid();
            Map<String, Object> shopData = new HashMap<>();
            shopData.put("name", name);
            shopData.put("address", address);
            shopData.put("ownerId", ownerId);
            shopData.put("createdAt", com.google.firebase.Timestamp.now());

            DocumentReference shopRef = db.collection("shops").document(ownerId);
            shopRef.set(shopData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Shop address saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save address", Toast.LENGTH_SHORT).show());
        }
    }
}
