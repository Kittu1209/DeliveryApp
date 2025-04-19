package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;

public class StudentFeedbackAdapter extends RecyclerView.Adapter<StudentFeedbackAdapter.FeedbackViewHolder> {

    private List<StudentFeedbackModel> feedbackList;

    public StudentFeedbackAdapter(List<StudentFeedbackModel> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        StudentFeedbackModel model = feedbackList.get(position);

        holder.tvName.setText("Name: " + model.getName());
        holder.tvEmail.setText("Email: " + model.getEmail());
        holder.tvSubject.setText("Subject: " + model.getSubject());
        holder.tvMessage.setText("Message: " + model.getMessage());

        String formattedDate = DateFormat.getDateTimeInstance().format(model.getTimestamp());
        holder.tvTimestamp.setText("Date: " + formattedDate);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvSubject, tvMessage, tvTimestamp;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
