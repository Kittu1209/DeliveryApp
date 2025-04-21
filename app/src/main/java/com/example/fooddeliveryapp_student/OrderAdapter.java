package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<OrderModel> orderList;

    public OrderAdapter(List<OrderModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        // Format date
        String formattedDate = "";
        if (order.getOrderDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            formattedDate = sdf.format(order.getOrderDate());
        }

        // Set order details
        holder.tvOrderId.setText(String.format("Order ID: #%s", order.getOrderId()));
        holder.tvOrderDate.setText(String.format("Order Date: %s", formattedDate));
        holder.tvTotalAmount.setText(String.format("Total: ₹%.2f", order.getTotalAmount()));

        // Set delivery status
        if (order.isDelivered()) {
            holder.tvDeliveryStatus.setText("Delivery Status: ✅ Delivered");
            holder.tvDeliveryStatus.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvDeliveryStatus.setText("Delivery Status: ❌ Not Delivered");
            holder.tvDeliveryStatus.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_red_dark));
        }

        // Set order items
        StringBuilder itemsText = new StringBuilder();
        if (order.getItemsList() != null) {
            for (String item : order.getItemsList()) {
                itemsText.append("• ").append(item).append("\n");
            }
        }
        holder.tvOrderItems.setText(itemsText.toString().trim());

        // Configure review button
        if (order.isDelivered() && !order.isReviewed()) {
            holder.btnReview.setVisibility(View.VISIBLE);
            holder.btnReview.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), StudentReviewActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                intent.putExtra("shopId", order.getShopId());
                v.getContext().startActivity(intent);
            });
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvOrderId;
        final TextView tvOrderDate;
        final TextView tvTotalAmount;
        final TextView tvDeliveryStatus;
        final TextView tvOrderItems;
        final Button btnReview;

        ViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvDeliveryStatus = itemView.findViewById(R.id.tv_delivery_status);
            tvOrderItems = itemView.findViewById(R.id.tv_order_items);
            btnReview = itemView.findViewById(R.id.btn_review);
        }
    }
}