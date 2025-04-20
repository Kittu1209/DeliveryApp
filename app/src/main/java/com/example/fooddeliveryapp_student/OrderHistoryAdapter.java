package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private List<OrderHistoryModel> orderList;

    public OrderHistoryAdapter(Context context, List<OrderHistoryModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, name, phone, address, status, totalAmount, itemDetails, deliveryManId;

        public ViewHolder(View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            name = itemView.findViewById(R.id.customer_name);
            phone = itemView.findViewById(R.id.customer_phone);
            address = itemView.findViewById(R.id.address);
            status = itemView.findViewById(R.id.status);
            totalAmount = itemView.findViewById(R.id.total_amount);
            itemDetails = itemView.findViewById(R.id.item_details);
              // New field
        }
    }

    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHistoryAdapter.ViewHolder holder, int position) {
        OrderHistoryModel order = orderList.get(position);

        holder.orderId.setText("Order ID: " + order.getOrderId());
        holder.name.setText("Name: " + order.getName());
        holder.phone.setText("Phone: " + order.getPhone());
        holder.address.setText("Address: " + order.getHostel() + ", Room " + order.getRoom());
        holder.status.setText("Status: " + order.getStatus());
        holder.totalAmount.setText("Total Amount: ₹" + order.getTotalAmount());

        // Display items
        List<Map<String, Object>> itemsList = order.getItems();
        if (itemsList != null && !itemsList.isEmpty()) {
            StringBuilder items = new StringBuilder();
            for (Map<String, Object> item : itemsList) {
                String itemName = (String) item.get("name");
                Long quantity = (Long) item.get("quantity");
                items.append(itemName).append(" x").append(quantity).append("\n");
            }
            holder.itemDetails.setText("Items:\n" + items.toString().trim());
        } else {
            holder.itemDetails.setText("Items: N/A");
        }

        // Show delivery man ID and delivery address
        holder.deliveryManId.setText("Assigned Delivery Man: " + order.getAssignedDeliveryManId());

        // Assuming delivery address is a map, extract information like hostel, room, etc.
        Map<String, Object> deliveryAddress = order.getDeliveryAddress();
        if (deliveryAddress != null) {
            String deliveryInfo = "Hostel: " + deliveryAddress.get("hostel") + "\n" +
                    "Room: " + deliveryAddress.get("room");
            holder.address.append("\n" + deliveryInfo);  // Append more details to address TextView
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
