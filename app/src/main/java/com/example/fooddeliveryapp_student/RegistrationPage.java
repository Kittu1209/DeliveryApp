package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationPage extends AppCompatActivity {

    private FirebaseAuth mauth;
    private FirebaseFirestore firestoreDB;
    private ProgressBar progressBar;
    private EditText email, name, phone, password, userId, shopNameInput;
    private RadioGroup userTypeGroup;
    private RadioButton radioStudent, radioVendor;
    private Button regButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mauth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();

        email = findViewById(R.id.REmail);
        name = findViewById(R.id.RName);
        phone = findViewById(R.id.Phoneno);
        password = findViewById(R.id.RPassword);
        userId = findViewById(R.id.RId);
        shopNameInput = findViewById(R.id.shopName);

        userTypeGroup = findViewById(R.id.userTypeGroup);
        radioStudent = findViewById(R.id.radioStudent);
        radioVendor = findViewById(R.id.radioVendor);

        regButton = findViewById(R.id.RRegisterButton);
        loginButton = findViewById(R.id.RLoginButton);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(view -> startActivity(new Intent(RegistrationPage.this, LoginPage.class)));

        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioVendor) {
                shopNameInput.setVisibility(View.VISIBLE);
            } else {
                shopNameInput.setVisibility(View.GONE);
            }
        });

        regButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);

        final String userEmail = email.getText().toString().trim();
        final String userName = name.getText().toString().trim();
        final String userPhone = phone.getText().toString().trim();
        final String userPassword = password.getText().toString().trim();
        final String userUniqueId = userId.getText().toString().trim();
        final String userType;
        final String shopName;

        if (radioStudent.isChecked()) {
            userType = "Student";
            shopName = "";
        } else if (radioVendor.isChecked()) {
            userType = "Vendor";
            shopName = shopNameInput.getText().toString().trim();
            if (TextUtils.isEmpty(shopName)) {
                showToast("Enter Shop Name");
                progressBar.setVisibility(View.GONE);
                return;
            }
        } else {
            showToast("Select a user type");
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Validations
        if (TextUtils.isEmpty(userUniqueId)) {
            showToast("Enter a unique ID");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(userName)) {
            showToast("Enter Name");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(userPhone) || !userPhone.matches("\\d{10}")) {
            showToast("Phone number must be 10 digits");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            showToast("Enter a valid Email Address");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(userPassword) || !isValidPassword(userPassword)) {
            showToast("Password must be at least 6 characters, contain 1 uppercase letter, 1 digit, and 1 special character (@#$%^&+=!)");
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Firebase Authentication
        mauth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mauth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), userUniqueId, userName, userEmail, userPhone, userType, shopName);
                        }
                    } else {
                        showToast("Authentication Failed! " + task.getException().getMessage());
                    }
                });
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }

    private void saveUserToFirestore(String uid, String uniqueId, String name, String email, String phone, String userType, String shopName) {
        Map<String, Object> user = new HashMap<>();

        if (userType.equals("Student")) {
            user.put("stuemail", email);
            user.put("stuid", uniqueId);
            user.put("stuname", name);
            user.put("stuphno", phone);
            user.put("userType", "Student");
        } else if (userType.equals("Vendor")) {
            user.put("vendorEmail", email);
            user.put("vendorId", uniqueId);
            user.put("vendorName", name);
            user.put("vendorPhone", phone);
            user.put("userType", "Vendor");
            user.put("shopName", shopName);
        }

        String collection = userType.equals("Student") ? "Students" : "Vendors";

        firestoreDB.collection(collection).document(uid).set(user)
                .addOnSuccessListener(aVoid -> showToast(userType + " registered successfully"))
                .addOnFailureListener(e -> showToast("Error saving data: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(RegistrationPage.this, message, Toast.LENGTH_SHORT).show();
    }
}
