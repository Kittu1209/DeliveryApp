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
import java.util.ArrayList;
import java.util.List;

public class Orders_Fragment_Vendor extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter ordersAdapter;
    private List<OrderModel> orderItemList;
    private FirebaseFirestore db;

    public Orders_Fragment_Vendor() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders___vendor, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderItemList = new ArrayList<>();
    //    ordersAdapter = new OrderAdapter(orderItemList);
        recyclerView.setAdapter(ordersAdapter);

        fetchOrders();

        return view;
    }

    private void fetchOrders() {
        db.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderItemList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String orderId = document.getId();
                        String userId = document.getString("userId");
                        String studentName = document.getString("studentName");
                        String phoneNumber = document.getString("phoneNumber");
                        String hostel = document.getString("hostel");
                        String room = document.getString("room");
                        List<CartItem> items = (List<CartItem>) document.get("items");
                        String paymentStatus = document.getString("paymentStatus");
                        String orderStatus = document.getString("orderStatus");
                        String razorpayPaymentId = document.getString("razorpayPaymentId");
                        long createdAt = document.getLong("createdAt");
                        long updatedAt = document.getLong("updatedAt");

                        double totalPrice = document.getDouble("totalPrice"); // Retrieve total price
                     //   OrderModel orderItem = new OrderModel(orderId, userId, studentName, phoneNumber,
                         //       hostel, room, items, paymentStatus, orderStatus, razorpayPaymentId, createdAt, updatedAt, totalPrice);

                       // orderItemList.add(orderItem);
                    }
                    ordersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show());
    }
}
