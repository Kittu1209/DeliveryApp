package com.example.fooddeliveryapp_student;

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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPage extends AppCompatActivity {

    private Button login_btn, register_btn;
    private EditText email_lg, pass_lg;
    private TextView forgotPassword;
    private FirebaseAuth authProfile;
    private DatabaseReference usersRef;

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
        forgotPassword = findViewById(R.id.ForgotPassword); // Reference Forgot Password TextView

        authProfile = FirebaseAuth.getInstance();
        usersRef= FirebaseDatabase.getInstance().getReference("Users");

        // Login Button Click
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st_email = email_lg.getText().toString().trim();
                String st_pass = pass_lg.getText().toString().trim();

                if (TextUtils.isEmpty(st_email)) {
                    email_lg.setError("Email is required");
                    email_lg.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(st_email).matches()) {
                    email_lg.setError("Enter a valid email");
                    email_lg.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(st_pass)) {
                    pass_lg.setError("Password is required");
                    pass_lg.requestFocus();
                    return;
                }

                // Firebase Login
                authProfile.signInWithEmailAndPassword(st_email, st_pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId=authProfile.getCurrentUser().getUid();
                                    checkUserRole(userId);
                                    Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(LoginPage.this, HomePage_Student.class));
                                    //finish();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                        Toast.makeText(LoginPage.this, "No account found with this email", Toast.LENGTH_LONG).show();
                                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(LoginPage.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LoginPage.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            private void checkUserRole(String userId) {
                                usersRef.child(userId).get().addOnCompleteListener(task->
                                {
                                    if(task.isSuccessful()){
                                        DataSnapshot snapshot=task.getResult();
                                        if(snapshot.exists())
                                        {
                                            String role=snapshot.child("role").getValue(String.class);
                                            if(role!=null)
                                            {
//                                                switch (role){
//                                                    case
                                                //}
                                            }
                                        }
                                    }
                                });

                            }
                        });
            }
        });

        // Register Button Click
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, RegistrationPage.class));
                Toast.makeText(LoginPage.this, "Redirecting to Registration Page", Toast.LENGTH_SHORT).show();
            }
        });

        // Forgot Password Click
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, ForgetPassword.class));
                Toast.makeText(LoginPage.this, "Redirecting to Password Recovery", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(LoginPage.this, "Already Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginPage.this, Student_Profile.class));
            finish();
        }
    }
}
