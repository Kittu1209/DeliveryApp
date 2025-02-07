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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationPage extends AppCompatActivity {

    private FirebaseAuth mauth;
    private ProgressBar progressBar;

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

        final EditText email = findViewById(R.id.REmail);
        final EditText sname = findViewById(R.id.RName);
        final EditText phno = findViewById(R.id.Phoneno);
        final EditText rpassword = findViewById(R.id.RPassword);
        final EditText stid = findViewById(R.id.RId);
        final RadioGroup userTypeGroup = findViewById(R.id.userTypeGroup);
        final RadioButton radioStudent = findViewById(R.id.radioStudent);
        final RadioButton radioVendor = findViewById(R.id.radioVendor);
        final EditText shopNameInput = findViewById(R.id.shopName);
        final Button regbutton = findViewById(R.id.RRegisterButton);
        final Button l_in = findViewById(R.id.RLoginButton);

        progressBar = findViewById(R.id.progressBar);

        l_in.setOnClickListener(view -> {
            Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
            startActivity(intent);
        });

        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioVendor) {
                shopNameInput.setVisibility(View.VISIBLE);
            } else {
                shopNameInput.setVisibility(View.GONE);
            }
        });

        regbutton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            final String sid = stid.getText().toString().trim();
            final String stname = sname.getText().toString().trim();
            final String sphno = phno.getText().toString().trim();
            final String semail = email.getText().toString().trim();
            final String spass = rpassword.getText().toString().trim();

            final String selectedUserType;
            final String finalShopName;

            if (radioStudent.isChecked()) {
                selectedUserType = "Student";
                finalShopName = "";  // No shop name for students
            } else if (radioVendor.isChecked()) {
                selectedUserType = "Vendor";
                finalShopName = shopNameInput.getText().toString().trim();
                if (TextUtils.isEmpty(finalShopName)) {
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
            if (TextUtils.isEmpty(sid) || !sid.matches("^[A-Z]{5}\\d{5}$")) {
                showToast("Student ID must have first 5 uppercase letters and last 5 digits");
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(stname)) {
                showToast("Enter Name");
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(sphno) || !sphno.matches("\\d{10}")) {
                showToast("Phone number must be 10 digits");
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(semail) || !Patterns.EMAIL_ADDRESS.matcher(semail).matches()) {
                showToast("Enter a valid Email Address");
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(spass) || !isValidPassword(spass)) {
                showToast("Password must be at least 6 characters, contain 1 uppercase letter, 1 digit, and 1 special character (@#$%^&+=!)");
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Create user in Firebase Authentication
            mauth.createUserWithEmailAndPassword(semail, spass)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mauth.getCurrentUser();
                            if (user != null) {
                                saveUserToFirebase(user.getUid(), sid, stname, semail, sphno, selectedUserType, finalShopName);
                            }
                        } else {
                            showToast("Authentication Failed! " + task.getException().getMessage());
                        }
                    });
        });
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }

    private void saveUserToFirebase(String uid, String sid, String stname, String semail, String sphno, String userType, String shopName) {
        DatabaseReference databaseReference;
        if ("Student".equals(userType)) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Student_Registration");
            RegisterModelStudent student = new RegisterModelStudent(sid, stname, semail, sphno, userType);
            databaseReference.child(uid).setValue(student)
                    .addOnSuccessListener(aVoid -> showToast("Student registered successfully"))
                    .addOnFailureListener(e -> showToast("Error saving student: " + e.getMessage()));
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("Vendor_Registration");
            RegisterModelVendor vendor = new RegisterModelVendor(stname, sid, semail, sphno, shopName, userType);
            databaseReference.child(uid).setValue(vendor)
                    .addOnSuccessListener(aVoid -> showToast("Vendor registered successfully"))
                    .addOnFailureListener(e -> showToast("Error saving vendor: " + e.getMessage()));
        }
    }

    private void showToast(String message) {
        Toast.makeText(RegistrationPage.this, message, Toast.LENGTH_SHORT).show();
    }
}
