package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminViewStaffAdapter extends RecyclerView.Adapter<AdminViewStaffAdapter.ViewHolder> {

    private List<AdminViewStaffModel> staffList;

    public AdminViewStaffAdapter(List<AdminViewStaffModel> staffList) {
        this.staffList = staffList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView staffName, staffEmail, staffPhone, staffID, currentDuty, licenseNumber, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            staffName = itemView.findViewById(R.id.staffName);
            staffEmail = itemView.findViewById(R.id.staffEmail);
            staffPhone = itemView.findViewById(R.id.staffPhone);
            staffID = itemView.findViewById(R.id.staffID);
            currentDuty = itemView.findViewById(R.id.currentDuty);
            licenseNumber = itemView.findViewById(R.id.licenseNumber);
            status = itemView.findViewById(R.id.status);
        }
    }

    @NonNull
    @Override
    public AdminViewStaffAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_viewstaff, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewStaffAdapter.ViewHolder holder, int position) {
        AdminViewStaffModel model = staffList.get(position);

        holder.staffName.setText("Name: " + model.getName());
        holder.staffEmail.setText("Email: " + model.getEmail());
        holder.staffPhone.setText("Phone: " + model.getPhone());
        holder.staffID.setText("Staff ID: " + model.getDel_man_id());
        holder.currentDuty.setText("Current Duty: " + model.getCurrent_duty());
        holder.licenseNumber.setText("License No: " + model.getDriving_license_no());
        holder.status.setText("Status: " + model.getAdmin_control());
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }
}
