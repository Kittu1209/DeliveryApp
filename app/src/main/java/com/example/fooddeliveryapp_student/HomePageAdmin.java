package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageAdmin extends AppCompatActivity {

    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_admin);

        // Profile Icon click
        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageAdmin.this, Admin_AccountDetail.class);
            startActivity(intent);
        });

        // CardView Clicks
        findViewById(R.id.card_student).setOnClickListener(v -> {
            startActivity(new Intent(HomePageAdmin.this, Admin_StudentDetails.class));
        });

        findViewById(R.id.card_vendor).setOnClickListener(v -> {
            startActivity(new Intent(HomePageAdmin.this, Admin_VendorDetails.class));
        });

        findViewById(R.id.card_delivery).setOnClickListener(v -> {
            startActivity(new Intent(HomePageAdmin.this, Admin_DeliveryDetails.class));
        });

        findViewById(R.id.card_dashboard).setOnClickListener(v -> {
            startActivity(new Intent(HomePageAdmin.this, Admin_Dashboard.class));
        });
    }
}
