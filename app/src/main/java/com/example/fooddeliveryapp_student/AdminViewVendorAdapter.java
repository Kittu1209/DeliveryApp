package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminViewVendorAdapter extends RecyclerView.Adapter<AdminViewVendorAdapter.ViewHolder> {

    private final List<AdminViewVendorModel> vendorList;

    public AdminViewVendorAdapter(List<AdminViewVendorModel> vendorList) {
        this.vendorList = vendorList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView vendorName, vendorEmail, vendorPhone, vendorId, shopName;

        public ViewHolder(View view) {
            super(view);
            vendorName = view.findViewById(R.id.textVendorName);
            vendorEmail = view.findViewById(R.id.textVendorEmail);
            vendorPhone = view.findViewById(R.id.textVendorPhone);
            vendorId = view.findViewById(R.id.textVendorId);
            shopName = view.findViewById(R.id.textShopName);
        }
    }

    @NonNull
    @Override
    public AdminViewVendorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_vendor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewVendorAdapter.ViewHolder holder, int position) {
        AdminViewVendorModel vendor = vendorList.get(position);
        holder.vendorName.setText("Name: " + vendor.getVendorName());
        holder.vendorEmail.setText("Email: " + vendor.getVendorEmail());
        holder.vendorPhone.setText("Phone: " + vendor.getVendorPhone());
        holder.vendorId.setText("ID: " + vendor.getVendorId());
        holder.shopName.setText("Shop: " + vendor.getShopName());
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }
}
