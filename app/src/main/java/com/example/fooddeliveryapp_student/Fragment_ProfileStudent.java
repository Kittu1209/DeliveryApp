package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

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

        // ✅ FIXED: Cast as ImageButton instead of Button
        ImageButton logoutButton = view.findViewById(R.id.logout_button_stu);

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Intent intent = new Intent(getActivity(), LoginPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Logout failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

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

    private void fetchUserData() {
        if (userRef != null) {
            userRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("stuname");
                                String email = documentSnapshot.getString("stuemail");
                                String phone = documentSnapshot.getString("stuphno");
                                String studentId = documentSnapshot.getString("stuid");

                                nameEditText.setText(name);
                                emailEditText.setText(email);
                                phoneEditText.setText(phone);
                                idEditText.setText(studentId);
                            }
                        }
                    });
        }
    }

    private void enableEditing() {
        nameEditText.setFocusableInTouchMode(true);
        idEditText.setFocusableInTouchMode(true);
        phoneEditText.setFocusableInTouchMode(true);
        emailEditText.setFocusableInTouchMode(true);
        saveButton.setVisibility(View.VISIBLE);
    }

    private void saveUpdatedData() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String studentId = idEditText.getText().toString().trim();

        // Validations
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            return;
        }

        if (TextUtils.isEmpty(phone) || !phone.matches("^[+]?[0-9]{10,13}$")) {  // Basic phone validation
            phoneEditText.setError("Enter a valid phone number");
            return;
        }

        if (TextUtils.isEmpty(studentId) || !studentId.matches("^[A-Z]{5}[0-9]{5}$")) {
            idEditText.setError("Student ID must be 5 uppercase letters followed by 5 digits");
            return;
        }

        // If all validations pass, proceed to save data
        RegisterModelStudent updatedStudent = new RegisterModelStudent(name, studentId, email, phone, "Student");

        userRef.set(updatedStudent, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        disableEditing();
                    } else {
                        Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void disableEditing() {
        nameEditText.setFocusable(false);
        emailEditText.setFocusable(false);
        phoneEditText.setFocusable(false);
        idEditText.setFocusable(false);
        saveButton.setVisibility(View.GONE);
    }
}
