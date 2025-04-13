package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminActiveDelManAdapter extends RecyclerView.Adapter<AdminActiveDelManAdapter.ViewHolder> {

    private Context context;
    private List<AdminActiveDelManModel> list;
    private FirebaseFirestore db;

    public AdminActiveDelManAdapter(Context context, List<AdminActiveDelManModel> list) {
        this.context = context;
        this.list = list;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_active_del_man, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminActiveDelManModel model = list.get(position);

        holder.name.setText(model.getName());
        holder.email.setText(model.getEmail());
        holder.phone.setText(model.getPhone());
        holder.address.setText(model.getDel_men_address());
        holder.license.setText(model.getDriving_license_no());

        holder.btnActivate.setOnClickListener(v -> {
            db.collection("delivery_man").document(model.getDel_man_id())
                    .update("admin_control", "active")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Delivery Man Activated", Toast.LENGTH_SHORT).show();
                        list.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    });
        });

        holder.btnRemove.setOnClickListener(v -> {
            db.collection("delivery_man").document(model.getDel_man_id())
                    .update("admin_control", "Removed")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Delivery Man Removed", Toast.LENGTH_SHORT).show();
                        list.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phone, address, license;
        Button btnActivate, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtDelManName);
            email = itemView.findViewById(R.id.txtDelManEmail);
            phone = itemView.findViewById(R.id.txtDelManPhone);
            address = itemView.findViewById(R.id.txtDelManAddress);
            license = itemView.findViewById(R.id.txtLicenseNo);
            btnActivate = itemView.findViewById(R.id.btnActivate);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
