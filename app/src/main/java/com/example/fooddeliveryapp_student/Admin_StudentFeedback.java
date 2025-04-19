package com.example.fooddeliveryapp_student;

import android.os.Bundle;
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
        firestore.collection("Feedback")
                .get()
                .addOnSuccessListener(feedbackDocs -> {
                    for (DocumentSnapshot feedbackDoc : feedbackDocs) {
                        feedbackDoc.getReference().collection("Student_Feedback")
                                .get()
                                .addOnSuccessListener(querySnapshots -> {
                                    for (DocumentSnapshot snapshot : querySnapshots) {
                                        StudentFeedbackModel feedback = snapshot.toObject(StudentFeedbackModel.class);
                                        feedbackList.add(feedback);
                                    }
                                    adapter.notifyDataSetChanged();
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
