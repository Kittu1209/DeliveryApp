package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Admin_ViewStaffDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<AdminViewStaffModel> staffList;
    AdminViewStaffAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_view_staff_details);

        recyclerView = findViewById(R.id.recyclerViewStaff);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        staffList = new ArrayList<>();
        adapter = new AdminViewStaffAdapter(staffList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadStaffFromFirestore();
    }

    private void loadStaffFromFirestore() {
        CollectionReference staffRef = db.collection("delivery_man");

        staffRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            staffList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                AdminViewStaffModel model = doc.toObject(AdminViewStaffModel.class);
                staffList.add(model);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading staff!", Toast.LENGTH_SHORT).show();
            Log.e("FIRESTORE", "Error: ", e);
        });
    }
}
