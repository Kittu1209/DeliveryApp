package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class order_adapter_vendor extends RecyclerView.Adapter<order_adapter_vendor.OrderViewHolder> {

    private List<order_model_vendor> orderList;

    public order_adapter_vendor(List<order_model_vendor> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_vendor, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        order_model_vendor order = orderList.get(position);

        holder.textOrderId.setText("Order ID: " + order.getOrderId());
        holder.textName.setText("Name: " + order.getName());
        holder.textPhone.setText("Phone: " + order.getPhone());
        holder.textAddress.setText("Address: " + order.getHostel() + ", Room " + order.getRoom());
        holder.textAmount.setText("Total: ₹" + order.getTotalAmount());
        holder.textStatus.setText("Status: " + order.getStatus());

        Timestamp timestamp = order.getCreatedAt();
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.textDate.setText("Date: " + sdf.format(date));
        } else {
            holder.textDate.setText("Date: N/A");
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId, textName, textPhone, textAddress, textAmount, textStatus, textDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textName = itemView.findViewById(R.id.text_name);
            textPhone = itemView.findViewById(R.id.text_phone);
            textAddress = itemView.findViewById(R.id.text_address);
            textAmount = itemView.findViewById(R.id.text_amount);
            textStatus = itemView.findViewById(R.id.text_status);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}
