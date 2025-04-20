package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

        fetchFeedbacks();
    }

    private void fetchFeedbacks() {
        firestore.collection("Feedback")  // Get documents in 'Feedback' collection
                .get()
                .addOnSuccessListener(feedbackDocs -> {
                    if (!feedbackDocs.isEmpty()) {
                        for (DocumentSnapshot feedbackDoc : feedbackDocs) {
                            Log.d("FeedbackDoc", "Feedback Doc ID: " + feedbackDoc.getId());

                            // Access the 'Student_Feedback' subcollection for each feedback document
                            feedbackDoc.getReference().collection("Student_Feedback")
                                    .get()
                                    .addOnSuccessListener(querySnapshots -> {
                                        if (!querySnapshots.isEmpty()) {
                                            for (DocumentSnapshot snapshot : querySnapshots) {
                                                // Convert each snapshot to a StudentFeedbackModel
                                                VendorFeedbackModel feedback = snapshot.toObject(VendorFeedbackModel.class);

                                                // Add the feedback to the list
                                                feedbackList.add(feedback);
                                            }
                                            // Notify the adapter to update the RecyclerView
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Log.d("Student_Feedback", "No feedback found in Student_Feedback collection.");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreError", "Error fetching Student_Feedback: " + e.getMessage());
                                        Toast.makeText(this, "Error fetching feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.d("Feedback", "No feedback documents found in Feedback collection.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching Feedback collection: " + e.getMessage());
                    Toast.makeText(this, "Error fetching feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
