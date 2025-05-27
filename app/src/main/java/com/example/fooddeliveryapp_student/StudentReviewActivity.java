package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentReviewActivity extends AppCompatActivity {

    private LinearLayout starContainer;
    private TextView ratingText;
    private EditText reviewComment;
    private Button submitReview;
    private String orderId, shopId, studentId;
    private FirebaseFirestore db;
    private int selectedRating = 0;
    private String studentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_review);

        // Initialize views
        starContainer = findViewById(R.id.starContainer);
        ratingText = findViewById(R.id.ratingText);
        reviewComment = findViewById(R.id.reviewComment);
        submitReview = findViewById(R.id.submitReview);
        db = FirebaseFirestore.getInstance();

        // Button click listener to submit review
        submitReview.setOnClickListener(v -> submitReview());

        // Get order details from intent
        orderId = getIntent().getStringExtra("orderId");
        shopId = getIntent().getStringExtra("shopId");

        // First fetch the order to get studentId
        fetchOrderDetails();
    }

    private void fetchOrderDetails() {
        db.collection("orders").document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            studentId = document.getString("userId");
                            if (studentId != null && !studentId.isEmpty()) {
                                fetchStudentName();
                            } else {
                                showError("Student information not found in order");
                            }
                        } else {
                            showError("Order document not found");
                        }
                    } else {
                        showError("Failed to load order details");
                    }
                });
    }

    private void fetchStudentName() {
        db.collection("Students").document(studentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            studentName = document.getString("stuname");
                            if (studentName == null || studentName.isEmpty()) {
                                studentName = "Anonymous";
                            }
                            createStarRating();
                        } else {
                            showError("Student document not found");
                            studentName = "Anonymous";
                            createStarRating();
                        }
                    } else {
                        showError("Failed to load student details");
                        studentName = "Anonymous";
                        createStarRating();
                    }
                });
    }

    private void createStarRating() {
        starContainer.removeAllViews();

        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(32),
                    dpToPx(32));
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            star.setLayoutParams(params);
            star.setTag(i);
            star.setImageResource(R.drawable.ic_star_empty);

            star.setOnClickListener(v -> {
                selectedRating = (int) v.getTag();
                updateStarDisplay(selectedRating);
                ratingText.setText(selectedRating + "/5");
            });

            starContainer.addView(star);
        }
    }

    private void updateStarDisplay(int rating) {
        for (int i = 0; i < starContainer.getChildCount(); i++) {
            ImageView star = (ImageView) starContainer.getChildAt(i);
            int starValue = i + 1;

            if (starValue <= rating) {
                star.setImageResource(R.drawable.ic_star_filled);
            } else {
                star.setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void submitReview() {
        if (selectedRating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> review = new HashMap<>();
        review.put("studentId", studentId);
        review.put("studentName", studentName);
        review.put("studentEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        review.put("shopId", shopId);
        review.put("orderId", orderId);
        review.put("rating", selectedRating);
        review.put("comment", reviewComment.getText().toString().trim());
        review.put("timestamp", System.currentTimeMillis());
        review.put("visible", true);

        db.collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    db.collection("orders").document(orderId)
                            .update("isReviewed", true)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit review: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        studentName = "Anonymous";
        createStarRating();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
