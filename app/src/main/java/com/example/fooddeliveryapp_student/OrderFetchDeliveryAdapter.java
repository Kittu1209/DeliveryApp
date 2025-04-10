package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderFetchDeliveryAdapter extends RecyclerView.Adapter<OrderFetchDeliveryAdapter.OrderViewHolder> {

    private List<OrderFetchDeliveryModel> orderList;
    private OnItemClickListener listener;

    public OrderFetchDeliveryAdapter(List<OrderFetchDeliveryModel> orderList, OnItemClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_deliver, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderFetchDeliveryModel order = orderList.get(position);

        // Bind the order data to the views
        holder.orderIdTextView.setText(order.getOrderId());
        holder.deliveryAddressTextView.setText(order.getDeliveryAddress());

        // Set onClickListener to pass the orderId to the listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(order.getOrderId()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String orderId); // Pass the orderId to the listener
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderIdTextView;
        public TextView deliveryAddressTextView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.textOrderId);
            deliveryAddressTextView = itemView.findViewById(R.id.textDeliveryAddress);
        }
    }
}
