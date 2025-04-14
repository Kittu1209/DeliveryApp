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

public class OrderDeliveryHomeAdapter extends RecyclerView.Adapter<OrderDeliveryHomeAdapter.OrderViewHolder> {

    private List<Order> ordersList;
    private Context context;

    public OrderDeliveryHomeAdapter(List<Order> orderList, Context context) {
        this.ordersList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_delivery_home, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order currentOrder = ordersList.get(position);
        holder.orderDetailsText.setText("Order ID: " + currentOrder.getOrderId()
                + "\nStatus: " + currentOrder.getStatus());

        holder.completeOrderButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeliveryDetailsActivity.class);
            intent.putExtra("orderId", currentOrder.getOrderId());
            intent.putExtra("status", currentOrder.getStatus());
            intent.putExtra("deliveryManId", currentOrder.getAssignedDeliveryManId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDetailsText;
        Button completeOrderButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDetailsText = itemView.findViewById(R.id.orderDetailsText);
            completeOrderButton = itemView.findViewById(R.id.completeOrderButton);
        }
    }
}
