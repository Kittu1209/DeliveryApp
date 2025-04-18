package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;

public class Admin_Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        CardView cardDashboard = findViewById(R.id.cardDashboard);
        CardView cardReports = findViewById(R.id.cardReports);

        cardDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_Dashboard.this, AdminDashboardPageActivity.class);
            startActivity(intent);
        });

        cardReports.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_Dashboard.this, AdminReportsActivity.class);
            startActivity(intent);
        });
    }
}