package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Privacy_Setting extends AppCompatActivity {

    private CardView changePassword, twoFactorAuth, managePermissions;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);

        // Initialize UI components
        changePassword = findViewById(R.id.PSchngpass);
        twoFactorAuth = findViewById(R.id.PStfauth);
        managePermissions = findViewById(R.id.PSapppermission);
        backButton = findViewById(R.id.btnBack);

        // Click Listeners for CardViews
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Change Password Activity
                Intent intent = new Intent(Privacy_Setting.this, ChangePasswordActivityStudent.class);
                startActivity(intent);
            }
        });

        twoFactorAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Two-Factor Authentication Activity
                Intent intent = new Intent(Privacy_Setting.this, Two_Factor_Authentication.class);
                startActivity(intent);
            }
        });

        managePermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Manage Permissions Activity
                Intent intent = new Intent(Privacy_Setting.this, Manage_App_permission.class);
                startActivity(intent);
            }
        });

        // Back Button Click Listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity and return to previous screen
            }
        });
    }
}
