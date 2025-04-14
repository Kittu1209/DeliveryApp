package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Contactus_Fragment_Vendor extends Fragment {

    private EditText etName, etEmail, etSubject, etMessage;
    private Button btnSubmit;

    private FirebaseFirestore firestore;

    public Contactus_Fragment_Vendor() {
        // Required empty public constructor
    }

    public static Contactus_Fragment_Vendor newInstance(String param1, String param2) {
        Contactus_Fragment_Vendor fragment = new Contactus_Fragment_Vendor();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance(); // Initialize Firestore

        if (getArguments() != null) {
            // Retrieve any passed arguments if needed
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactus___vendor, container, false);

        // Initialize UI components
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etSubject = view.findViewById(R.id.etSubject);
        etMessage = view.findViewById(R.id.etMessage);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> submitContactForm());

        return view;
    }

    private void submitContactForm() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(subject) || TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create data map
        Map<String, Object> contactData = new HashMap<>();
        contactData.put("name", name);
        contactData.put("email", email);
        contactData.put("subject", subject);
        contactData.put("message", message);
        contactData.put("timestamp", System.currentTimeMillis());

        // Save to Firestore → Feedback > [docID] > Vendor_Feedback
        DocumentReference feedbackDocRef = firestore.collection("Feedback").document(); // Auto ID
        CollectionReference vendorFeedbackRef = feedbackDocRef.collection("Vendor_Feedback");

        vendorFeedbackRef.add(contactData)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Message Sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
