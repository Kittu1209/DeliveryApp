package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Map;

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
        // Hide BottomNavigationView
        requireActivity().findViewById(R.id.bottom_nevigation).setVisibility(View.GONE);
    }

    public void onDestroyView() {
        super.onDestroyView();
        // Show BottomNavigationView again when fragment is destroyed
        requireActivity().findViewById(R.id.bottom_nevigation).setVisibility(View.VISIBLE);
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
                        Date orderDate = document.getTimestamp("createdAt") != null
                                ? document.getTimestamp("createdAt").toDate()
                                : new Date();
                        double totalAmount = document.getDouble("totalAmount") != null
                                ? document.getDouble("totalAmount")
                                : 0.0;
                        String status = document.getString("status") != null
                                ? document.getString("status")
                                : "pending";

                        boolean isDelivered = status.equalsIgnoreCase("delivered");

                        // Get shopId from the first item (assuming all items are from same shop)
                        String shopId = "";
                        ArrayList<Object> items = (ArrayList<Object>) document.get("items");
                        if (items != null && !items.isEmpty()) {
                            Object firstItem = items.get(0);
                            if (firstItem instanceof Map) {
                                Map<?, ?> itemMap = (Map<?, ?>) firstItem;
                                shopId = (String) itemMap.get("shopId");
                            }
                        }

                        // Check if review exists for this order
                        boolean isReviewed = document.getBoolean("isReviewed") != null
                                ? document.getBoolean("isReviewed")
                                : false;

                        ArrayList<String> itemsList = new ArrayList<>();
                        if (items != null) {
                            for (Object itemObj : items) {
                                if (itemObj instanceof Map) {
                                    Map<?, ?> itemMap = (Map<?, ?>) itemObj;
                                    String itemName = (String) itemMap.get("name");
                                    Long quantity = (Long) itemMap.get("quantity");

                                    if (itemName != null && quantity != null) {
                                        itemsList.add(itemName + " x " + quantity);
                                    }
                                }
                            }
                        }

                        // Corrected order of parameters when creating OrderModel
                        orderList.add(new OrderModel(
                                orderId,
                                orderDate,
                                totalAmount,
                                isDelivered,
                                isReviewed,
                                shopId,
                                itemsList
                        ));
                    }

                    orderAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("MyOrders", "Error fetching orders: ", e);
                    Toast.makeText(getContext(), "Failed to load orders: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}