package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class Home_Fragment_Vendor extends Fragment {

    private static final String TAG = "HomeFragmentVendor";
    private TextView textMenuItemsCount, textEarningsAmount, textShopName, textTodaysOrdersCount;
    private ToggleButton toggleMute;
    private boolean isMuted = false;

    private FirebaseFirestore db;
    private CollectionReference productsRef, paymentsRef, ordersRef, shopsRef;
    private String currentShopId;
    private ListenerRegistration orderListener;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private RecyclerView recyclerRecentOrders;
    private RecentOrdersAdapter recentOrdersAdapter;
    private List<OrderVendorHome> recentOrdersList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            currentShopId = auth.getCurrentUser().getUid();
            db = FirebaseFirestore.getInstance();
            productsRef = db.collection("products");
            paymentsRef = db.collection("vendor_payments");
            ordersRef = db.collection("orders");
            shopsRef = db.collection("shops");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home___vendor, container, false);

        // Initialize views
        textMenuItemsCount = view.findViewById(R.id.textMenuItemsCount);
        textEarningsAmount = view.findViewById(R.id.textEarningsAmount);
        toggleMute = view.findViewById(R.id.toggleMute);
        textShopName = view.findViewById(R.id.textShopName);
        textTodaysOrdersCount = view.findViewById(R.id.textTodaysOrdersCount);
        recyclerRecentOrders = view.findViewById(R.id.recyclerRecentOrders);
       // manageorders=view.findViewById(R.id.textManageOrdersTitle);
        // Setup RecyclerView for recent orders
        recyclerRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        recentOrdersAdapter = new RecentOrdersAdapter(recentOrdersList);
        recyclerRecentOrders.setAdapter(recentOrdersAdapter);
        view.findViewById(R.id.textManageOrdersSubtitle).setOnClickListener(v -> {
            navigateToFragment(new Orders_Fragment_Vendor());
        });
        // Set click listeners for navigation
        view.findViewById(R.id.cardAddItem).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddProductActivity.class));
        });

        view.findViewById(R.id.cardAnalytics).setOnClickListener(v -> {
            navigateToFragment(new Dashboard_Fragment_vendor());
        });

        view.findViewById(R.id.cardReviews).setOnClickListener(v -> {
            if (isAdded() && getActivity() != null) {
                Intent intent = new Intent(getActivity(), VendorReviewActivity.class);
                intent.putExtra("shopId", currentShopId);
                startActivity(intent);
            }
        });

        // Initialize media and vibration
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.cha_ching);

        toggleMute.setOnCheckedChangeListener((buttonView, isChecked) -> isMuted = !isChecked);

        // Fetch all data
        fetchShopName();
        fetchMenuItemsCount();
        fetchEarningsAmount();
        fetchTodaysOrders();
        fetchRecentOrders();
        setupRealTimeOrderListener();

        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        try {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Fragment navigation failed", e);
            Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchShopName() {
        shopsRef.document(currentShopId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String shopName = documentSnapshot.getString("name");
                        textShopName.setText(shopName != null ? shopName : "My Shop");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching shop name", e));
    }

    private void fetchMenuItemsCount() {
        productsRef.whereEqualTo("shopId", currentShopId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    textMenuItemsCount.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching menu items", e));
    }

    private void fetchEarningsAmount() {
        // Get today's date range
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        final Date startDate = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        final Date endDate = calendar.getTime();

        paymentsRef.whereEqualTo("shopId", currentShopId)
                .whereGreaterThanOrEqualTo("timestamp", startDate)
                .whereLessThanOrEqualTo("timestamp", endDate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    double total = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        // Check multiple possible amount field names
                        Double amount = null;
                        if (doc.contains("amount")) {
                            amount = doc.getDouble("amount");
                        } else if (doc.contains("totalAmount")) {
                            amount = doc.getDouble("totalAmount");
                        } else if (doc.contains("total")) {
                            amount = doc.getDouble("total");
                        }

                        if (amount != null) {
                            total += amount;
                        }
                    }

                    // Format the amount with currency symbol
                    String formattedAmount = NumberFormat.getCurrencyInstance(new Locale("en", "IN"))
                            .format(total);

                    // Update UI
                    if (textEarningsAmount != null) {
                        textEarningsAmount.setText(formattedAmount);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching earnings", e);
                    if (textEarningsAmount != null) {
                        textEarningsAmount.setText("₹0");
                    }
                });
    }

//    private void fetchTodaysOrders() {
//    Calendar calendar = Calendar.getInstance();
//    calendar.set(Calendar.HOUR_OF_DAY, 0);
//    calendar.set(Calendar.MINUTE, 0);
//    calendar.set(Calendar.SECOND, 0);
//    final Date startDate = calendar.getTime();
//
//    calendar.set(Calendar.HOUR_OF_DAY, 23);
//    calendar.set(Calendar.MINUTE, 59);
//    calendar.set(Calendar.SECOND, 59);
//    final Date endDate = calendar.getTime();
//
//    ordersRef.whereEqualTo("shopId", currentShopId)
//            .whereIn("status", Arrays.asList("Delivery Man Assigned", "pending"))
//            .whereGreaterThanOrEqualTo("createdAt", startDate)
//            .whereLessThanOrEqualTo("createdAt", endDate)
//            .get()
//            .addOnSuccessListener(querySnapshot -> {
//                textTodaysOrdersCount.setText(String.valueOf(querySnapshot.size()));
//            })
//            .addOnFailureListener(e -> {
//                Log.w(TAG, "Indexed query failed, falling back to client-side filtering", e);
//                ordersRef.whereEqualTo("shopId", currentShopId)
//                        .whereIn("status", Arrays.asList("Delivery Man Assigned", "pending"))
//                        .get()
//                        .addOnSuccessListener(querySnapshot -> {
//                            int count = 0;
//                            for (QueryDocumentSnapshot doc : querySnapshot) {
//                                Date createdAt = doc.getDate("createdAt");
//                                if (createdAt != null &&
//                                        !createdAt.before(startDate) &&
//                                        !createdAt.after(endDate)) {
//                                    count++;
//                                }
//                            }
//                            textTodaysOrdersCount.setText(String.valueOf(count));
//                        })
//                        .addOnFailureListener(e2 -> {
//                            Log.e(TAG, "Error fetching orders", e2);
//                            textTodaysOrdersCount.setText("0");
//                        });
//            });
//}
private void fetchTodaysOrders() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    final Date startDate = calendar.getTime();

    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    final Date endDate = calendar.getTime();

    Log.d(TAG, "Fetching orders between: " + startDate + " and " + endDate);

    ordersRef.whereEqualTo("shopId", currentShopId)
            .whereIn("status", Arrays.asList("Delivery Man Assigned", "pending"))
            .whereGreaterThanOrEqualTo("createdAt", startDate)
            .whereLessThanOrEqualTo("createdAt", endDate)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                Log.d(TAG, "Found " + querySnapshot.size() + " orders today");
                textTodaysOrdersCount.setText(String.valueOf(querySnapshot.size()));
            })
            .addOnFailureListener(e -> {
                Log.w(TAG, "Indexed query failed, falling back to client-side filtering", e);
                ordersRef.whereEqualTo("shopId", currentShopId)
                        .whereIn("status", Arrays.asList("Delivery Man Assigned", "pending"))
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            int count = 0;
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Date createdAt = doc.getDate("createdAt");
                                if (createdAt != null &&
                                        !createdAt.before(startDate) &&
                                        !createdAt.after(endDate)) {
                                    count++;
                                }
                            }
                            Log.d(TAG, "Client-side filtered count: " + count);
                            textTodaysOrdersCount.setText(String.valueOf(count));
                        })
                        .addOnFailureListener(e2 -> {
                            Log.e(TAG, "Error fetching orders", e2);
                            textTodaysOrdersCount.setText("0");
                        });
            });
}
    private void fetchRecentOrders() {
        ordersRef.whereEqualTo("shopId", currentShopId)
                .whereEqualTo("status", Arrays.asList("pending", "Delivery Man Assigned"))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    recentOrdersList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        OrderVendorHome order = doc.toObject(OrderVendorHome.class);
                        if (order != null) {
                            order.setId(doc.getId());
                            recentOrdersList.add(order);
                        }
                    }
                    recentOrdersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching recent orders", e));
    }

    private void markOrderAsPacked(OrderVendorHome order) {
        ordersRef.document(order.getId())
                .update("status", "Order Packed")
                .addOnSuccessListener(aVoid -> {
                    updateStockQuantities(order);
                    fetchRecentOrders();
                    Toast.makeText(getContext(), "Order marked as packed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update order status", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating order status", e);
                });
    }

    private void updateStockQuantities(OrderVendorHome order) {
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getShopId().equals(currentShopId)) {
                    productsRef.document(item.getProductId())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    long currentStock = documentSnapshot.getLong("stockQuantity");
                                    long newStock = currentStock - item.getQuantity();

                                    productsRef.document(item.getProductId())
                                            .update("stockQuantity", newStock)
                                            .addOnFailureListener(e ->
                                                    Log.e(TAG, "Error updating stock for product: " + item.getProductId(), e));
                                }
                            })
                            .addOnFailureListener(e ->
                                    Log.e(TAG, "Error fetching product: " + item.getProductId(), e));
                }
            }
        }
    }

    private void setupRealTimeOrderListener() {
        orderListener = ordersRef
                .whereEqualTo("status", Arrays.asList("pending", "Delivery Man Assigned"))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshots != null && !snapshots.isEmpty()) {
                            boolean newOrder = false;
                            for (QueryDocumentSnapshot doc : snapshots) {
                                if (doc.contains("items")) {
                                    for (Object itemObj : (Iterable<?>) doc.get("items")) {
                                        if (itemObj instanceof Map) {
                                            Map<?, ?> item = (Map<?, ?>) itemObj;
                                            if (currentShopId.equals(item.get("shopId"))) {
                                                newOrder = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (newOrder && !isMuted) {
                                triggerNotification();
                                fetchTodaysOrders();
                                fetchRecentOrders();
                            }
                        }
                    }
                });
    }

    private void triggerNotification() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(300);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (orderListener != null) {
            orderListener.remove();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private class RecentOrdersAdapter extends RecyclerView.Adapter<RecentOrdersAdapter.OrderViewHolder> {
        private List<OrderVendorHome> orders;

        public RecentOrdersAdapter(List<OrderVendorHome> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_packing, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            OrderVendorHome order = orders.get(position);
            holder.bind(order);
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView textOrderId, textCustomerName, textCustomerAddress, textOrderTotal, textOrderTime;
            Button btnMarkAsPacked;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                textOrderId = itemView.findViewById(R.id.textOrderId);
                textCustomerName = itemView.findViewById(R.id.textCustomerName);
                textCustomerAddress = itemView.findViewById(R.id.textCustomerAddress);
                textOrderTotal = itemView.findViewById(R.id.textOrderTotal);
                textOrderTime = itemView.findViewById(R.id.textOrderTime);
                btnMarkAsPacked = itemView.findViewById(R.id.btnMarkAsPacked);
            }

            public void bind(OrderVendorHome order) {
                textOrderId.setText("#" + (order.getOrderId() != null ? order.getOrderId().substring(0, Math.min(6, order.getOrderId().length())) : ""));
                textCustomerName.setText(order.getCustomerName());
                textCustomerAddress.setText("Hostel: " + order.getCustomerHostel() + ", Room: " + order.getCustomerRoom());
                textOrderTotal.setText("Total: ₹" + order.getTotalAmount());
                textOrderTime.setText(formatTime(order.getCreatedAt()));

                btnMarkAsPacked.setOnClickListener(v -> {
                    markOrderAsPacked(order);
                });
            }

            private String formatTime(Date date) {
                if (date == null) return "";
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return sdf.format(date);
            }
        }
    }
}