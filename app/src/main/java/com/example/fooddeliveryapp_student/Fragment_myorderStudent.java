package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class Fragment_myorderStudent extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private ArrayList<OrderModel> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_myorder_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_my_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        fetchMyOrders();
    }

    private void fetchMyOrders() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String orderId = document.getString("orderId");
                        Date orderDate = document.getTimestamp("createdAt").toDate();
                        double totalAmount = document.getDouble("totalAmount");
                        String status = document.getString("status");

                        boolean isDelivered = status.equalsIgnoreCase("delivered");

                        ArrayList<String> itemsList = new ArrayList<>();
                        ArrayList<Object> items = (ArrayList<Object>) document.get("items");
                        if (items != null) {
                            for (Object itemObj : items) {
                                if (itemObj instanceof java.util.Map) {
                                    java.util.Map<?, ?> itemMap = (java.util.Map<?, ?>) itemObj;
                                    String itemName = (String) itemMap.get("name");
                                    Long quantity = (Long) itemMap.get("quantity");
                                    itemsList.add(itemName + " x " + quantity);
                                }
                            }
                        }

                        orderList.add(new OrderModel(orderId, orderDate, totalAmount, isDelivered, itemsList));
                    }
                    orderAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load orders!", Toast.LENGTH_SHORT).show());
    }
}
