package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    private final Context context;
    private final List<AdminOrderModel> orderList;

    public AdminOrderAdapter(Context context, List<AdminOrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public AdminOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderAdapter.ViewHolder holder, int position) {
        AdminOrderModel order = orderList.get(position);

        holder.orderId.setText("Order ID: " + order.getOrderId());
        holder.itemName.setText("Item: " + order.getItemName());
        holder.price.setText("Price: ₹" + order.getPrice());
        holder.quantity.setText("Qty: " + order.getQuantity());
        holder.studentName.setText("Student: " + order.getStudentName());
        holder.address.setText("Address: " + order.getDeliveryAddress());
        holder.status.setText("Status: " + order.getStatus());

        try {
            byte[] decodedString = Base64.decode(order.getImageUrl(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, itemName, price, quantity, studentName, address, status;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.adminOrderId);
            itemName = itemView.findViewById(R.id.adminOrderItem);
            price = itemView.findViewById(R.id.adminOrderPrice);
            quantity = itemView.findViewById(R.id.adminOrderQuantity);
            studentName = itemView.findViewById(R.id.adminOrderStudentName);
            address = itemView.findViewById(R.id.adminOrderAddress);
            status = itemView.findViewById(R.id.adminOrderStatus);
            image = itemView.findViewById(R.id.adminOrderImage);
        }
    }
}
