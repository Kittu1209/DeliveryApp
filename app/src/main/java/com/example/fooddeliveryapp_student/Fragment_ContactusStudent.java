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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Fragment_ContactusStudent extends Fragment {

    private EditText etName, etEmail, etSubject, etMessage;
    private Button btnSubmit;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    public Fragment_ContactusStudent() {
        // Required empty public constructor
    }

    public static Fragment_ContactusStudent newInstance(String param1, String param2) {
        Fragment_ContactusStudent fragment = new Fragment_ContactusStudent();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__contactus_student, container, false);

        // Initialize UI components
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etSubject = view.findViewById(R.id.etSubject);
        etMessage = view.findViewById(R.id.etMessage);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Fetch student details from Firestore
        fetchStudentDetails();

        // Submit button action
        btnSubmit.setOnClickListener(v -> submitContactForm());

        return view;
    }

    private void fetchStudentDetails() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String studentId = currentUser.getUid();

            firestore.collection("Students")
                    .document(studentId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("stuname");
                            String email = documentSnapshot.getString("stuemail");

                            etName.setText(name);
                            etEmail.setText(email);

                            // Optional: make fields read-only
                            etName.setEnabled(false);
                            etEmail.setEnabled(false);
                        } else {
                            Toast.makeText(getContext(), "Student data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch student info", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create feedback data
        Map<String, Object> contactData = new HashMap<>();
        contactData.put("name", name);
        contactData.put("email", email);
        contactData.put("subject", subject);
        contactData.put("message", message);
        contactData.put("timestamp", System.currentTimeMillis());

        // Store in Feedback collection → subcollection Student_Feedback
        DocumentReference feedbackRef = firestore.collection("Feedback").document(); // Auto-ID
        CollectionReference studentFeedbackRef = feedbackRef.collection("Student_Feedback");

        studentFeedbackRef.add(contactData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
                    etSubject.setText("");
                    etMessage.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
