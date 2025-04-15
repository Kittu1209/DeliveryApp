package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivityStudent extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    private ImageView showOldPasswordIcon, showNewPasswordIcon, showConfirmPasswordIcon;

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

        showOldPasswordIcon = findViewById(R.id.show_old_password_icon);
        showNewPasswordIcon = findViewById(R.id.show_new_password_icon);
        showConfirmPasswordIcon = findViewById(R.id.show_confirm_password_icon);

        changePasswordButton.setOnClickListener(v -> changePassword());

        // Toggle visibility of passwords
        showOldPasswordIcon.setOnClickListener(v -> togglePasswordVisibility(oldPasswordEditText, showOldPasswordIcon));
        showNewPasswordIcon.setOnClickListener(v -> togglePasswordVisibility(newPasswordEditText, showNewPasswordIcon));
        showConfirmPasswordIcon.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordEditText, showConfirmPasswordIcon));
    }

    private void togglePasswordVisibility(EditText editText, ImageView eyeIcon) {
        int inputType = editText.getInputType();

        if ((inputType & android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ==
                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Hide password
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeIcon.setImageResource(R.drawable.baseline_adjust_24); // icon for hidden
        } else {
            // Show password
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeIcon.setImageResource(R.drawable.baseline_block_24); // icon for visible
        }

        editText.setSelection(editText.getText().length()); // keep cursor at end
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
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                if (authTask.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ChangePassword", "Password updated in Firebase Auth");
                            updatePasswordInFirestore(newPassword);
                        } else {
                            showToast("Error updating password: " + task.getException().getMessage());
                        }
                    });
                } else {
                    showToast("Authentication failed. Check your old password.");
                }
            });
        }
    }

    private void updatePasswordInFirestore(String newPassword) {
        String userEmail = user.getEmail();

        firestore.collection("Students").whereEqualTo("stuemail", userEmail).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        firestore.collection("Students").document(docId)
                                .update("password", newPassword)
                                .addOnSuccessListener(aVoid -> {
                                    showToast("Password updated successfully for Student");
                                    clearFields();
                                })
                                .addOnFailureListener(e -> showToast("Error updating password for Student"));
                    } else {
                        checkVendorOrDeliveryMan(userEmail, newPassword);
                    }
                })
                .addOnFailureListener(e -> showToast("Error fetching user data"));
    }

    private void checkVendorOrDeliveryMan(String userEmail, String newPassword) {
        firestore.collection("Vendors").whereEqualTo("vendorEmail", userEmail).get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        String docId = query.getDocuments().get(0).getId();
                        firestore.collection("Vendors").document(docId)
                                .update("password", newPassword)
                                .addOnSuccessListener(aVoid -> {
                                    showToast("Password updated successfully for Vendor");
                                    clearFields();
                                })
                                .addOnFailureListener(e -> showToast("Error updating password for Vendor"));
                    } else {
                        firestore.collection("delivery_man").whereEqualTo("email", userEmail).get()
                                .addOnSuccessListener(query2 -> {
                                    if (!query2.isEmpty()) {
                                        String docId = query2.getDocuments().get(0).getId();
                                        firestore.collection("delivery_man").document(docId)
                                                .update("password", newPassword)
                                                .addOnSuccessListener(aVoid -> {
                                                    showToast("Password updated successfully for Delivery Man");
                                                    clearFields();
                                                })
                                                .addOnFailureListener(e -> showToast("Error updating password for Delivery Man"));
                                    } else {
                                        showToast("User not found in any collection");
                                    }
                                });
                    }
                });
    }

    private boolean isValidPassword(String password) {
        String pattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(pattern);
    }

    private void showToast(String message) {
        Toast.makeText(ChangePasswordActivityStudent.this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearFields() {
        oldPasswordEditText.setText("");
        newPasswordEditText.setText("");
        confirmPasswordEditText.setText("");
    }
}
