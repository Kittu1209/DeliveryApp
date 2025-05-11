package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Admin_AccountDetail extends AppCompatActivity {

    private CardView cardProfile, cardAddCategory, cardUpdateCategory, cardPayments, cardSettings, cardLogout, cardAddAdmin;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize CardViews
        cardProfile = findViewById(R.id.card_profile);
        cardAddCategory = findViewById(R.id.card_add_category);
        cardUpdateCategory = findViewById(R.id.card_update_category);
        cardPayments = findViewById(R.id.card_payments);
        cardSettings = findViewById(R.id.card_settings);
        cardLogout = findViewById(R.id.card_logout);
        cardAddAdmin = findViewById(R.id.card_add_admin);

        // Check user type and show/hide Add Admin card
        checkAdminType();

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

        // Add Admin functionality
        cardAddAdmin.setOnClickListener(v ->
                startActivity(new Intent(Admin_AccountDetail.this, RegisterActivityAdmin.class))
        );

        // Logout functionality
        cardLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Admin_AccountDetail.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_AccountDetail.this, LoginPage.class));
            finish(); // Optional: finish current activity
        });
    }

    private void checkAdminType() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Admins").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userType = document.getString("userType");
                            if (userType != null && userType.equals("SuperAdmin")) {
                                cardAddAdmin.setVisibility(View.VISIBLE);
                                // Update constraint for logout card to be below add admin card
                                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) cardLogout.getLayoutParams();
                                params.topToBottom = R.id.card_add_admin;
                                cardLogout.setLayoutParams(params);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error checking admin type", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}