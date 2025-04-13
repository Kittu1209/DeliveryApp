package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.cardview.widget.CardView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Admin_DeliveryDetails extends AppCompatActivity {

    CardView cardRegisterDeliveryMen, cardDeliveryStaffDetails, cardDeliveredOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_delivery_details);

        // Make sure the ScrollView in XML has this ID
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_deliverydetails), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize CardViews
        cardRegisterDeliveryMen = findViewById(R.id.cardRegisterDeliveryMen);
        cardDeliveryStaffDetails = findViewById(R.id.cardDeliveryStaffDetails);
        cardDeliveredOrders = findViewById(R.id.cardDeliveredOrders);

        // Set click listeners
        cardRegisterDeliveryMen.setOnClickListener(view -> {
            startActivity(new Intent(Admin_DeliveryDetails.this, Admin_ActiveDelMan.class));
        });

        cardDeliveryStaffDetails.setOnClickListener(view -> {
            startActivity(new Intent(Admin_DeliveryDetails.this, Admin_ViewStaffDetails.class));
        });

        cardDeliveredOrders.setOnClickListener(view -> {
            startActivity(new Intent(Admin_DeliveryDetails.this, Admin_DeliveryOrders.class));
        });
    }
}
