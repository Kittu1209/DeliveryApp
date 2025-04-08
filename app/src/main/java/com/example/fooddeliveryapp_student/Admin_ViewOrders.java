package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Admin_ViewOrders extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<AdminOrderModel> orderList;
    private AdminOrderAdapter adapter;
    private final String currentVendorId = "1cVFS6yP7MgL1kJtESQhE0CVeVp1"; // Replace with actual vendor ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);

        recyclerView = findViewById(R.id.recyclerViewAdminOrders);
        orderList = new ArrayList<>();
        adapter = new AdminOrderAdapter(this, orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchVendorOrders();
    }

    private void fetchVendorOrders() {
        FirebaseFirestore.getInstance().collection("orders")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                        if (items != null) {
                            for (Map<String, Object> item : items) {
                                String shopId = (String) item.get("shopId");
                                if (shopId != null && shopId.equals(currentVendorId)) {

                                    String itemName = (String) item.get("name");
                                    double price = ((Number) item.get("price")).doubleValue();
                                    int quantity = ((Number) item.get("quantity")).intValue();
                                    String imageUrl = (String) item.get("imageUrl");

                                    Map<String, Object> address = (Map<String, Object>) doc.get("deliveryAddress");
                                    String studentName = (String) address.get("name");
                                    String hostel = (String) address.get("hostel");
                                    String room = (String) address.get("room");
                                    String deliveryAddress = hostel + ", Room " + room;

                                    String orderId = (String) doc.get("orderId");
                                    String status = (String) doc.get("status");

                                    AdminOrderModel order = new AdminOrderModel(orderId, itemName, price, quantity, studentName, deliveryAddress, status, imageUrl);
                                    orderList.add(order);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
