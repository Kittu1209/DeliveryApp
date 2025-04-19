package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VendorFeedbackAdapter extends RecyclerView.Adapter<VendorFeedbackAdapter.ViewHolder> {

    private List<VendorFeedbackModel> feedbackList;

    public VendorFeedbackAdapter(List<VendorFeedbackModel> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvSubject, tvMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    @NonNull
    @Override
    public VendorFeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vendor_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorFeedbackAdapter.ViewHolder holder, int position) {
        VendorFeedbackModel feedback = feedbackList.get(position);
        holder.tvName.setText("Name: " + feedback.getName());
        holder.tvEmail.setText("Email: " + feedback.getEmail());
        holder.tvSubject.setText("Subject: " + feedback.getSubject());
        holder.tvMessage.setText("Message: " + feedback.getMessage());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }
}
