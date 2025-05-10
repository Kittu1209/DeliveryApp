package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

        CardView deliveryCard = findViewById(R.id.card_delivery);

        if (deliveryCard == null) {
            Log.e("AdminDelivery", "CardView not found in layout");
            Toast.makeText(this, "Layout configuration error", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if critical view is missing
            return;
        }


        deliveryCard.setOnClickListener(v -> {

            startActivity(new Intent(this, Admin_DeliveryDetails.class));
        });

        findViewById(R.id.card_dashboard).setOnClickListener(v -> {
            startActivity(new Intent(HomePageAdmin.this, Admin_Dashboard.class));
        });
    }
}
