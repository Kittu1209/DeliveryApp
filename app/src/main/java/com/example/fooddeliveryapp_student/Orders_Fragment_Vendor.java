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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Orders_Fragment_Vendor extends Fragment {

    private RecyclerView recyclerView;
    private order_adapter_vendor adapter;
    private List<order_model_vendor> orderList;
    private FirebaseFirestore db;

    public Orders_Fragment_Vendor() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders___vendor, container, false);

        recyclerView = view.findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter = new order_adapter_vendor(orderList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchOrders();

        return view;
    }

    private void fetchOrders() {
        db.collection("orders").get().addOnSuccessListener(queryDocumentSnapshots -> {
            orderList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String orderId = doc.getString("orderId");
                String userId = doc.getString("userId");
                String name = doc.getString("name");
                String phone = doc.getString("phone");
                String hostel = doc.getString("hostel");
                String room = doc.getString("room");
                String status = doc.getString("status");
                double totalAmount = doc.getDouble("totalAmount") != null ? doc.getDouble("totalAmount") : 0;
                Timestamp createdAt = doc.getTimestamp("createdAt");

                List<ItemModel> itemList = new ArrayList<>();
                List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) doc.get("items");
                if (itemsRaw != null) {
                    for (Map<String, Object> itemMap : itemsRaw) {
                        String itemName = (String) itemMap.get("name");
                        double price = itemMap.get("price") instanceof Number ? ((Number) itemMap.get("price")).doubleValue() : 0;
                        int qty = itemMap.get("quantity") instanceof Number ? ((Number) itemMap.get("quantity")).intValue() : 0;
                        itemList.add(new ItemModel(itemName, price, qty));
                    }
                }

                orderList.add(new order_model_vendor(orderId, userId, name, phone, hostel, room, status, totalAmount, createdAt, itemList));
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
        });
    }
}
