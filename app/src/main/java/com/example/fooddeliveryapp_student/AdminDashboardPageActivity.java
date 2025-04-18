package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardPageActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView totalOrders, activeVendors, revenue, deliveryMen;
    private TableLayout ordersContainer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_admin_dashboard_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        initializeViews();
        loadAllData();
    }

    private void initializeViews() {
        totalOrders = findViewById(R.id.totalOrders);
        activeVendors = findViewById(R.id.activeVendors);
        revenue = findViewById(R.id.revenue);
        deliveryMen = findViewById(R.id.deliveryMen);
        ordersContainer = findViewById(R.id.ordersContainer);
    }

    private void loadAllData() {
        loadTodayOrdersCount();
        loadActiveVendorsCount();
        loadTotalRevenue();
        loadAvailableDeliveryMen();
        loadRecentOrders();
    }

    private void loadTodayOrdersCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStart = calendar.getTime();

        db.collection("orders")
                .whereGreaterThanOrEqualTo("createdAt", new Timestamp(todayStart))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        totalOrders.setText(String.valueOf(task.getResult().size()));
                    } else {
                        Log.e("AdminDashboard", "Error getting today's orders", task.getException());
                        totalOrders.setText("0");
                    }
                });
    }

    private void loadActiveVendorsCount() {
        db.collection("Vendors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activeVendors.setText(String.valueOf(task.getResult().size()));
                    } else {
                        Log.e("AdminDashboard", "Error getting vendors", task.getException());
                        activeVendors.setText("0");
                    }
                });
    }

    private void loadTotalRevenue() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStart = calendar.getTime();

        db.collection("payments")
                .whereGreaterThanOrEqualTo("createdAt", new Timestamp(todayStart))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double total = 0;
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Try different ways to get the amount
                                Double amount = null;

                                // Try as Double first
                                if (document.get("amount") instanceof Double) {
                                    amount = document.getDouble("amount");
                                }
                                // Try as Long if stored as integer
                                else if (document.get("amount") instanceof Long) {
                                    amount = document.getLong("amount").doubleValue();
                                }
                                // Try as String if stored that way
                                else if (document.get("amount") instanceof String) {
                                    try {
                                        amount = Double.parseDouble(document.getString("amount"));
                                    } catch (NumberFormatException e) {
                                        Log.e("AdminDashboard", "Error parsing amount as string", e);
                                    }
                                }

                                if (amount != null) {
                                    total += amount;
                                    count++;
                                }
                            } catch (Exception e) {
                                Log.e("AdminDashboard", "Error processing payment document", e);
                            }
                        }
                        Log.d("AdminDashboard", "Found " + count + " successful payments totaling Rs " + total);
                        revenue.setText(String.format(Locale.getDefault(), "Rs %.2f", total));
                    } else {
                        Log.e("AdminDashboard", "Error getting payments", task.getException());
                        revenue.setText("Rs 0");
                    }
                });
    }

    private void loadAvailableDeliveryMen() {
        db.collection("delivery_man")
                .whereEqualTo("admin_control", "active")
                .whereEqualTo("current_duty", "Available")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deliveryMen.setText(String.valueOf(task.getResult().size()));
                    } else {
                        Log.e("AdminDashboard", "Error getting delivery men", task.getException());
                        deliveryMen.setText("0");
                    }
                });
    }

    private void loadRecentOrders() {
        db.collection("orders")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ordersContainer.removeAllViews();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                String orderId = document.getString("orderId");
                                Timestamp createdAt = document.getTimestamp("createdAt");
                                String userId = document.getString("userId");
                                String status = document.getString("status");

                                addOrderRow(orderId, createdAt, userId, status, dateFormat);
                            } catch (Exception e) {
                                Log.e("AdminDashboard", "Error parsing order", e);
                            }
                        }
                    } else {
                        Log.e("AdminDashboard", "Error getting recent orders", task.getException());
                    }
                });
    }

    private void addOrderRow(String orderId, Timestamp createdAt, String userId, String status, SimpleDateFormat dateFormat) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        row.setBackgroundResource(R.drawable.table_row_bg);

        // Order ID
        addDataCell(row, orderId != null ? orderId.substring(0, Math.min(orderId.length(), 8)) : "N/A", 240);

        // Date
        addDataCell(row, createdAt != null ? dateFormat.format(createdAt.toDate()) : "N/A", 310);

        // Customer
        addDataCell(row, userId != null ? userId.substring(0, Math.min(userId.length(), 6)) + "..." : "N/A", 290);

        // Status
        TextView statusCell = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);
        statusCell.setLayoutParams(params);
        statusCell.setText(status != null ? status : "N/A");
        statusCell.setTextSize(14);
        statusCell.setTypeface(null, Typeface.BOLD);

        // Set status color
        if (status != null) {
            switch (status.toLowerCase()) {
                case "completed":
                case "delivered":
                    statusCell.setTextColor(ContextCompat.getColor(this, R.color.green_success));
                    break;
                case "Delivery Man Assigned":
                    statusCell.setTextColor(ContextCompat.getColor(this, R.color.orange_warning));
                    break;
                case "cancelled":
                    statusCell.setTextColor(ContextCompat.getColor(this, R.color.red_error));
                    break;
                default:
                    statusCell.setTextColor(ContextCompat.getColor(this, R.color.gray_default));
            }
        }

        row.addView(statusCell);
        ordersContainer.addView(row);
    }

    private void addDataCell(TableRow row, String text, int width) {
        TextView cell = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(width, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);
        cell.setLayoutParams(params);
        cell.setText(text);
        cell.setTextColor(Color.BLACK);
        cell.setTextSize(14);
        cell.setPadding(8, 8, 8, 8);
        row.addView(cell);
    }
}