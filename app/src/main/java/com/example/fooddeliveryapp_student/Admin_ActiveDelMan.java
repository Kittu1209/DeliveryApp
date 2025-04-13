package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Admin_ActiveDelMan extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<AdminActiveDelManModel> list;
    private AdminActiveDelManAdapter adapter;
    private FirebaseFirestore db;
    private TextView noApprovalsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_active_del_man);

        noApprovalsText = findViewById(R.id.no_approvals_text);
        recyclerView = findViewById(R.id.recyclerActiveDelMan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new AdminActiveDelManAdapter(this, list, (email, name) -> sendEmailToDeliveryMan(email, name));
        recyclerView.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();

        fetchDeliveryMen();
    }
    private void sendEmailToDeliveryMan(String email, String name) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822"); // only email apps are shown

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Delivery Account is Now Active");

        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "Dear " + name + ",\n\n" +
                        "We are pleased to inform you that your delivery partner account has been successfully activated in the 'DoorStep-Campus Delivery App'.\n\n" +
                        "To begin accepting delivery tasks, please open the app and tap the 'Available' button to set your duty status accordingly.\n\n" +
                        "If you have any questions or need assistance, feel free to reach out to our support team.\n\n" +
                        "Thank you for being a part of our team.\n\n" +
                        "Best regards,\n" +
                        "Admin Team\n" +
                        "DoorStep-Campus Delivery App");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send activation email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed on device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDeliveryMen() {
        db.collection("delivery_man")
                .whereEqualTo("admin_control", "block")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear(); // Clear previous data
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        AdminActiveDelManModel model = doc.toObject(AdminActiveDelManModel.class);
                        if (model != null) {
                           // model.setDelManId(doc.getId()); // Set document ID if needed
                            list.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    if (list.isEmpty()) {
                        noApprovalsText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noApprovalsText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show());
    }
}
