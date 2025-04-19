package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminDeliveryFeedback extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VendorFeedbackAdapter adapter; // Reusing the same adapter
    private List<VendorFeedbackModel> feedbackList;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delivery_feedback);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerDeliveryFeedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        adapter = new VendorFeedbackAdapter(feedbackList); // Reuse adapter
        recyclerView.setAdapter(adapter);

        fetchDeliveryFeedback();
    }

    private void fetchDeliveryFeedback() {
        firestore.collection("Feedback")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        CollectionReference deliveryFeedbackRef = doc.getReference().collection("Delivery_Feedback");
                        deliveryFeedbackRef.get().addOnSuccessListener(deliverySnapshots -> {
                            for (QueryDocumentSnapshot feedbackDoc : deliverySnapshots) {
                                VendorFeedbackModel feedback = feedbackDoc.toObject(VendorFeedbackModel.class);
                                feedbackList.add(feedback);
                            }
                            adapter.notifyDataSetChanged();
                        });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
