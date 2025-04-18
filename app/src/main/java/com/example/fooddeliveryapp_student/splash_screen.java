package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class splash_screen extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000; // 2 seconds delay
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        new Handler().postDelayed(this::checkUserStatus, SPLASH_TIME);
    }

    private void checkUserStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Check if the user is a Vendor
            db.collection("Vendors").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // User is a Vendor
                        startActivity(new Intent(splash_screen.this, HomePageVendor.class));
                        finish();
                    } else {
                        // Check if the user is a Student
                        db.collection("Students").document(userId).get().addOnCompleteListener(studentTask -> {
                            if (studentTask.isSuccessful()) {
                                DocumentSnapshot studentDoc = studentTask.getResult();
                                if (studentDoc.exists()) {
                                    // User is a Student
                                    startActivity(new Intent(splash_screen.this, HomePage_Student.class));
                                } else {
                                    // User exists but not categorized, send to login
                                    startActivity(new Intent(splash_screen.this, LoginPage.class));
                                   // Toast.makeText(this, "User type not found. Please log in again.", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        });
                    }
                } else {
                    // Firestore error or no user record found
                    startActivity(new Intent(splash_screen.this, LoginPage.class));
                    finish();
                }
            });
        } else {
            // No user logged in, redirect to login screen
            startActivity(new Intent(splash_screen.this, LoginPage.class));
            finish();
        }
    }
}
