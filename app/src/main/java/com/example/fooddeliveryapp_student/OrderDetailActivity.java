package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView orderIdText, customerNameText, addressText, phoneText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderIdText = findViewById(R.id.orderIdText);
        customerNameText = findViewById(R.id.customerNameText);
        addressText = findViewById(R.id.addressText);
        phoneText = findViewById(R.id.phoneText);

        // Get data from Intent
        String orderId = getIntent().getStringExtra("orderId");
        String name = getIntent().getStringExtra("name");
        String hostel = getIntent().getStringExtra("hostel");
        String room = getIntent().getStringExtra("room");
        String phone = getIntent().getStringExtra("phone");

        orderIdText.setText("Order ID: " + orderId);
        customerNameText.setText("Name: " + name);
        addressText.setText("Address: " + hostel + ", Room " + room);
        phoneText.setText("Phone: " + phone);
    }
}
