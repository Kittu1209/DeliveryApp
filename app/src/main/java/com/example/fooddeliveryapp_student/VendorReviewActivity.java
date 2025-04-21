package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
public class VendorReviewActivity extends AppCompatActivity {

    private static final String TAG = "VendorReview";
    private RecyclerView reviewsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FirebaseFirestore db;
    private String shopId;
    private List<ReviewModel> reviewList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_review);

        // Initialize Firestore with persistence enabled
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        // Get shopId from intent
        shopId = getIntent().getStringExtra("shopId");
        if (shopId == null || shopId.isEmpty()) {
            Toast.makeText(this, "Invalid shop ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Loading reviews for shop: " + shopId);

        // Initialize views
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        // Setup RecyclerView
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        fetchReviews();
    }

    private void fetchReviews() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        // Temporary simpler query while index is building
        db.collection("reviews")
                .whereEqualTo("shopId", shopId)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        reviewList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Filter visible reviews client-side temporarily
                                if (document.getBoolean("visible") != Boolean.TRUE) {
                                    continue;
                                }

                                long timestampLong = document.getLong("timestamp");
                                Date timestamp = new Date(timestampLong);

                                ReviewModel review = new ReviewModel(
                                        document.getString("studentId"),
                                        document.getString("studentName"),
                                        document.getString("shopId"),
                                        document.getString("orderId"),
                                        document.getLong("rating").intValue(),
                                        document.getString("comment"),
                                        timestamp
                                );
                                reviewList.add(review);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing review: " + document.getId(), e);
                            }
                        }

                        // Sort by timestamp client-side temporarily
                        Collections.sort(reviewList, (r1, r2) ->
                                Long.compare(r2.getTimestamp().getTime(), r1.getTimestamp().getTime()));

                        if (reviewList.isEmpty()) {
                            emptyView.setText("No reviews found");
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            reviewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e(TAG, "Error getting reviews", task.getException());
                        emptyView.setText("Error loading reviews");
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}