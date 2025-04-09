package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignedDeliveryAdapter extends RecyclerView.Adapter<AssignedDeliveryAdapter.ViewHolder> {

    private Context context;
    private List<AssignedDeliveryModel> deliveryList;

    public AssignedDeliveryAdapter(Context context, List<AssignedDeliveryModel> deliveryList) {
        this.context = context;
        this.deliveryList = deliveryList;
    }

    @NonNull
    @Override
    public AssignedDeliveryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assigned_deliveres, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignedDeliveryAdapter.ViewHolder holder, int position) {
        AssignedDeliveryModel model = deliveryList.get(position);

        holder.tvOrderId.setText("Order ID: #" + model.getOrderId());
        holder.tvCustomerName.setText("Customer: " + model.getCustomerName());
        holder.tvAddress.setText("Address: " + model.getAddress());
        holder.tvItem.setText("Item: " + model.getItemName());
        holder.tvStatus.setText("Status: " + model.getDeliveryStatus());

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeliveryDetailsActivity.class);
            intent.putExtra("orderId", model.getOrderId());
            intent.putExtra("phoneNumber", model.getAddress()); // Replace with actual phone if needed
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvAddress, tvItem, tvStatus;
        Button btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvItem = itemView.findViewById(R.id.tvItem);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
