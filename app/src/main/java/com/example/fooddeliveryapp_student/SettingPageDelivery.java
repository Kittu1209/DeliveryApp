package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SettingPageDelivery extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_page_delivery);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views and set click listeners
        findViewById(R.id.cardmyprofile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeliveryProfileActivity();
            }
        });

        findViewById(R.id.cardfaq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeliveryFAQActivity();
            }
        });
        findViewById(R.id.cardcontactus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeliveryContactus();
            }
        });

        findViewById(R.id.cardchnagepassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangePasswordActivity();
            }
        });
        findViewById(R.id.cardlogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    // Method to open Delivery Profile Activity
    private void openDeliveryProfileActivity() {
        Intent intent = new Intent(SettingPageDelivery.this, DeliveryProfileActivity.class);
        startActivity(intent);
    }

    // Method to open Delivery FAQ Activity
    private void openDeliveryFAQActivity() {
        Intent intent = new Intent(SettingPageDelivery.this, DeliveryFAQ.class);
        startActivity(intent);
    }

    private void openDeliveryContactus() {
        startActivity(new Intent(this, DeliveryContactus.class));
    }
    // Method to open Change Password Activity
    private void openChangePasswordActivity() {
        Intent intent = new Intent(SettingPageDelivery.this, ChangePasswordActivityStudent.class);
        startActivity(intent);
    }
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        Intent intent = new Intent(SettingPageDelivery.this, LoginPage.class); // or your actual login page
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish(); // Finish this activity
    }
}
