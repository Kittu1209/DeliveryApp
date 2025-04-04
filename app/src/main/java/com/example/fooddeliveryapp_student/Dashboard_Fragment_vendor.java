package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;

public class Dashboard_Fragment_vendor extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView totalOrdersText, totalRevenueText;
    private String currentShopId;

    private static final String TAG = "DashboardVendor";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard__vendor, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        totalOrdersText = view.findViewById(R.id.tvTotalOrders);
        totalRevenueText = view.findViewById(R.id.tvTotalRevenue);

        getShopIdForCurrentUser();

        return view;
    }

    private void getShopIdForCurrentUser() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "User not logged in.");
            totalOrdersText.setText("Not logged in");
            totalRevenueText.setText("Not logged in");
            return;
        }

        String userId = currentUser.getUid();

        db.collection("shops")
                .whereEqualTo("ownerId", userId) // or use "userId" if that's the field name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        currentShopId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Log.d(TAG, "Found shopId: " + currentShopId);
                        fetchOrdersData();
                    } else {
                        Log.d(TAG, "No shop found for this user.");
                        totalOrdersText.setText("No shop");
                        totalRevenueText.setText("No shop");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch shopId: ", e);
                    totalOrdersText.setText("Error");
                    totalRevenueText.setText("Error");
                });
    }

    private void fetchOrdersData() {
        db.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalOrders = 0;
                    double totalRevenue = 0.0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) document.get("items");

                        if (items != null) {
                            for (Map<String, Object> item : items) {
                                String shopId = (String) item.get("shopId");
                                if (shopId != null && shopId.equals(currentShopId)) {
                                    totalOrders++;

                                    Number priceNum = (Number) item.get("price");
                                    Number quantityNum = (Number) item.get("quantity");

                                    double price = priceNum != null ? priceNum.doubleValue() : 0.0;
                                    int quantity = quantityNum != null ? quantityNum.intValue() : 0;

                                    double itemTotal = price * quantity;
                                    totalRevenue += itemTotal;

                                    Log.d(TAG, "Matched item: " + item + ", Total: " + itemTotal);
                                }
                            }
                        }
                    }

                    totalOrdersText.setText(String.valueOf(totalOrders));
                    totalRevenueText.setText("₹" + totalRevenue);
                    Log.d(TAG, "Final Total Orders: " + totalOrders + ", Revenue: ₹" + totalRevenue);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching orders: ", e);
                    totalOrdersText.setText("Error");
                    totalRevenueText.setText("Error");
                });
    }
}
