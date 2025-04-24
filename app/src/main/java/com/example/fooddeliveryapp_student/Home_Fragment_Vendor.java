package com.example.fooddeliveryapp_student;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home_Fragment_Vendor extends Fragment {

    private static final String TAG = "HomeFragmentVendor";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private TextView textShopName, textMenuItemsCount, textEarningsAmount, textTodaysOrdersCount;
    private TableLayout ordersTable;
    private ToggleButton toggleMute;
    private Button refreshButton;

    private FirebaseFirestore db;
    private CollectionReference productsRef, paymentsRef, ordersRef, shopsRef;
    private String currentShopId;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean isMuted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home___vendor, container, false);
        initializeViews(view);
        setupClickListeners(view);
        initializeNotificationSystem();

        if (checkPlayServices()) {
            initializeFirebase();
            fetchAllData();
        }

        return view;
    }

    private void initializeViews(View view) {
        textShopName = view.findViewById(R.id.textShopName);
        textMenuItemsCount = view.findViewById(R.id.textMenuItemsCount);
        textEarningsAmount = view.findViewById(R.id.textEarningsAmount);
        textTodaysOrdersCount = view.findViewById(R.id.textTodaysOrdersCount);
        ordersTable = view.findViewById(R.id.ordersTable);
        toggleMute = view.findViewById(R.id.toggleMute);
        refreshButton = view.findViewById(R.id.refreshButton);
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.textManageOrdersSubtitle).setOnClickListener(v ->
                navigateToFragment(new Orders_Fragment_Vendor()));

        view.findViewById(R.id.cardAddItem).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddProductActivity.class)));

        view.findViewById(R.id.cardAnalytics).setOnClickListener(v ->
                navigateToFragment(new Dashboard_Fragment_vendor()));

        view.findViewById(R.id.cardReviews).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VendorReviewActivity.class);
            intent.putExtra("shopId", currentShopId);
            startActivity(intent);
        });

        refreshButton.setOnClickListener(v -> refreshOrdersTable());
    }

    private void initializeNotificationSystem() {
        try {
            vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.cha_ching);
            toggleMute.setOnCheckedChangeListener((buttonView, isChecked) -> isMuted = isChecked);
        } catch (Exception e) {
            Log.e(TAG, "Notification system initialization failed", e);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(requireContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(requireActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(requireContext(), "This device is not supported.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
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

    private void fetchAllData() {
        fetchShopName();
        fetchMenuItemsCount();
        fetchTodaysEarnings();
        fetchTodaysOrdersCount();
        refreshOrdersTable();
    }

    private void fetchShopName() {
        shopsRef.document(currentShopId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String name = task.getResult().getString("name");
                        textShopName.setText(name != null ? name : "My Shop");
                    } else {
                        Log.e(TAG, "Error fetching shop name", task.getException());
                    }
                });
    }

    private void fetchMenuItemsCount() {
        productsRef.whereEqualTo("shopId", currentShopId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        textMenuItemsCount.setText(String.valueOf(task.getResult().size()));
                    } else {
                        Log.e(TAG, "Error fetching menu items", task.getException());
                    }
                });
    }

    private void fetchTodaysEarnings() {
        Date[] dateRange = getTodayDateRange();

        paymentsRef.whereEqualTo("shopId", currentShopId)
                .whereGreaterThanOrEqualTo("createdAt", dateRange[0])
                .whereLessThanOrEqualTo("createdAt", dateRange[1])
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double total = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Double amount = doc.getDouble("amount");
                            if (amount != null) total += amount;
                        }
                        textEarningsAmount.setText(formatCurrency(total));
                    } else {
                        Log.e(TAG, "Error fetching earnings", task.getException());
                        textEarningsAmount.setText("Rs. 0.00");
                    }
                });
    }

    private void fetchTodaysOrdersCount() {
        Date[] dateRange = getTodayDateRange();

        ordersRef.whereGreaterThanOrEqualTo("createdAt", dateRange[0])
                .whereLessThanOrEqualTo("createdAt", dateRange[1])
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                            if (items != null) {
                                for (Map<String, Object> item : items) {
                                    if (currentShopId.equals(item.get("shopId"))) {
                                        count++;
                                        break;
                                    }
                                }
                            }
                        }
                        textTodaysOrdersCount.setText(String.valueOf(count));
                    } else {
                        Log.e(TAG, "Error fetching today's orders", task.getException());
                        textTodaysOrdersCount.setText("0");
                    }
                });
    }

    private void refreshOrdersTable() {
        ordersTable.removeAllViews();
        addTableHeader();

        ordersRef
                .whereGreaterThanOrEqualTo("createdAt", getTodayDateRange()[0])
                .whereLessThanOrEqualTo("createdAt", getTodayDateRange()[1])
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean hasOrders = false;

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String status = doc.getString("status");
                            if (status != null && !status.equalsIgnoreCase("Delivered")) {
                                List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                                String orderId = doc.getString("orderId");

                                if (items != null) {
                                    for (Map<String, Object> item : items) {
                                        String shopId = (String) item.get("shopId");
                                        if (shopId != null && shopId.equals(currentShopId)) {
                                            String name = (String) item.get("name");
                                            Number quantity = (Number) item.get("quantity");

                                            addOrderToTable(
                                                    orderId != null ? orderId.substring(0, Math.min(orderId.length(), 6)) : "N/A",
                                                    name != null ? name : "N/A",
                                                    quantity != null ? quantity.toString() : "0"
                                            );
                                            hasOrders = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (!hasOrders) {
                            showEmptyTableMessage("No pending orders");
                        }
                    } else {
                        showEmptyTableMessage("Failed to load orders");
                        Log.e(TAG, "Error fetching orders", task.getException());
                    }
                });
    }


    private void processOrderDocument(QueryDocumentSnapshot doc) {
        try {
            String orderId = doc.getString("orderId");
            List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");

            if (items != null) {
                for (Map<String, Object> item : items) {
                    String shopId = (String) item.get("shopId");
                    if (currentShopId.equals(shopId)) {
                        String productName = (String) item.get("name");
                        Number quantity = (Number) item.get("quantity");

                        addOrderToTable(
                                orderId != null ? orderId.substring(0, Math.min(6, orderId.length())) : "N/A",
                                productName != null ? productName : "N/A",
                                quantity != null ? quantity.toString() : "0"
                        );
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing order", e);
        }
    }

    private void addTableHeader() {
        TableRow headerRow = new TableRow(requireContext());
        addHeaderCell(headerRow, "Order ID");
        addHeaderCell(headerRow, "Product");
        addHeaderCell(headerRow, "Qty");
        ordersTable.addView(headerRow);
    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextAppearance(requireContext(), android.R.style.TextAppearance_Medium);
        row.addView(textView);
    }

    private void addOrderToTable(String orderId, String productName, String quantity) {
        TableRow row = new TableRow(requireContext());
        addDataCell(row, orderId);
        addDataCell(row, productName);
        addDataCell(row, quantity);
        ordersTable.addView(row);
    }

    private void addDataCell(TableRow row, String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        row.addView(textView);
    }

    private void showEmptyTableMessage(String message) {
        TableRow row = new TableRow(requireContext());
        TextView textView = new TextView(requireContext());
        textView.setText(message);
        textView.setPadding(16, 16, 16, 16);
        row.addView(textView);
        ordersTable.addView(row);
    }

    private Date[] getTodayDateRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date start = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date end = calendar.getTime();

        return new Date[]{start, end};
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
