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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegistrationPage extends AppCompatActivity {

    private FirebaseAuth mauth;
    private FirebaseFirestore firestoreDB;
    private ProgressBar progressBar;
    private EditText email, name, phone, password, userId, shopNameInput;
    private RadioGroup userTypeGroup;
    private TextView reg_del_men_text;
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

        // Initialize Firebase components
        mauth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();

        // Check if user is already logged in
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            redirectUser(currentUser.getUid());
            return;
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        email = findViewById(R.id.REmail);
        name = findViewById(R.id.RName);
        phone = findViewById(R.id.Phoneno);
        password = findViewById(R.id.RPassword);
        userId = findViewById(R.id.RId);
        shopNameInput = findViewById(R.id.shopName);
        userTypeGroup = findViewById(R.id.userTypeGroup);
        radioStudent = findViewById(R.id.radioStudent);
        radioVendor = findViewById(R.id.radioVendor);
        progressBar = findViewById(R.id.progressBar);
        reg_del_men_text = findViewById(R.id.register_del_men_text);
        regButton = findViewById(R.id.RRegisterButton);
        loginButton = findViewById(R.id.RLoginButton);
    }

    private void setupListeners() {
        reg_del_men_text.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationPage.this, Admin_RegisterDeliveryMen.class));
        });

        loginButton.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationPage.this, LoginPage.class));
            finish();
        });

        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            shopNameInput.setVisibility(checkedId == R.id.radioVendor ? View.VISIBLE : View.GONE);
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

        final String userType = radioStudent.isChecked() ? "Student" : "Vendor";
        final String shopName = radioVendor.isChecked() ? shopNameInput.getText().toString().trim() : "";

        if (!validateInputs(userEmail, userName, userPhone, userPassword, userUniqueId, userType, shopName)) {
            progressBar.setVisibility(View.GONE);
            return;
        }

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

    private boolean validateInputs(String email, String name, String phone,
                                   String password, String uniqueId,
                                   String userType, String shopName) {
        if (TextUtils.isEmpty(uniqueId)) {
            showToast("Enter a unique ID");
            userId.requestFocus();
            return false;
        }
        if (!uniqueId.matches("^[A-Z]{5}\\d{5}$")) {
            showToast("ID must be 10 characters: First 5 uppercase letters, last 5 digits");
            userId.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            showToast("Enter Name");
            this.name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            showToast("Enter Phone Number");
            this.phone.requestFocus();
            return false;
        }
        if (!phone.matches("^\\d{10}$")) {
            showToast("Phone number must be exactly 10 digits");
            this.phone.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid Email Address");
            this.email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("Enter Password");
            this.password.requestFocus();
            return false;
        }
        if (!isValidPassword(password)) {
            showToast("Password must be at least 6 characters with 1 uppercase, 1 digit, and 1 special character");
            this.password.requestFocus();
            return false;
        }
        if (userType.equals("Vendor") && TextUtils.isEmpty(shopName)) {
            showToast("Enter Shop Name");
            shopNameInput.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }

    private void saveUserToFirestore(String uid, String uniqueId, String name,
                                     String email, String phone,
                                     String userType, String shopName) {
        Map<String, Object> user = new HashMap<>();
        Date currentDate = new Date();

        if (userType.equals("Student")) {
            user.put("stuemail", email);
            user.put("stuid", uniqueId);
            user.put("stuname", name);
            user.put("stuphno", phone);
            user.put("userType", "Student");
            user.put("createdAt", currentDate);
            user.put("updatedAt", currentDate);

            firestoreDB.collection("Students").document(uid).set(user)
                    .addOnSuccessListener(aVoid -> {
                        showToast("Student registered successfully");
                        clearFields();
                        startActivity(new Intent(RegistrationPage.this, LoginPage.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        showToast("Error saving student data: " + e.getMessage());
                        // Delete user if Firestore fails
                        mauth.getCurrentUser().delete();
                    });

        } else if (userType.equals("Vendor")) {
            user.put("vendorEmail", email);
            user.put("vendorId", uniqueId);
            user.put("vendorName", name);
            user.put("vendorPhone", phone);
            user.put("userType", "Vendor");
            user.put("shopName", shopName);
            user.put("shopAddress", "");
            user.put("createdAt", currentDate);
            user.put("updatedAt", currentDate);

            firestoreDB.collection("Vendors").document(uid).set(user)
                    .addOnSuccessListener(aVoid -> {
                        showToast("Vendor registered successfully");
                        clearFields();
                        Intent intent = new Intent(RegistrationPage.this, Shops_Address.class);
                        intent.putExtra("vendorId", uid);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        showToast("Error saving vendor data: " + e.getMessage());
                        // Delete user if Firestore fails
                        mauth.getCurrentUser().delete();
                    });
        }
    }

    private void redirectUser(String uid) {
        // Check if user is student
        firestoreDB.collection("Students").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        startActivity(new Intent(this, LoginPage.class));
                        finish();
                    } else {
                        // Check if user is vendor
                        firestoreDB.collection("Vendors").document(uid).get()
                                .addOnSuccessListener(vendorSnapshot -> {
                                    if (vendorSnapshot.exists()) {
                                        Intent intent = new Intent(this, Shops_Address.class);
                                        intent.putExtra("vendorId", uid);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // User not found in either collection
                                        mauth.signOut();
                                    }
                                });
                    }
                });
    }

    private void clearFields() {
        email.setText("");
        name.setText("");
        phone.setText("");
        password.setText("");
        userId.setText("");
        shopNameInput.setText("");
        userTypeGroup.clearCheck();
        shopNameInput.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(RegistrationPage.this, message, Toast.LENGTH_SHORT).show();
    }
}