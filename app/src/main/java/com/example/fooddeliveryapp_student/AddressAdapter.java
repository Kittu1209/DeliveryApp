package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private final Context context;
    private final List<AddressModel> addressList;
    private final OnAddressSelectedListener onAddressSelectedListener;

    public AddressAdapter(Context context, List<AddressModel> addressList, OnAddressSelectedListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.onAddressSelectedListener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel address = addressList.get(position);

        holder.tvStudentName.setText("Name: " + address.getStudentName());
        holder.tvPhoneNumber.setText("Phone: " + address.getPhoneNumber());
        holder.tvHostel.setText("Hostel: " + address.getHostel());
        holder.tvRoom.setText("Room: " + address.getRoom());

        // Handle Select Address Button Click
        holder.btnSelectAddress.setOnClickListener(v -> {
            if (onAddressSelectedListener != null) {
                onAddressSelectedListener.onAddressSelected(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvPhoneNumber, tvHostel, tvRoom;
        Button btnSelectAddress;

        public AddressViewHolder(View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvHostel = itemView.findViewById(R.id.tvHostel);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            btnSelectAddress = itemView.findViewById(R.id.btnSelectAddress);
        }
    }

    public interface OnAddressSelectedListener {
        void onAddressSelected(AddressModel selectedAddress);
    }
}
