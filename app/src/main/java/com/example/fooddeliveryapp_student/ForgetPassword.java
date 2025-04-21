package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private EditText emailReset;
    private Button resetPasswordButton;
    private TextView backToLogin;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        // Initialize UI elements
        emailReset = findViewById(R.id.emailReset);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backToLogin = findViewById(R.id.backToLogin);
        auth = FirebaseAuth.getInstance();

        // Reset Password Button Click
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailReset.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailReset.setError("Email is required");
                    emailReset.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailReset.setError("Enter a valid email");
                    emailReset.requestFocus();
                    return;
                }

                // Send Reset Email
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgetPassword.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgetPassword.this, LoginPage.class));
                        finish();
                    } else {
                        Toast.makeText(ForgetPassword.this, "Failed to send reset email", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Back to Login Click
        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgetPassword.this, LoginPage.class));
            finish();
        });

        // Handle Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
