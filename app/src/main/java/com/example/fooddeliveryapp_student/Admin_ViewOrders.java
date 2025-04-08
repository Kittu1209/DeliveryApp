package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);

        recyclerView = findViewById(R.id.recyclerViewAdminOrders);
        orderList = new ArrayList<>();
        adapter = new AdminOrderAdapter(this, orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchAllOrders();
    }

    private void fetchAllOrders() {
        FirebaseFirestore.getInstance().collection("orders")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                        if (items != null) {
                            for (Map<String, Object> item : items) {
                                try {
                                    String itemName = item.get("name") != null ? item.get("name").toString() : "Unnamed Item";
                                    double price = item.get("price") instanceof Number ? ((Number) item.get("price")).doubleValue() : 0.0;
                                    int quantity = item.get("quantity") instanceof Number ? ((Number) item.get("quantity")).intValue() : 1;

                                    // Decode Base64 imageUrl
                                    String base64Image = item.get("imageUrl") != null ? item.get("imageUrl").toString() : "";
                                    String decodedImage = "";
                                    if (!base64Image.isEmpty()) {
                                        // Remove data:image/... if present
                                        if (base64Image.contains(",")) {
                                            base64Image = base64Image.split(",")[1];
                                        }
                                        decodedImage = base64Image; // Just pass raw base64; adapter will decode
                                    }

                                    Map<String, Object> address = (Map<String, Object>) doc.get("deliveryAddress");
                                    String studentName = address != null && address.get("name") != null ? address.get("name").toString() : "Unknown";
                                    String hostel = address != null && address.get("hostel") != null ? address.get("hostel").toString() : "N/A";
                                    String room = address != null && address.get("room") != null ? address.get("room").toString() : "N/A";
                                    String deliveryAddress = hostel + ", Room " + room;

                                    String orderId = doc.get("orderId") != null ? doc.get("orderId").toString() : "No ID";
                                    String status = doc.get("status") != null ? doc.get("status").toString() : "Pending";

                                    AdminOrderModel order = new AdminOrderModel(orderId, itemName, price, quantity, studentName, deliveryAddress, status, decodedImage);
                                    orderList.add(order);
                                } catch (Exception e) {
                                    Log.e("Admin_ViewOrders", "Error parsing order item: " + e.getMessage());
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Admin_ViewOrders", "Error fetching orders: " + e.getMessage());
                });
    }
}
