package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Admin_RegisterDeliveryMen extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etPassword, etDL, etAddress;
    Button btnRegister;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register_delivery_men);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etDL = findViewById(R.id.et_driving_license);
        etAddress = findViewById(R.id.text_address);
        btnRegister = findViewById(R.id.btn_register);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> registerDeliveryMan());
    }

    private void registerDeliveryMan() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String dl = etDL.getText().toString().trim().toUpperCase();
        String address = etAddress.getText().toString().trim();

        if (!isValidName(name)) {
            showToast("Enter a valid name (only letters, min 3 chars)");
            return;
        }

        if (!isValidPhone(phone)) {
            showToast("Enter valid 10-digit phone starting with 6/7/8/9");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter valid email address");
            return;
        }

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters");
            return;
        }

        if (address.length() < 4) {
            showToast("Address must be at least 4 characters");
            return;
        }

        if (!isValidDL(dl)) {
            showToast("Enter valid 16-character Driving License No.");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String delManId = authResult.getUser().getUid();

                    Map<String, Object> deliveryMan = new HashMap<>();
                    deliveryMan.put("name", name);
                    deliveryMan.put("phone", phone);
                    deliveryMan.put("email", email);
                    deliveryMan.put("del_man_id", delManId);
                    deliveryMan.put("del_men_address", address);
                    deliveryMan.put("current_duty", "Not Available");
                    deliveryMan.put("driving_license_no", dl);
                    deliveryMan.put("admin_control", "block");
                    deliveryMan.put("created_at", Timestamp.now());
                    deliveryMan.put("updated_at", Timestamp.now());

                    db.collection("delivery_man").document(delManId).set(deliveryMan)
                            .addOnSuccessListener(unused -> {
                                showToast("Registered Successfully");
                                clearFields();
                            })
                            .addOnFailureListener(e -> showToast("Firestore Error: " + e.getMessage()));
                })
                .addOnFailureListener(e -> showToast("Auth Error: " + e.getMessage()));
    }

    private boolean isValidName(String name) {
        return name.matches("^[A-Za-z ]{3,}$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^[6-9]\\d{9}$");
    }

    private boolean isValidDL(String dl) {
        return dl.matches("^[A-Z]{2}[0-9]{2}[0-9]{11}$");
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void clearFields() {
        etName.setText("");
        etPhone.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etAddress.setText("");
        etDL.setText("");
    }
}
