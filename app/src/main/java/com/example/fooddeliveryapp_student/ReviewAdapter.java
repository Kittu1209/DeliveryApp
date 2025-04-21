package com.example.fooddeliveryapp_student;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewModel> reviewList;

    public ReviewAdapter(List<ReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, comment, timestamp, orderId;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            comment = itemView.findViewById(R.id.comment);
            timestamp = itemView.findViewById(R.id.timestamp);
            orderId = itemView.findViewById(R.id.orderId);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bind(ReviewModel review) {
            try {
                studentName.setText(review.getStudentName() != null ? review.getStudentName() : "Anonymous");
                ratingBar.setRating(review.getRating());

                if (review.getComment() != null && !review.getComment().isEmpty()) {
                    comment.setText(review.getComment());
                    comment.setVisibility(View.VISIBLE);
                } else {
                    comment.setVisibility(View.GONE);
                }

                if (review.getOrderId() != null) {
                    orderId.setText("Order: " + review.getOrderId());
                    orderId.setVisibility(View.VISIBLE);
                } else {
                    orderId.setVisibility(View.GONE);
                }

                if (review.getTimestamp() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                    timestamp.setText(sdf.format(review.getTimestamp()));
                }
            } catch (Exception e) {
                Log.e("ReviewAdapter", "Error binding review data", e);
            }
        }}
}