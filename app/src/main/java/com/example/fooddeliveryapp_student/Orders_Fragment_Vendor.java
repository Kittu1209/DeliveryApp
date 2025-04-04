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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        String currentVendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Step 1: Get shopIds where ownerId == currentVendorId
        db.collection("Shops").whereEqualTo("ownerId", currentVendorId).get().addOnSuccessListener(shopSnapshots -> {
            Set<String> vendorShopIds = new HashSet<>();
            for (QueryDocumentSnapshot shopDoc : shopSnapshots) {
                String shopId = shopDoc.getId(); // assuming doc ID is shopId
                vendorShopIds.add(shopId);
            }

            if (vendorShopIds.isEmpty()) {
                Toast.makeText(getContext(), "No shops found for this vendor", Toast.LENGTH_SHORT).show();
            } else {
                fetchOrdersForShops(vendorShopIds);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch shop data", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void fetchOrdersForShops(Set<String> vendorShopIds) {
        db.collection("orders").get().addOnSuccessListener(queryDocumentSnapshots -> {
            orderList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) doc.get("items");

                boolean isOrderForVendor = false;
                List<ItemModel> vendorItems = new ArrayList<>();

                if (itemsRaw != null) {
                    for (Map<String, Object> itemMap : itemsRaw) {
                        String itemShopId = (String) itemMap.get("shopId");
                        if (vendorShopIds.contains(itemShopId)) {
                            isOrderForVendor = true;

                            String itemName = (String) itemMap.get("name");
                            double price = itemMap.get("price") instanceof Number ? ((Number) itemMap.get("price")).doubleValue() : 0;
                            int qty = itemMap.get("quantity") instanceof Number ? ((Number) itemMap.get("quantity")).intValue() : 0;

                            vendorItems.add(new ItemModel(itemName, price, qty));
                        }
                    }
                }

                if (isOrderForVendor) {
                    String orderId = doc.getString("orderId");
                    String userId = doc.getString("userId");
                    String status = doc.getString("status");
                    double totalAmount = doc.getDouble("totalAmount") != null ? doc.getDouble("totalAmount") : 0;
                    Timestamp createdAt = doc.getTimestamp("createdAt");

                    // Fetch address
                    Map<String, Object> addressMap = (Map<String, Object>) doc.get("deliveryAddress");
                    String name = "", phone = "", hostel = "", room = "";
                    if (addressMap != null) {
                        name = (String) addressMap.get("name");
                        phone = (String) addressMap.get("phone");
                        hostel = (String) addressMap.get("hostel");
                        room = (String) addressMap.get("room");
                    }

                    order_model_vendor orderModel = new order_model_vendor(orderId, userId, name, phone, hostel, room, status, totalAmount, createdAt, vendorItems);
                    orderList.add(orderModel);
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
        });
    }
}
