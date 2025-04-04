package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private ArrayList<OrderModel> orderList;

    public OrderAdapter(ArrayList<OrderModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(order.getOrderDate());

        holder.tvOrderId.setText("Order ID: #" + order.getOrderId());
        holder.tvOrderDate.setText("Order Date: " + formattedDate);
        holder.tvTotalAmount.setText("Total: ₹" + order.getTotalAmount());

        if (order.isDelivered()) {
            holder.tvDeliveryStatus.setText("Delivery Status: ✅ Delivered");
            holder.tvDeliveryStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvDeliveryStatus.setText("Delivery Status: ❌ Not Delivered");
            holder.tvDeliveryStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }

        // Display ordered items
        StringBuilder itemsText = new StringBuilder();
        for (String item : order.getItemsList()) {
            itemsText.append("• ").append(item).append("\n");
        }
        holder.tvOrderItems.setText(itemsText.toString().trim());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvTotalAmount, tvDeliveryStatus, tvOrderItems;

        ViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvDeliveryStatus = itemView.findViewById(R.id.tv_delivery_status);
            tvOrderItems = itemView.findViewById(R.id.tv_order_items);
        }
    }
}
