package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class Admin_StudentFeedback extends AppCompatActivity {

    RecyclerView recyclerFeedback;
    List<StudentFeedbackModel> feedbackList;
    StudentFeedbackAdapter adapter;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_student_feedback);

        recyclerFeedback = findViewById(R.id.recyclerViewFeedback);
        feedbackList = new ArrayList<>();
        adapter = new StudentFeedbackAdapter(feedbackList);
        recyclerFeedback.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

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
                                                StudentFeedbackModel feedback = snapshot.toObject(StudentFeedbackModel.class);

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
