package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivityStudent extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_student);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        oldPasswordEditText = findViewById(R.id.old_password);
        newPasswordEditText = findViewById(R.id.new_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        changePasswordButton = findViewById(R.id.change_password_button);

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            showToast("Please enter all fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return;
        }

        if (newPassword.equals(oldPassword)) {
            showToast("Don't enter the same password again");
            return;
        }

        if (!isValidPassword(newPassword)) {
            showToast("Password must be at least 6 characters, contain 1 uppercase letter, 1 digit, and 1 special character (@#$%^&+=!)");
            return;
        }

        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updatePasswordInFirestore(newPassword); // Update in Firestore table
                } else {
                    showToast("Error updating password");
                }
            });
        }
    }

    private void updatePasswordInFirestore(String newPassword) {
        String userEmail = user.getEmail();

        // Check user type in Students
        firestore.collection("Students").whereEqualTo("email", userEmail).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        firestore.collection("Students").document(docId)
                                .update("password", newPassword)
                                .addOnSuccessListener(aVoid -> {
                                    showToast("Password updated successfully for Student");
                                    finish();
                                })
                                .addOnFailureListener(e -> showToast("Firestore update failed for Student"));
                    } else {
                        // Check Vendors
                        firestore.collection("Vendors").whereEqualTo("vendorEmail", userEmail).get()
                                .addOnSuccessListener(query -> {
                                    if (!query.isEmpty()) {
                                        String docId = query.getDocuments().get(0).getId();
                                        firestore.collection("Vendors").document(docId)
                                                .update("password", newPassword)
                                                .addOnSuccessListener(aVoid -> {
                                                    showToast("Password updated successfully for Vendor");
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> showToast("Firestore update failed for Vendor"));
                                    } else {
                                        // Check DeliveryMen
                                        firestore.collection("delivery_man").whereEqualTo("email", userEmail).get()
                                                .addOnSuccessListener(query2 -> {
                                                    if (!query2.isEmpty()) {
                                                        String docId = query2.getDocuments().get(0).getId();
                                                        firestore.collection("delivery_man").document(docId)
                                                                .update("password", newPassword)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    showToast("Password updated successfully for Delivery Man");
                                                                    finish();
                                                                })
                                                                .addOnFailureListener(e -> showToast("Firestore update failed for Delivery Man"));
                                                    } else {
                                                        showToast("User not found in any collection");
                                                    }
                                                });
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to fetch user data"));
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }

    private void showToast(String message) {
        Toast.makeText(ChangePasswordActivityStudent.this, message, Toast.LENGTH_SHORT).show();
    }
}
