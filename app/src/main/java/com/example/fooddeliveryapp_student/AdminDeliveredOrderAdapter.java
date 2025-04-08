package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminDeliveredOrderAdapter extends RecyclerView.Adapter<AdminDeliveredOrderAdapter.ViewHolder> {
    List<AdminDeliveredOrderModel> orderList;

    public AdminDeliveredOrderAdapter(List<AdminDeliveredOrderModel> orderList) {
        this.orderList = orderList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, userName, itemName, quantity, price, deliveryAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            userName = itemView.findViewById(R.id.userName);
            itemName = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            deliveryAddress = itemView.findViewById(R.id.deliveryAddress);
        }

        public void bind(AdminDeliveredOrderModel model) {
            orderId.setText("Order ID: " + model.getOrderId());
            userName.setText("User: " + model.getUserName());
            itemName.setText("Item: " + model.getItemName());
            quantity.setText("Quantity: " + model.getQuantity());
            price.setText("Price: ₹" + model.getPrice());
            deliveryAddress.setText("Address: " + model.getDeliveryAddress());
        }
    }

    @NonNull
    @Override
    public AdminDeliveredOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_delivered_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDeliveredOrderAdapter.ViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
