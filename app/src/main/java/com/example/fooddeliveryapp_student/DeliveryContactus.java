package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DeliveryContactus extends AppCompatActivity {

    private EditText etName, etEmail, etSubject, etMessage;
    private Button btnSubmit;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_contactus); // Make sure this layout exists

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Fetch delivery person data
        fetchDeliveryPersonInfo();

        // Submit button listener
        btnSubmit.setOnClickListener(v -> submitContactForm());
    }

    private void fetchDeliveryPersonInfo() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String deliveryManId = currentUser.getUid();

            firestore.collection("delivery_man")
                    .document(deliveryManId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");

                            etName.setText(name);
                            etEmail.setText(email);

                            // Make fields read-only
                            etName.setEnabled(false);
                            etEmail.setEnabled(false);
                        } else {
                            Toast.makeText(this, "Delivery man data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to fetch delivery info", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void submitContactForm() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(subject) || TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> contactData = new HashMap<>();
        contactData.put("name", name);
        contactData.put("email", email);
        contactData.put("subject", subject);
        contactData.put("message", message);
        contactData.put("timestamp", System.currentTimeMillis());

        // Firestore path: Feedback → Auto-ID Document → Delivery_Feedback Subcollection
        DocumentReference feedbackRef = firestore.collection("Feedback").document(); // Auto-ID
        CollectionReference deliveryFeedbackRef = feedbackRef.collection("Delivery_Feedback");

        deliveryFeedbackRef.add(contactData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();
                    // Clear only subject and message
                    etSubject.setText("");
                    etMessage.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
