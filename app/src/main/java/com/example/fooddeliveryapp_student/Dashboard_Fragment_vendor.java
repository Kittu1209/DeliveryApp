package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Dashboard_Fragment_vendor extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView totalOrdersText, totalRevenueText, shopNameText;
    private LinearLayout ordersTableContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard__vendor, container, false);
        totalOrdersText = view.findViewById(R.id.tvTotalOrders);
        totalRevenueText = view.findViewById(R.id.tvTotalRevenue);
        shopNameText = view.findViewById(R.id.tvShopName);
        ordersTableContainer = view.findViewById(R.id.ordersTableContainer);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkAuthenticationAndFetchData();
    }

    private void checkAuthenticationAndFetchData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            showNotLoggedInMessage();
        } else {
            getVendorData(currentUser);
        }
    }

    private void showNotLoggedInMessage() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                shopNameText.setText("Not logged in");
                totalOrdersText.setText("Not logged in");
                totalRevenueText.setText("Not logged in");
            });
        }
    }

    private void getVendorData(FirebaseUser user) {
        String email = user.getEmail();
        if (email == null) return;

        db.collection("Vendors")
                .whereEqualTo("vendorEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String shopName = task.getResult().getDocuments().get(0).getString("shopName");
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> shopNameText.setText(shopName));
                        }
                        fetchShopIdAndData(user.getUid());
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                shopNameText.setText("No vendor data");
                                totalOrdersText.setText("Error");
                                totalRevenueText.setText("Error");
                            });
                        }
                    }
                });
    }

    private void fetchShopIdAndData(String userId) {
        db.collection("shops")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String shopId = task.getResult().getDocuments().get(0).getId();
                        fetchDashboardData(shopId);
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                totalOrdersText.setText("No shop");
                                totalRevenueText.setText("No shop");
                            });
                        }
                    }
                });
    }

    private void fetchDashboardData(String shopId) {
        fetchOrdersData(shopId);
        fetchRevenueData(shopId);
        fetchDeliveredOrders(shopId);
    }

    private void fetchOrdersData(String shopId) {
        db.collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalOrders = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                            if (items != null) {
                                for (Map<String, Object> item : items) {
                                    String itemShopId = (String) item.get("shopId");
                                    if (shopId.equals(itemShopId)) {
                                        totalOrders++;
                                    }
                                }
                            }
                        }
                        if (getActivity() != null) {
                            int finalTotalOrders = totalOrders;
                            getActivity().runOnUiThread(() -> totalOrdersText.setText(String.valueOf(finalTotalOrders)));
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> totalOrdersText.setText("Error"));
                        }
                    }
                });
    }

//    private void fetchRevenueData(String shopId) {
//        db.collection("orders")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        double totalRevenue = 0.0;
//                        for (QueryDocumentSnapshot doc : task.getResult()) {
//                            List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
//                            if (items != null) {
//                                for (Map<String, Object> item : items) {
//                                    String itemShopId = (String) item.get("shopId");
//                                    if (shopId.equals(itemShopId)) {
//                                        Number price = (Number) item.get("price");
//                                        if (price != null) {
//                                            totalRevenue += price.doubleValue();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        if (getActivity() != null) {
//                            double finalTotalRevenue = totalRevenue;
//                            getActivity().runOnUiThread(() -> totalRevenueText.setText(String.format("₹%.2f", finalTotalRevenue)));
//                        }
//                    } else {
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(() -> totalRevenueText.setText("Error"));
//                        }
//                    }
//                });
//    }
private void fetchRevenueData(String shopId) {
    db.collection("vendor_payments")  // Use vendor_payments collection
            .whereEqualTo("shopId", shopId)  // Filter by shopId
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    double totalRevenue = 0.0;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        // Get the amount field from the vendor_payments collection
                        Number amount = (Number) doc.get("amount");
                        if (amount != null) {
                            totalRevenue += amount.doubleValue();  // Add the amount to totalRevenue
                        }
                    }
                    if (getActivity() != null) {
                        double finalTotalRevenue = totalRevenue;
                        getActivity().runOnUiThread(() ->
                                totalRevenueText.setText(String.format("₹%.2f", finalTotalRevenue))  // Update UI with the total revenue
                        );
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                totalRevenueText.setText("Error")  // In case of error, show an error message
                        );
                    }
                }
            });
}

    private void fetchDeliveredOrders(String shopId) {
        db.collection("orders")
                .whereIn("status", Arrays.asList("delivered", "Delivered"))
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (getActivity() == null) return;

                    getActivity().runOnUiThread(() -> {
                        ordersTableContainer.removeAllViews();

                        if (task.isSuccessful()) {
                            boolean hasOrders = false;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                                if (items != null) {
                                    for (Map<String, Object> item : items) {
                                        String itemShopId = (String) item.get("shopId");
                                        if (shopId.equals(itemShopId)) {
                                            hasOrders = true;
                                            addOrderRow(doc.getId(), doc.getString("status"));
                                            break;
                                        }
                                    }
                                }
                            }

                            if (!hasOrders) {
                                TextView empty = new TextView(getContext());
                                empty.setText("No delivered orders");
                                ordersTableContainer.addView(empty);
                            }
                        } else {
                            TextView errorView = new TextView(getContext());
                            errorView.setText("Error loading orders");
                            ordersTableContainer.addView(errorView);
                        }
                    });
                });
    }

    private void addOrderRow(String orderId, String status) {
        TextView row = new TextView(getContext());
        row.setText("Order ID: " + orderId + " | Status: " + status);
        ordersTableContainer.addView(row);
    }
}
