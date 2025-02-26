package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    private EditText email_lg, pass_lg;
    private Button login_btn, register_btn;
    private TextView forgotPassword;
    private FirebaseAuth authProfile;
    private FirebaseFirestore firestoreDB;

    private static final String TAG = "LoginPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Initialize UI elements
        email_lg = findViewById(R.id.Username);
        pass_lg = findViewById(R.id.Password);
        login_btn = findViewById(R.id.Loginbutton);
        register_btn = findViewById(R.id.Registerbutton);
        forgotPassword = findViewById(R.id.ForgotPassword);

        authProfile = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();

        login_btn.setOnClickListener(view -> loginUser());
        register_btn.setOnClickListener(v -> navigateTo(RegistrationPage.class, "Redirecting to Registration Page"));
        forgotPassword.setOnClickListener(v -> navigateTo(ForgetPassword.class, "Redirecting to Password Recovery"));
    }

    private void loginUser() {
        String email = email_lg.getText().toString().trim();
        String password = pass_lg.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        authProfile.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && authProfile.getCurrentUser() != null) {
                        Log.d(TAG, "Login successful, checking user role...");
                        checkUserRole(authProfile.getCurrentUser().getUid());
                    } else {
                        handleLoginError(task);
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            email_lg.setError("Email is required");
            email_lg.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_lg.setError("Enter a valid email");
            email_lg.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            pass_lg.setError("Password is required");
            pass_lg.requestFocus();
            return false;
        }
        return true;
    }

    private void checkUserRole(String userId) {
        firestoreDB.collection("Admins").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        navigateTo(HomePageAdmin.class, "Welcome Admin!");
                    } else {
                        checkStudentRole(userId);
                    }
                });
    }

    private void checkStudentRole(String userId) {
        firestoreDB.collection("Students").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        navigateTo(HomePage_Student.class, "Welcome Student!");
                    } else {
                        checkVendorRole(userId);
                    }
                });
    }

    private void checkVendorRole(String userId) {
        firestoreDB.collection("Vendors").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        navigateTo(HomePageVendor.class, "Welcome Vendor!");
                    } else {
                        showToast("Access Denied! Role not recognized");
                        authProfile.signOut();
                    }
                });
    }

    private void handleLoginError(@NonNull Task<AuthResult> task) {
        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
            showToast("No account found with this email");
        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
            showToast("Invalid email or password");
        } else {
            showToast("Login Failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
        }
    }

    private void navigateTo(Class<?> targetActivity, String message) {
        Intent intent = new Intent(LoginPage.this, targetActivity);
        startActivity(intent);
        showToast(message);
        finish();  // Close the login activity
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
