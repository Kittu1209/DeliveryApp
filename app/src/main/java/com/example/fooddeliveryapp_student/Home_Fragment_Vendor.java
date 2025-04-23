package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
        initializeFirebase();
    }

    private void initializeFirebase() {
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
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners(view);
        initializeNotificationSystem();
        fetchAllData();
        return view;
    }

    private void initializeViews(View view) {
        textMenuItemsCount = view.findViewById(R.id.textMenuItemsCount);
        textEarningsAmount = view.findViewById(R.id.textEarningsAmount);
        toggleMute = view.findViewById(R.id.toggleMute);
        textShopName = view.findViewById(R.id.textShopName);
        textTodaysOrdersCount = view.findViewById(R.id.textTodaysOrdersCount);
        recyclerRecentOrders = view.findViewById(R.id.recyclerRecentOrders);
    }

    private void setupRecyclerView() {
        recyclerRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        recentOrdersAdapter = new RecentOrdersAdapter(recentOrdersList);
        recyclerRecentOrders.setAdapter(recentOrdersAdapter);
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.textManageOrdersSubtitle).setOnClickListener(v ->
                navigateToFragment(new Orders_Fragment_Vendor()));

        view.findViewById(R.id.cardAddItem).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddProductActivity.class)));

        view.findViewById(R.id.cardAnalytics).setOnClickListener(v ->
                navigateToFragment(new Dashboard_Fragment_vendor()));

        view.findViewById(R.id.cardReviews).setOnClickListener(v -> {
            if (isAdded() && getActivity() != null) {
                Intent intent = new Intent(getActivity(), VendorReviewActivity.class);
                intent.putExtra("shopId", currentShopId);
                startActivity(intent);
            }
        });
    }

    private void initializeNotificationSystem() {
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.cha_ching);
        toggleMute.setOnCheckedChangeListener((buttonView, isChecked) -> isMuted = !isChecked);
    }

    private void fetchAllData() {
        fetchShopName();
        fetchMenuItemsCount();
        fetchEarningsAmount();
        fetchTodaysOrders();
        fetchRecentOrders();
        setupRealTimeOrderListener();
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
        Date[] dateRange = getTodayDateRange();

        paymentsRef.whereEqualTo("shopId", currentShopId)
                .whereGreaterThanOrEqualTo("createdAt", dateRange[0])
                .whereLessThanOrEqualTo("createdAt", dateRange[1])
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    double total = calculateTotalEarnings(querySnapshot);
                    String formattedAmount = formatCurrency(total);
                    textEarningsAmount.setText(formattedAmount);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching earnings, trying fallback query", e);
                    fetchEarningsFallback();
                });
    }

    private Date[] getTodayDateRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();

        return new Date[]{startDate, endDate};
    }

    private double calculateTotalEarnings(QuerySnapshot querySnapshot) {
        double total = 0;
        for (QueryDocumentSnapshot doc : querySnapshot) {
            Double amount = doc.getDouble("amount");
            if (amount != null) {
                total += amount;
            }
        }
        return total;
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(amount);
    }

    private void fetchEarningsFallback() {
        paymentsRef.whereEqualTo("shopId", currentShopId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    double total = calculateTotalEarnings(querySnapshot);
                    textEarningsAmount.setText(formatCurrency(total));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Fallback query failed", e);
                    textEarningsAmount.setText("₹0");
                });
    }

    private void fetchTodaysOrders() {
        Date[] dateRange = getTodayDateRange();

        ordersRef.whereGreaterThanOrEqualTo("createdAt", dateRange[0])
                .whereLessThanOrEqualTo("createdAt", dateRange[1])
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = countOrdersForShop(querySnapshot);
                    textTodaysOrdersCount.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching today's orders, trying fallback", e);
                    fetchTodaysOrdersFallback();
                });
    }

    private int countOrdersForShop(QuerySnapshot querySnapshot) {
        int count = 0;
        for (QueryDocumentSnapshot doc : querySnapshot) {
            if (hasItemsFromCurrentShop(doc)) {
                count++;
            }
        }
        return count;
    }

    private boolean hasItemsFromCurrentShop(QueryDocumentSnapshot doc) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
        if (items != null) {
            for (Map<String, Object> item : items) {
                String shopId = (String) item.get("shopId");
                if (currentShopId.equals(shopId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void fetchTodaysOrdersFallback() {
        ordersRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Date createdAt = doc.getDate("createdAt");
                        if (createdAt != null && isToday(createdAt) && hasItemsFromCurrentShop(doc)) {
                            count++;
                        }
                    }
                    textTodaysOrdersCount.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Fallback query failed", e);
                    textTodaysOrdersCount.setText("0");
                });
    }

    private boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR);
    }

    private void fetchRecentOrders() {
        ordersRef.whereIn("status", Arrays.asList("pending", "Delivery Man Assigned"))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    processRecentOrders(querySnapshot);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching recent orders", e);
                    fetchRecentOrdersFallback();
                });
    }

    private void processRecentOrders(QuerySnapshot querySnapshot) {
        recentOrdersList.clear();
        for (QueryDocumentSnapshot doc : querySnapshot) {
            try {
                OrderVendorHome order = doc.toObject(OrderVendorHome.class);
                order.setId(doc.getId());
                extractOrderItems(doc, order);

                if (!order.getItems().isEmpty()) {
                    recentOrdersList.add(order);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error mapping order document: " + doc.getId(), e);
            }
        }
        recentOrdersAdapter.notifyDataSetChanged();

        if (recentOrdersList.isEmpty()) {
            Log.d(TAG, "No recent orders found for this shop");
        }
    }

    private void extractOrderItems(QueryDocumentSnapshot doc, OrderVendorHome order) {
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) doc.get("items");
        List<OrderItem> shopItems = new ArrayList<>();

        if (itemsMap != null) {
            for (Map<String, Object> itemMap : itemsMap) {
                String shopId = (String) itemMap.get("shopId");
                if (currentShopId.equals(shopId)) {
                    OrderItem orderItem = createOrderItem(itemMap, shopId);
                    shopItems.add(orderItem);
                }
            }
        }
        order.setItems(shopItems);
    }

    private OrderItem createOrderItem(Map<String, Object> itemMap, String shopId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId((String) itemMap.get("productId"));
        orderItem.setName((String) itemMap.get("name"));
        orderItem.setPrice(((Number) itemMap.get("price")).doubleValue());
        orderItem.setQuantity(((Number) itemMap.get("quantity")).intValue());
        orderItem.setShopId(shopId);
        return orderItem;
    }

    private void fetchRecentOrdersFallback() {
        ordersRef.orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    recentOrdersList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String status = doc.getString("status");
                        if ("pending".equals(status) || "Delivery Man Assigned".equals(status)) {
                            OrderVendorHome order = doc.toObject(OrderVendorHome.class);
                            order.setId(doc.getId());
                            extractOrderItems(doc, order);

                            if (!order.getItems().isEmpty()) {
                                recentOrdersList.add(order);
                            }
                        }
                    }
                    recentOrdersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Fallback query failed", e);
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                });
    }

    private void markOrderAsPacked(OrderVendorHome order) {
        ordersRef.document(order.getId())
                .update("status", "Order Packed")
                .addOnSuccessListener(aVoid -> {
                    updateStockQuantities(order);
                    refreshDataAfterPacking();
                    Toast.makeText(getContext(), "Order marked as packed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update order status", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating order status", e);
                });
    }

    private void refreshDataAfterPacking() {
        fetchRecentOrders();
        fetchTodaysOrders();
        fetchEarningsAmount();
    }

    private void updateStockQuantities(OrderVendorHome order) {
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getShopId().equals(currentShopId)) {
                    updateProductStock(item);
                }
            }
        }
    }

    private void updateProductStock(OrderItem item) {
        productsRef.document(item.getProductId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        long currentStock = documentSnapshot.getLong("stockQuantity");
                        long newStock = currentStock - item.getQuantity();
                        updateStockInFirestore(item.getProductId(), newStock);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error fetching product: " + item.getProductId(), e));
    }

    private void updateStockInFirestore(String productId, long newStock) {
        productsRef.document(productId)
                .update("stockQuantity", newStock)
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error updating stock for product: " + productId, e));
    }

    private void setupRealTimeOrderListener() {
        orderListener = ordersRef
                .whereIn("status", Arrays.asList("pending", "Delivery Man Assigned"))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed.", e);
                            return;
                        }

                        if (hasNewOrdersForShop(snapshots)) {
                            triggerNotification();
                            refreshAllData();
                        }
                    }
                });
    }

    private boolean hasNewOrdersForShop(QuerySnapshot snapshots) {
        if (snapshots != null && !snapshots.isEmpty()) {
            for (QueryDocumentSnapshot doc : snapshots) {
                if (hasItemsFromCurrentShop(doc)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void refreshAllData() {
        fetchTodaysOrders();
        fetchRecentOrders();
        fetchEarningsAmount();
    }

    private void triggerNotification() {
        if (isMuted) return;

        try {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(300);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error triggering notification", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleanupResources();
    }

    private void cleanupResources() {
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
            bindOrderToViewHolder(holder, order);
        }

        private void bindOrderToViewHolder(OrderViewHolder holder, OrderVendorHome order) {
            holder.textOrderId.setText(formatOrderId(order.getOrderId()));
            holder.textCustomerName.setText(order.getCustomerName());
            holder.textCustomerAddress.setText(formatCustomerAddress(order));
            holder.textOrderTotal.setText(formatOrderTotal(order));
            holder.textOrderTime.setText(formatOrderTime(order.getCreatedAt()));
            holder.btnMarkAsPacked.setOnClickListener(v -> markOrderAsPacked(order));
        }

        private String formatOrderId(String orderId) {
            return "#" + (orderId != null ?
                    orderId.substring(0, Math.min(6, orderId.length())) : "N/A");
        }

        private String formatCustomerAddress(OrderVendorHome order) {
            return "Hostel: " + order.getCustomerHostel() + ", Room: " + order.getCustomerRoom();
        }

        private String formatOrderTotal(OrderVendorHome order) {
            double total = calculateOrderTotal(order);
            return "Total: ₹" + total;
        }

        private double calculateOrderTotal(OrderVendorHome order) {
            double total = 0;
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    total += item.getPrice() * item.getQuantity();
                }
            }
            return total;
        }

        private String formatOrderTime(Date date) {
            if (date == null) return "N/A";
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return sdf.format(date);
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
        }
    }
}
