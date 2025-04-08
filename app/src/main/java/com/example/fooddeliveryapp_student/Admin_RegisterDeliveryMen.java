package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    EditText etName, etPhone, etEmail, etPassword;
    Button btnRegister;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register_delivery_men);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
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

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
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
                    deliveryMan.put("current_duty", "Not Available");
                    deliveryMan.put("driving_license_no", ""); // Empty initially
                    deliveryMan.put("admin_control", "active");
                    deliveryMan.put("created_at", Timestamp.now());
                    deliveryMan.put("updated_at", Timestamp.now());

                    db.collection("delivery_man")
                            .document(delManId)
                            .set(deliveryMan)
                            .addOnSuccessListener(unused -> {
                                sendEmailToDeliveryMan(email, name, password);
                                Toast.makeText(this, "Registered & Email Sent!", Toast.LENGTH_SHORT).show();
                                clearFields();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Auth Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        etName.setText("");
        etPhone.setText("");
        etEmail.setText("");
        etPassword.setText("");
    }

    private void sendEmailToDeliveryMan(String email, String name, String password) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822"); // only email apps are shown

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Delivery Account Details");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello " + name + ",\n\n" +
                "Your delivery partner account has been created.\n\n" +
                "Login Details:\n" +
                "Email: " + email + "\n" +
                "Password: " + password + "\n\n" +
                "Please login and complete your profile.\n\n- Admin\n DoorStep-Campus Delivery app.");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send account details email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed on device.", Toast.LENGTH_SHORT).show();
        }
    }

}
