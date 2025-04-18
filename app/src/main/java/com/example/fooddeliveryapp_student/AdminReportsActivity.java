package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminReportsActivity extends AppCompatActivity {

    private static final String TAG = "AdminReports";
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy", Locale.US);

    private TextView dailyRevenueValue, weeklyRevenueValue, ordersDeliveredValue;
    private TextView totalDeliveriesValue, todayDeliveriesValue, mostActiveUserValue;
    private TextView newUsersValue, repeatCustomersValue, avgOrderValue, monthlyRevenueValue;

    private FirebaseFirestore db;
    private CollectionReference paymentsRef, ordersRef, studentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        initializeViews();
        initializeFirestore();
        setDefaultValues();
        loadAllData();
    }

    private void initializeViews() {
        dailyRevenueValue = findViewById(R.id.dailyRevenueValue);
        weeklyRevenueValue = findViewById(R.id.weeklyRevenueValue);
        ordersDeliveredValue = findViewById(R.id.ordersDeliveredValue);
        totalDeliveriesValue = findViewById(R.id.totalDeliveriesValue);
        todayDeliveriesValue = findViewById(R.id.todayDeliveriesValue);
        mostActiveUserValue = findViewById(R.id.mostActiveUserValue);
        newUsersValue = findViewById(R.id.newUsersValue);
        repeatCustomersValue = findViewById(R.id.repeatCustomersValue);
        avgOrderValue = findViewById(R.id.avgOrderValue);
        monthlyRevenueValue = findViewById(R.id.monthlyRevenueValue);
    }

    private void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
        paymentsRef = db.collection("payments");
        ordersRef = db.collection("orders");
        studentsRef = db.collection("Students");
    }

    private void setDefaultValues() {
        dailyRevenueValue.setText("₹0.00");
        weeklyRevenueValue.setText("₹0.00");
        ordersDeliveredValue.setText("0");
        totalDeliveriesValue.setText("0");
        todayDeliveriesValue.setText("Today: 0");
        mostActiveUserValue.setText("Loading...");
        newUsersValue.setText("0");
        repeatCustomersValue.setText("0");
        avgOrderValue.setText("₹0.00");
        monthlyRevenueValue.setText("₹0.00");
    }

    private void loadAllData() {
        Log.d(TAG, "Starting to load all data");
        loadDailyRevenue();
        loadWeeklyRevenue();
        loadOrdersDelivered();
        loadTotalDeliveriesThisMonth();
        loadTodayDeliveries();
        loadMostActiveUser();
        loadNewUsersThisMonth();
        loadRepeatCustomers();
        loadAverageOrderValue();
        loadMonthlyRevenue();
    }

    private void loadDailyRevenue() {
        String today = DISPLAY_DATE_FORMAT.format(new Date());
        Log.d(TAG, "Loading daily revenue for: " + today);

        paymentsRef.whereEqualTo("status", "success")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double total = 0;
                        int count = 0;
                        for (QueryDocumentSnapshot payment : task.getResult()) {
                            Timestamp timestamp = payment.getTimestamp("createdAt");
                            Double amount = payment.getDouble("amount");

                            if (timestamp != null && amount != null) {
                                Date paymentDate = timestamp.toDate();
                                String paymentDateStr = DISPLAY_DATE_FORMAT.format(paymentDate);
                                if (paymentDateStr.equals(today)) {
                                    total += amount;
                                    count++;
                                }
                            }
                        }
                        Log.d(TAG, "Found " + count + " daily payments totaling ₹" + total);
                        dailyRevenueValue.setText(String.format(Locale.getDefault(), "₹%.2f", total));
                    } else {
                        Log.e(TAG, "Daily revenue load failed: ", task.getException());
                        dailyRevenueValue.setText("₹0.00");
                    }
                });
    }

    private void loadWeeklyRevenue() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date startOfWeek = calendar.getTime();
        Date endOfWeek = new Date();
        Log.d(TAG, "Loading weekly revenue from " + startOfWeek + " to " + endOfWeek);

        paymentsRef.whereEqualTo("status", "success")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double total = 0;
                        int count = 0;
                        for (QueryDocumentSnapshot payment : task.getResult()) {
                            Timestamp timestamp = payment.getTimestamp("createdAt");
                            Double amount = payment.getDouble("amount");

                            if (timestamp != null && amount != null) {
                                Date paymentDate = timestamp.toDate();
                                if (!paymentDate.before(startOfWeek) && !paymentDate.after(endOfWeek)) {
                                    total += amount;
                                    count++;
                                }
                            }
                        }
                        Log.d(TAG, "Found " + count + " weekly payments totaling ₹" + total);
                        weeklyRevenueValue.setText(String.format(Locale.getDefault(), "₹%.2f", total));
                    } else {
                        Log.e(TAG, "Weekly revenue load failed: ", task.getException());
                        weeklyRevenueValue.setText("₹0.00");
                    }
                });
    }

    private void loadOrdersDelivered() {
        Log.d(TAG, "Loading delivered orders count");
        ordersRef.whereEqualTo("status", "Delivered")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        Log.d(TAG, "Found " + count + " delivered orders");
                        ordersDeliveredValue.setText(String.valueOf(count));
                    } else {
                        Log.e(TAG, "Delivered orders load failed: ", task.getException());
                        ordersDeliveredValue.setText("0");
                    }
                });
    }

    private void loadTotalDeliveriesThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();
        Date endOfMonth = new Date();
        Log.d(TAG, "Loading monthly deliveries from " + startOfMonth + " to " + endOfMonth);

        ordersRef.whereEqualTo("status", "Delivered")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot order : task.getResult()) {
                            Timestamp timestamp = order.getTimestamp("createdAt");
                            if (timestamp != null) {
                                Date orderDate = timestamp.toDate();
                                if (!orderDate.before(startOfMonth) && !orderDate.after(endOfMonth)) {
                                    count++;
                                }
                            }
                        }
                        Log.d(TAG, "Found " + count + " deliveries this month");
                        totalDeliveriesValue.setText(String.valueOf(count));
                    } else {
                        Log.e(TAG, "Monthly deliveries load failed: ", task.getException());
                        totalDeliveriesValue.setText("0");
                    }
                });
    }

    private void loadTodayDeliveries() {
        String today = DISPLAY_DATE_FORMAT.format(new Date());
        Log.d(TAG, "Loading today's deliveries for: " + today);

        ordersRef.whereEqualTo("status", "Delivered")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot order : task.getResult()) {
                            Timestamp timestamp = order.getTimestamp("createdAt");
                            if (timestamp != null) {
                                Date orderDate = timestamp.toDate();
                                String orderDateStr = DISPLAY_DATE_FORMAT.format(orderDate);
                                if (orderDateStr.equals(today)) {
                                    count++;
                                }
                            }
                        }
                        Log.d(TAG, "Found " + count + " deliveries today");
                        todayDeliveriesValue.setText("Today: " + count);
                    } else {
                        Log.e(TAG, "Today's deliveries load failed: ", task.getException());
                        todayDeliveriesValue.setText("Today: 0");
                    }
                });
    }

    private void loadMostActiveUser() {
        Log.d(TAG, "Loading most active user");
        ordersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> userOrders = new HashMap<>();
                        String mostActiveUserId = null;
                        int maxOrders = 0;

                        for (QueryDocumentSnapshot order : task.getResult()) {
                            String userId = order.getString("userId");
                            if (userId != null) {
                                int count = userOrders.containsKey(userId) ? userOrders.get(userId) + 1 : 1;
                                userOrders.put(userId, count);
                                if (count > maxOrders) {
                                    maxOrders = count;
                                    mostActiveUserId = userId;
                                }
                            }
                        }

                        if (mostActiveUserId != null) {
                            Log.d(TAG, "Most active user ID: " + mostActiveUserId + " with " + maxOrders + " orders");
                            fetchUserName(mostActiveUserId);
                        } else {
                            Log.d(TAG, "No active users found");
                            mostActiveUserValue.setText("No active users");
                        }
                    } else {
                        Log.e(TAG, "Most active user load failed: ", task.getException());
                        mostActiveUserValue.setText("Error");
                    }
                });
    }

    private void fetchUserName(String userId) {
        studentsRef.document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String name = task.getResult().getString("stuname");
                        if (name != null) {
                            Log.d(TAG, "Found user name: " + name);
                            mostActiveUserValue.setText(name);
                        } else {
                            Log.d(TAG, "User name not found for ID: " + userId);
                            mostActiveUserValue.setText("Unknown");
                        }
                    } else {
                        Log.e(TAG, "User name load failed: ", task.getException());
                        mostActiveUserValue.setText("Unknown");
                    }
                });
    }

    private void loadNewUsersThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();
        Date endOfMonth = new Date();
        Log.d(TAG, "Loading new users from " + startOfMonth + " to " + endOfMonth);

        studentsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot student : task.getResult()) {
                            Timestamp timestamp = student.getTimestamp("createdAt");
                            if (timestamp != null) {
                                Date joinDate = timestamp.toDate();
                                if (!joinDate.before(startOfMonth) && !joinDate.after(endOfMonth)) {
                                    count++;
                                }
                            }
                        }
                        Log.d(TAG, "Found " + count + " new users this month");
                        newUsersValue.setText(String.valueOf(count));
                    } else {
                        Log.e(TAG, "New users load failed: ", task.getException());
                        newUsersValue.setText("0");
                    }
                });
    }

    private void loadRepeatCustomers() {
        Log.d(TAG, "Loading repeat customers");
        ordersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> customerOrders = new HashMap<>();
                        int repeatCustomers = 0;

                        for (QueryDocumentSnapshot order : task.getResult()) {
                            String userId = order.getString("userId");
                            if (userId != null) {
                                int count = customerOrders.containsKey(userId) ? customerOrders.get(userId) + 1 : 1;
                                customerOrders.put(userId, count);
                                if (count == 2) {
                                    repeatCustomers++;
                                }
                            }
                        }
                        Log.d(TAG, "Found " + repeatCustomers + " repeat customers");
                        repeatCustomersValue.setText(String.valueOf(repeatCustomers));
                    } else {
                        Log.e(TAG, "Repeat customers load failed: ", task.getException());
                        repeatCustomersValue.setText("0");
                    }
                });
    }

    private void loadAverageOrderValue() {
        Log.d(TAG, "Loading average order value");

        ordersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalAmount = 0;
                        int orderCount = 0;

                        for (QueryDocumentSnapshot order : task.getResult()) {
                            Double amount = order.getDouble("totalAmount");
                            if (amount != null) {
                                totalAmount += amount;
                                orderCount++;
                            }
                        }

                        if (orderCount > 0) {
                            double average = totalAmount / orderCount;
                            Log.d(TAG, "Average order value: ₹" + average);
                            avgOrderValue.setText(String.format(Locale.getDefault(), "₹%.2f", average));
                        } else {
                            Log.d(TAG, "No orders found for average calculation");
                            avgOrderValue.setText("₹0.00");
                        }
                    } else {
                        Log.e(TAG, "Average order value load failed: ", task.getException());
                        avgOrderValue.setText("₹0.00");
                    }
                });
    }

    private void loadMonthlyRevenue() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();
        Date endOfMonth = new Date();
        Log.d(TAG, "Loading monthly revenue from " + startOfMonth + " to " + endOfMonth);

        paymentsRef.whereEqualTo("status", "success")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double total = 0;
                        int count = 0;
                        for (QueryDocumentSnapshot payment : task.getResult()) {
                            Timestamp timestamp = payment.getTimestamp("createdAt");
                            Double amount = payment.getDouble("amount");

                            if (timestamp != null && amount != null) {
                                Date paymentDate = timestamp.toDate();
                                if (!paymentDate.before(startOfMonth) && !paymentDate.after(endOfMonth)) {
                                    total += amount;
                                    count++;
                                }
                            }
                        }
                        Log.d(TAG, "Found " + count + " monthly payments totaling ₹" + total);
                        monthlyRevenueValue.setText(String.format(Locale.getDefault(), "₹%.2f", total));
                    } else {
                        Log.e(TAG, "Monthly revenue load failed: ", task.getException());
                        monthlyRevenueValue.setText("₹0.00");
                    }
                });
    }
}