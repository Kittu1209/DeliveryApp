package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Admin_DeliveryOrders extends AppCompatActivity {

    RecyclerView recyclerViewOrders;
    TextView noOrdersText;
    List<AdminDeliveredOrderModel> orderList;
    AdminDeliveredOrderAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_delivery_orders);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        noOrdersText = findViewById(R.id.noOrdersText);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new AdminDeliveredOrderAdapter(orderList);
        recyclerViewOrders.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchDeliveredOrders();
    }

    private void fetchDeliveredOrders() {
        db.collection("orders")
                .whereEqualTo("deliveryStatus", "Delivered")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String orderId = doc.getId();
                            String userName = doc.getString("userName");
                            String deliveryAddress = doc.getString("deliveryAddress");

                            List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                            if (items != null && !items.isEmpty()) {
                                for (Map<String, Object> item : items) {
                                    String itemName = (String) item.get("name");
                                    long quantity = (long) item.get("quantity");
                                    double price = item.get("price") instanceof Long ?
                                            ((Long) item.get("price")).doubleValue() :
                                            (Double) item.get("price");

                                    orderList.add(new AdminDeliveredOrderModel(
                                            orderId, userName, itemName, (int) quantity, price, deliveryAddress
                                    ));
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        noOrdersText.setVisibility(View.GONE);
                        recyclerViewOrders.setVisibility(View.VISIBLE);
                    } else {
                        noOrdersText.setVisibility(View.VISIBLE);
                        recyclerViewOrders.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    noOrdersText.setText("Failed to load orders.");
                    noOrdersText.setVisibility(View.VISIBLE);
                    recyclerViewOrders.setVisibility(View.GONE);
                });
    }
}
