package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Admin_VendorDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vendor_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_admin_vendor_details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // View Vendor Profile
        CardView cardViewVendorProfile = findViewById(R.id.cardViewVendorProfile);
        cardViewVendorProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_VendorDetails.this, Admin_viewVendor.class);
            startActivity(intent);
        });

        // Edit Vendor Profile
        CardView cardEditVendorProfile = findViewById(R.id.cardEditVendorProfile);
        cardEditVendorProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_VendorDetails.this, Admin_editVendor.class);
            startActivity(intent);
        });

        // View Products
        CardView cardViewProducts = findViewById(R.id.cardViewProducts);
        cardViewProducts.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_VendorDetails.this, Admin_viewProducts.class);
            startActivity(intent);
        });

        // View Orders
        CardView cardViewOrders = findViewById(R.id.cardViewOrders);
        cardViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_VendorDetails.this, Admin_ViewOrders.class);
            startActivity(intent);
        });

        // Add Category
        CardView cardAddCategory = findViewById(R.id.cardAddCategory);
        cardAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_VendorDetails.this, AddCategoryActivity.class);
            startActivity(intent);
        });

        CardView cardVendorFeedback = findViewById(R.id.cardVendorFeedback);
        cardAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_VendorDetails.this, AdminVendorFeedback.class);
            startActivity(intent);
        });
    }
}
