package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Admin_StudentDetails extends AppCompatActivity {

    CardView cardOrderHistory, cardFeedback, cardStudentDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_student_details);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_admin_student_details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Cards
        cardOrderHistory = findViewById(R.id.cardOrderHistory);
        cardFeedback = findViewById(R.id.cardFeedback);
        cardStudentDetail= findViewById(R.id.carduserdetail);
        // Set Click Listeners
        cardOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_StudentDetails.this, Admin_StudentOrderHistory.class);
                startActivity(intent);
            }
        });

        cardStudentDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_StudentDetails.this, AdminViewUserDetails.class);
                startActivity(intent);
            }
        });

        cardFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_StudentDetails.this, Admin_StudentFeedback.class);
                startActivity(intent);
            }
        });
    }
}
