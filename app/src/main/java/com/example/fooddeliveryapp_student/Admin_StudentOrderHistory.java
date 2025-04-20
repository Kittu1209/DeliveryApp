package com.example.fooddeliveryapp_student;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Admin_StudentOrderHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    List<OrderHistoryModel> orderList;
    OrderHistoryAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_student_order_history);

        recyclerView = findViewById(R.id.recyclerView_admin_student_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(this, orderList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String orderId = doc.getString("orderId");
                            String status = doc.getString("status");
                            String name = doc.getString("name");
                            String phone = doc.getString("phone");
                            String hostel = doc.getString("hostel");
                            String room = doc.getString("room");
                            long totalAmount = doc.getLong("totalAmount");
                            List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                            String assignedDeliveryManId = doc.getString("assignedDeliveryManId");
                            Map<String, Object> deliveryAddress = (Map<String, Object>) doc.get("deliveryAddress");

                            OrderHistoryModel model = new OrderHistoryModel(orderId, status, name, phone, hostel, room, totalAmount, items, deliveryAddress);
                            orderList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
