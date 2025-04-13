package com.example.fooddeliveryapp_student;

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
        adapter = new AdminActiveDelManAdapter(this, list);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchDeliveryMen();
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
