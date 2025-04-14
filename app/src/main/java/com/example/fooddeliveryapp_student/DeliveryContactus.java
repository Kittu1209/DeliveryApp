package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DeliveryContactus extends AppCompatActivity {

    private EditText etName, etEmail, etSubject, etMessage;
    private Button btnSubmit;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_contactus); // make sure this XML exists

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> submitContactForm());
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
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
