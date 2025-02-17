package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Fragment_ProfileStudent extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, idEditText;
    private Button changePasswordButton, editProfileButton, saveButton;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference userRef;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__profile_student, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            // Get reference to the student's document in Firestore
            userRef = firestore.collection("Students").document(user.getUid());
        }

        // Get UI elements
        nameEditText = view.findViewById(R.id.profile_name);
        emailEditText = view.findViewById(R.id.profile_email);
        phoneEditText = view.findViewById(R.id.profile_phone);
        idEditText = view.findViewById(R.id.profile_id);
        changePasswordButton = view.findViewById(R.id.change_password_button);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        saveButton = view.findViewById(R.id.save_button);

        // Disable editing initially
        disableEditing();

        // Fetch user data from Firestore
        fetchUserData();

        // Edit Profile button click
        editProfileButton.setOnClickListener(v -> enableEditing());

        // Save Profile button click
        saveButton.setOnClickListener(v -> saveUpdatedData());

        // Change Password button click
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivityStudent.class);
            startActivity(intent);
        });

        return view;
    }

    // Fetch user data from Firestore
    private void fetchUserData() {
        if (userRef != null) {
            userRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                // Retrieve data from Firestore and set it in the EditTexts
                                String name = documentSnapshot.getString("stuname");
                                String email = documentSnapshot.getString("stuemail");
                                String phone = documentSnapshot.getString("stuphno");
                                String studentId = documentSnapshot.getString("stuid");
                                String userType = documentSnapshot.getString("userType");

                                // Logging for debugging
                                Log.d("Firestore", "Name: " + name + ", Email: " + email + ", Phone: " + phone + ", ID: " + studentId + ", UserType: " + userType);

                                // Set the data into the EditTexts
                                nameEditText.setText(name);
                                emailEditText.setText(email);
                                phoneEditText.setText(phone);
                                idEditText.setText(studentId);
                            }
                        } else {
                            Log.e("Firestore", "Error fetching document", task.getException());
                        }
                    });
        }
    }

    // Enable editing
    private void enableEditing() {
        nameEditText.setFocusableInTouchMode(true);
        idEditText.setFocusableInTouchMode(true);
        phoneEditText.setFocusableInTouchMode(true);
        emailEditText.setFocusableInTouchMode(true);
        saveButton.setVisibility(View.VISIBLE);
    }

    // Save updated data to Firestore
    private void saveUpdatedData() {
        if (userRef != null) {
            // Get values from the EditTexts
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String studentId = idEditText.getText().toString();

            // Create a map of the updated data
            if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !studentId.isEmpty()) {
                RegisterModelStudent updatedStudent = new RegisterModelStudent(name, studentId, email, phone, "Student");

                userRef.set(updatedStudent, SetOptions.merge())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                // Disable editing and hide save button
                                disableEditing();
                            } else {
                                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Disable editing initially
    private void disableEditing() {
        nameEditText.setFocusable(false);
        emailEditText.setFocusable(false);
        phoneEditText.setFocusable(false);
        idEditText.setFocusable(false);
        saveButton.setVisibility(View.GONE);
    }
}
