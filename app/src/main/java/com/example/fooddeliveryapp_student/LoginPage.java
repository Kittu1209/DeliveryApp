package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private ImageView togglePasswordIcon;
    private boolean isPasswordVisible = false;

    private static final String TAG = "LoginPage";
    private static final String[] REQUIRED_SHOP_FIELDS = {
            "address", "description", "deliveryTime",
            "priceForTwo", "cuisine", "image", "name"
    };

    @SuppressLint("MissingInflatedId")
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
        togglePasswordIcon = findViewById(R.id.togglePasswordVisibility);

        authProfile = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();

        login_btn.setOnClickListener(view -> loginUser());
        register_btn.setOnClickListener(v -> navigateTo(RegistrationPage.class, "Redirecting to Registration Page"));
        forgotPassword.setOnClickListener(v -> navigateTo(ForgetPassword.class, "Redirecting to Password Recovery"));

        togglePasswordIcon.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            pass_lg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordIcon.setImageResource(R.drawable.baseline_block_24);
        } else {
            pass_lg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordIcon.setImageResource(R.drawable.baseline_adjust_24);
        }
        pass_lg.setSelection(pass_lg.length());
        isPasswordVisible = !isPasswordVisible;
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
        } else if (password.length() < 6) {
            pass_lg.setError("Password must be at least 6 characters");
            pass_lg.requestFocus();
            return false;
        }

        return true;
    }

    private void checkUserRole(String userId) {
        firestoreDB.collection("Admins").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        email_lg.setText("");
                        pass_lg.setText("");
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
                        checkShopStatus(userId);
                    } else {
                        checkDeliveryManRole(userId);
                    }
                });
    }

    private void checkShopStatus(String userId) {
        firestoreDB.collection("shops").document(userId).get()
                .addOnCompleteListener(shopTask -> {
                    if (shopTask.isSuccessful() && shopTask.getResult().exists()) {
                        DocumentSnapshot shopDoc = shopTask.getResult();

                        if (isShopComplete(shopDoc)) {
                            Boolean isActive = shopDoc.getBoolean("isActive");
                            if (isActive != null && isActive) {
                                // All data present and active - proceed to vendor home
                                Intent intent = new Intent(LoginPage.this, HomePageVendor.class);
                                intent.putExtra("fragment", "vendor_home");
                                startActivity(intent);
                                showToast("Welcome Vendor!");
                                finish();
                            } else {
                                showVendorAccountNotActiveDialog();
                                authProfile.signOut();
                            }
                        } else {
                            // Incomplete shop data - redirect to setup
                            Intent intent = new Intent(LoginPage.this, Shops_Address.class);
                            intent.putExtra("setup_new_shop", true);
                            startActivity(intent);
                            showToast("Please complete your shop setup");
                            finish();
                        }
                    } else {
                        // No shop exists - redirect to setup
                        Intent intent = new Intent(LoginPage.this, Shops_Address.class);
                        intent.putExtra("setup_new_shop", true);
                        startActivity(intent);
                        showToast("Please complete your shop setup");
                        finish();
                    }
                });
    }

    private boolean isShopComplete(DocumentSnapshot shopDoc) {
        for (String field : REQUIRED_SHOP_FIELDS) {
            if (!shopDoc.contains(field)){
                return false;
            }
            Object value = shopDoc.get(field);
            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                return false;
            }
        }
        return true;
    }

    private void showVendorAccountNotActiveDialog() {
        email_lg.setText("");
        pass_lg.setText("");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);

        title.setText("Account Not Active");
        message.setText("Your vendor account is not currently active. Please contact admin.");

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void checkDeliveryManRole(String userId) {
        firestoreDB.collection("delivery_man").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        String delManId = document.getString("del_man_id");
                        String adminControl = document.getString("admin_control");

                        if ("active".equalsIgnoreCase(adminControl)) {
                            if (delManId != null) {
                                getSharedPreferences("DelManPrefs", MODE_PRIVATE)
                                        .edit()
                                        .putString("del_man_id", delManId)
                                        .apply();
                            }
                            Intent intent = new Intent(LoginPage.this, DeliveryHomeActivity.class);
                            startActivity(intent);
                            showToast("Welcome Delivery Personnel!");
                            finish();
                        } else {
                            showAccountNotApprovedDialog();
                            authProfile.signOut();
                        }
                    } else {
                        showToast("Access Denied! Role not recognized");
                        authProfile.signOut();
                    }
                });
    }

    private void showAccountNotApprovedDialog() {
        email_lg.setText("");
        pass_lg.setText("");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);

        title.setText("Access Restricted");
        message.setText("Your account is under review and not yet approved by administrator.");

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
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
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}