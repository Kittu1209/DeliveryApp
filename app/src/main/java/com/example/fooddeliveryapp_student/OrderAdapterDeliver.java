package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapterDeliver extends RecyclerView.Adapter<OrderAdapterDeliver.OrderViewHolder> {

    private final List<order_model_vendor> orderList;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(order_model_vendor order);
    }

    public OrderAdapterDeliver(List<order_model_vendor> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_deliver, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        order_model_vendor order = orderList.get(position);
        holder.orderIdText.setText("Order ID: " + order.getOrderId());
        holder.deliveryAddressText.setText("Address: " + order.getHostel() + ", Room " + order.getRoom());

        holder.btnViewDetails.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, deliveryAddressText;
        Button btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.textOrderId);
            deliveryAddressText = itemView.findViewById(R.id.textDeliveryAddress);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
