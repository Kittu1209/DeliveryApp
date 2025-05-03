package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminApproveVendorAdapter extends RecyclerView.Adapter<AdminApproveVendorAdapter.ViewHolder> {

    private final List<AdminApproveVendorModel> vendorList;
    private final Context context;
    private final FirebaseFirestore db;

    public AdminApproveVendorAdapter(List<AdminApproveVendorModel> vendorList, Context context) {
        this.vendorList = vendorList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_approve_vendor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminApproveVendorModel vendor = vendorList.get(position);

        try {
            holder.tvVendorName.setText(vendor.getVendorName());
            holder.tvShopName.setText(vendor.getName());
            holder.tvAddress.setText(vendor.getAddress());
            holder.tvCuisine.setText(vendor.getCuisine());
            holder.tvDescription.setText(vendor.getDescription());
            holder.tvDeliveryTime.setText(vendor.getDeliveryTime());
            holder.tvPriceForTwo.setText(vendor.getPriceForTwo());
            holder.tvEmail.setText(vendor.getEmail());
            holder.tvPhone.setText(vendor.getPhone());

            loadVendorImage(holder.ivShopImage, vendor.getImage());

            holder.btnApprove.setOnClickListener(v -> handleVendorApproval(vendor));
            holder.btnReject.setOnClickListener(v -> handleVendorRejection(vendor, position));
        } catch (Exception e) {
            Log.e("AdminApproveVendor", "Error binding view", e);
            Toast.makeText(context, "Error displaying vendor", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadVendorImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.heartlogo)
                    .error(R.drawable.heartlogo)
                    .into(imageView);
        }
    }

    private void handleVendorApproval(AdminApproveVendorModel vendor) {
        db.collection("shops").document(vendor.getId())
                .update("isActive", true, "updatedAt", com.google.firebase.Timestamp.now())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vendorList.remove(vendor);
                        notifyDataSetChanged();
                        sendApprovalEmail(vendor.getEmail(), vendor.getVendorName());
                        Toast.makeText(context, "Vendor approved", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("AdminApproveVendor", "Approval failed", task.getException());
                        Toast.makeText(context, "Approval failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleVendorRejection(AdminApproveVendorModel vendor, int position) {
        String rejectedName = vendor.getName() + " (rejected)";

        db.collection("shops").document(vendor.getId())
                .update("name", rejectedName, "updatedAt", com.google.firebase.Timestamp.now())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vendorList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, vendorList.size());
                        Toast.makeText(context, "Vendor rejected", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("AdminApproveVendor", "Rejection failed", task.getException());
                        Toast.makeText(context, "Rejection failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendApprovalEmail(String email, String name) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Shop Approval Notification");
            emailIntent.putExtra(Intent.EXTRA_TEXT, createEmailContent(name));

            if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(emailIntent, "Send approval email"));
            } else {
                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("AdminApproveVendor", "Email error", e);
            Toast.makeText(context, "Failed to send email", Toast.LENGTH_SHORT).show();
        }
    }

    private String createEmailContent(String name) {
        return "Dear " + name + ",\n\n" +
                "Your shop has been approved in our platform.\n\n" +
                "You can now start receiving orders.\n\n" +
                "Best regards,\nAdmin Team";
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivShopImage;
        final TextView tvVendorName, tvShopName, tvAddress, tvCuisine, tvDescription;
        final TextView tvDeliveryTime, tvPriceForTwo, tvEmail, tvPhone;
        final Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShopImage = itemView.findViewById(R.id.ivShopImage);
            tvVendorName = itemView.findViewById(R.id.tvVendorName);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvCuisine = itemView.findViewById(R.id.tvCuisine);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDeliveryTime = itemView.findViewById(R.id.tvDeliveryTime);
            tvPriceForTwo = itemView.findViewById(R.id.tvPriceForTwo);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}