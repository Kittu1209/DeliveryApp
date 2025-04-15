package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminEditVendorAdapter extends RecyclerView.Adapter<AdminEditVendorAdapter.ViewHolder> {

    private final List<AdminEditVendorModel> vendorList;
    private final Context context;

    public AdminEditVendorAdapter(Context context, List<AdminEditVendorModel> vendorList) {
        this.context = context;
        this.vendorList = vendorList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phone, id, shop;
        Button editBtn, deleteBtn;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textVendorName);
            email = view.findViewById(R.id.textVendorEmail);
            phone = view.findViewById(R.id.textVendorPhone);
            id = view.findViewById(R.id.textVendorId);
            shop = view.findViewById(R.id.textShopName);
            editBtn = view.findViewById(R.id.btnEditVendor);
            deleteBtn = view.findViewById(R.id.btnDeleteVendor);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_edit_vendor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminEditVendorModel vendor = vendorList.get(position);

        // Safe field access and display
        holder.name.setText("Name: " + safeText(vendor.getVendorName()));
        holder.email.setText("Email: " + safeText(vendor.getVendorEmail()));
        holder.phone.setText("Phone: " + safeText(vendor.getVendorPhone()));
        holder.id.setText("ID: " + safeText(vendor.getVendorId()));
        holder.shop.setText("Shop: " + safeText(vendor.getShopName()));

        holder.editBtn.setOnClickListener(v -> {
            if (isValidVendor(vendor)) {
                Intent intent = new Intent(context, EditVendorPage.class);
                intent.putExtra("docId", vendor.getDocId());
                intent.putExtra("vendorName", vendor.getVendorName());
                intent.putExtra("vendorEmail", vendor.getVendorEmail());
                intent.putExtra("vendorPhone", vendor.getVendorPhone());
                intent.putExtra("vendorId", vendor.getVendorId());
                intent.putExtra("shopName", vendor.getShopName());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Invalid vendor data. Cannot edit.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            String docId = vendor.getDocId();
            if (docId == null || docId.trim().isEmpty()) {
                Toast.makeText(context, "Invalid vendor ID", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance().collection("Vendors")
                    .document(docId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        vendorList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Vendor deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete vendor", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    // Helper to check for valid vendor fields
    private boolean isValidVendor(AdminEditVendorModel vendor) {
        return vendor != null &&
                !isNullOrEmpty(vendor.getDocId()) &&
                !isNullOrEmpty(vendor.getVendorName()) &&
                !isNullOrEmpty(vendor.getVendorEmail()) &&
                !isNullOrEmpty(vendor.getVendorPhone()) &&
                !isNullOrEmpty(vendor.getVendorId()) &&
                !isNullOrEmpty(vendor.getShopName());
    }

    // Helper to avoid null display
    private String safeText(String str) {
        return (str == null || str.trim().isEmpty()) ? "N/A" : str.trim();
    }

    // Helper to validate non-null non-empty strings
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
