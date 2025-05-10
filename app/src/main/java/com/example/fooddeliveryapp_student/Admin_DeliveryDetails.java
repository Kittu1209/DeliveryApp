package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Admin_DeliveryDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delivery_details);

        // Initialize CardViews
        CardView cardRegisterDeliveryMen = findViewById(R.id.cardRegisterDeliveryMen);
        CardView cardDeliveryStaffDetails = findViewById(R.id.cardDeliveryStaffDetails);
        CardView cardDeliveredOrders = findViewById(R.id.cardDeliveredOrders);
        CardView cardFeedback = findViewById(R.id.cardDeliveredFeedback); // Corrected ID

        // Set click listeners
        cardRegisterDeliveryMen.setOnClickListener(view -> {
            startActivity(new Intent(this, Admin_ActiveDelMan.class));
        });

        cardDeliveryStaffDetails.setOnClickListener(view -> {
            startActivity(new Intent(this, Admin_ViewStaffDetails.class));
        });

        cardDeliveredOrders.setOnClickListener(view -> {
            startActivity(new Intent(this, Admin_DeliveryOrders.class));
        });

        cardFeedback.setOnClickListener(view -> {
            startActivity(new Intent(this, AdminDeliveryFeedback.class));
        });
    }
}