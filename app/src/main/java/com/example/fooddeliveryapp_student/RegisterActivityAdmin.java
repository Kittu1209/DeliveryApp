package com.example.fooddeliveryapp_student;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivityAdmin extends AppCompatActivity {
    EditText adminid, name, pass, email, phno;
    Button reg_btn;
    FirebaseAuth mauth;
    FirebaseFirestore db;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth & Firestore
        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI Components
        adminid = findViewById(R.id.admin_id_regis_tf);
        name = findViewById(R.id.name_regis_tf);
        pass = findViewById(R.id.pass_regis_tf);
        email = findViewById(R.id.email_regis_tf);
        phno = findViewById(R.id.phno_regis_tf);
        reg_btn = findViewById(R.id.regis_regis_btn);
        progressBar = findViewById(R.id.progressbar);

//        // Navigate to Login Page
//        click_log_tv.setOnClickListener(v -> {
//            Intent intent = new Intent(RegisterActivityAdmin.this, LoginActivityAdmin.class);
//            startActivity(intent);
//            finish();
//        });

        // Handle Registration Button Click
        reg_btn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String adID = adminid.getText().toString().trim();
            String adname = name.getText().toString().trim();
            String adpass = pass.getText().toString().trim();
            String ademail = email.getText().toString().trim();
            String adphno = phno.getText().toString().trim();

            if (TextUtils.isEmpty(adID) || TextUtils.isEmpty(adname) || TextUtils.isEmpty(adpass) ||
                    TextUtils.isEmpty(ademail) || TextUtils.isEmpty(adphno)) {
                Toast.makeText(RegisterActivityAdmin.this, "All fields are required", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Register the Admin in Firebase Auth
            mauth.createUserWithEmailAndPassword(ademail, adpass).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseUser user = mauth.getCurrentUser();
                    if (user != null) {
                        saveAdminToFirestore(user.getUid(), adID, adname, ademail, adphno);
                    }
                } else {
                    Toast.makeText(RegisterActivityAdmin.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void saveAdminToFirestore(String uid, String adID, String adname, String ademail, String adphno) {
        // Creating a HashMap to store data in Firestore
        Map<String, Object> admin = new HashMap<>();
        admin.put("admin_id", adID);
        admin.put("name", adname);
        admin.put("email", ademail);
        admin.put("phone_number", adphno);
        admin.put("userType", "SuperAdmin");
        admin.put("createdAt", new Date());
        admin.put("updatedAt", new Date());

        // Save to Firestore
        db.collection("Admins").document(uid).set(admin)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivityAdmin.this, "Admin registered successfully", Toast.LENGTH_SHORT).show();
                    // Optionally redirect to login or dashboard after successful registration
//                    startActivity(new Intent(RegisterActivityAdmin.this, LoginActivityAdmin.class));
//                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivityAdmin.this, "Error saving admin: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
