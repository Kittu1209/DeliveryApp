package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Admin_AccountDetail extends AppCompatActivity {

    private CardView cardProfile, cardAddCategory, cardUpdateCategory, cardPayments, cardSettings, cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_account_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize CardViews
        cardProfile = findViewById(R.id.card_profile);
        cardAddCategory = findViewById(R.id.card_add_category);
        cardUpdateCategory = findViewById(R.id.card_update_category);
        cardPayments = findViewById(R.id.card_payments);
        cardSettings = findViewById(R.id.card_settings);
        cardLogout = findViewById(R.id.card_logout);

        // Redirect to Admin Profile Page
        cardProfile.setOnClickListener(v ->
                startActivity(new Intent(Admin_AccountDetail.this, Admin_Profilepage.class))
        );

        // Redirect to Add Category
        cardAddCategory.setOnClickListener(v ->
                startActivity(new Intent(Admin_AccountDetail.this, AddCategoryActivity.class))
        );

        // Redirect to Update Category
        cardUpdateCategory.setOnClickListener(v ->
                startActivity(new Intent(Admin_AccountDetail.this, Admin_UpdateCategory.class))
        );

        // Redirect to Payments
        cardPayments.setOnClickListener(v ->
                startActivity(new Intent(Admin_AccountDetail.this, Admin_Payment.class))
        );

        // Redirect to Settings
        cardSettings.setOnClickListener(v ->
                startActivity(new Intent(Admin_AccountDetail.this, Admin_Setting.class))
        );

        // Logout functionality
        cardLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Admin_AccountDetail.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_AccountDetail.this, LoginPage.class));
            finish(); // Optional: finish current activity
        });
    }
}
