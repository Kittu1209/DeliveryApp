package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class Orders_Fragment_Vendor extends Fragment {

    private RecyclerView recyclerView;
    private order_adapter_vendor ordersAdapter;
    private List<order_model_vendor> orderItemList;
    private FirebaseFirestore db;

    public Orders_Fragment_Vendor() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders___vendor, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderItemList = new ArrayList<>();
        ordersAdapter = new order_adapter_vendor(orderItemList);
        recyclerView.setAdapter(ordersAdapter);

        fetchOrders();
        return view;
    }

    private void fetchOrders() {
        db.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderItemList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String orderId = doc.getString("orderId");
                        String name = doc.getString("name");
                        String phone = doc.getString("phone");
                        String hostel = doc.getString("hostel");
                        String room = doc.getString("room");
                        String status = doc.getString("status");
                        double totalAmount = doc.getDouble("totalAmount") != null ? doc.getDouble("totalAmount") : 0.0;
                        Timestamp createdAt = doc.getTimestamp("createdAt");
                        String userId = doc.getString("userId");
                        List<Object> items = (List<Object>) doc.get("items");

                        order_model_vendor order = new order_model_vendor(orderId, name, phone, hostel, room, status, totalAmount, createdAt, userId, items);
                        orderItemList.add(order);
                    }
                    ordersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show()
                );
    }
}
