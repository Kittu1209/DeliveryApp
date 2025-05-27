package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaymentPage extends AppCompatActivity implements PaymentResultListener {

    private TextView tvTotalAmount, tvoderamount;
    private Button btnPayNow;
    private FirebaseFirestore db;
    private double totalAmount = 0.0, orderamount=0.0;
    private int totalAmountInPaise;
    private AddressModel selectedAddress;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvoderamount=findViewById(R.id.tvOrderPrice);
        btnPayNow = findViewById(R.id.btnPayNow);
        db = FirebaseFirestore.getInstance();

        selectedAddress = (AddressModel) getIntent().getSerializableExtra("selectedAddress");

        fetchTotalCartAmount();

        btnPayNow.setOnClickListener(v -> startPayment());
    }

    private void fetchTotalCartAmount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        db.collection("carts").document(userId).collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    totalAmount = 0.0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Double price = doc.getDouble("price");
                        Long quantity = doc.getLong("quantity");
                        if (price != null && quantity != null) {
                            orderamount += price * quantity;
                        }
                        if (price != null && quantity != null) {
                            totalAmount += (price * quantity);
                        }
                    }
                    totalAmount += 20.0;
                    totalAmountInPaise = (int) (totalAmount * 100);
                    tvTotalAmount.setText("Total Amount: ₹" + totalAmount);
                    tvoderamount.setText("Order Amount: ₹"+ orderamount);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch cart total", Toast.LENGTH_SHORT).show());
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_9DWCyV2eFgw3N8"); // Replace with your Razorpay key

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Food Delivery App");
            options.put("description", "Payment for Order");
            options.put("currency", "INR");
            options.put("amount", totalAmountInPaise);
            options.put("prefill.email", "user@example.com");
            options.put("prefill.contact", "9876543210");

            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment Successful! ID: " + razorpayPaymentID, Toast.LENGTH_LONG).show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();

            db.collection("carts").document(uid).collection("items")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            List<Map<String, Object>> itemsList = new ArrayList<>();
                            final double[] finalAmount = {0.0};

                            for (QueryDocumentSnapshot doc : snapshot) {
                                Map<String, Object> item = new HashMap<>();
                                Double price = doc.getDouble("price");
                                Long quantity = doc.getLong("quantity");

                                item.put("productId", doc.getString("productId"));
                                item.put("name", doc.getString("name"));
                                item.put("price", price);
                                item.put("quantity", quantity);
                                item.put("shopId", doc.getString("shopId"));
                                item.put("imageUrl", doc.getString("imageUrl"));

                                if (price != null && quantity != null) {
                                    finalAmount[0] += price * quantity;
                                }

                                itemsList.add(item);
                            }

                            String orderId = "order_" + UUID.randomUUID().toString().substring(0, 8);

                            Map<String, Object> orderData = new HashMap<>();
                            orderData.put("userId", uid);
                            orderData.put("orderId", orderId);
                            orderData.put("status", "pending");
                            orderData.put("createdAt", new Date());
                            orderData.put("totalAmount", finalAmount[0]);
                            orderData.put("items", itemsList);
                            orderData.put("assignedDeliveryManId", null); // Assigned later

                            if (selectedAddress != null) {
                                Map<String, Object> addressData = new HashMap<>();
                                addressData.put("name", selectedAddress.getStudentName());
                                addressData.put("phone", selectedAddress.getPhoneNumber());
                                addressData.put("hostel", selectedAddress.getHostel());
                                addressData.put("room", selectedAddress.getRoom());
                                orderData.put("deliveryAddress", addressData);
                            }

                            db.collection("orders").document(orderId)
                                    .set(orderData)
                                    .addOnSuccessListener(aVoid -> {
                                        storePaymentDetails(uid, razorpayPaymentID, finalAmount[0], orderId);
                                        assignOrderToDeliveryMan(orderId);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(PaymentPage.this, "Failed to save order", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(this, "Cart is empty. No items to order.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error fetching cart items", Toast.LENGTH_LONG).show());
        }
    }

    private void assignOrderToDeliveryMan(String orderId) {
        db.collection("delivery_man").whereEqualTo("admin_control", "active").whereIn("current_duty", Arrays.asList("Available", "Busy")).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> deliveryMen = queryDocumentSnapshots.getDocuments();
                    if (deliveryMen.isEmpty()) {
                        Log.d("DeliveryAssign", "No delivery men available");
                        Toast.makeText(this, "No available delivery men. Order cannot be placed.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Round Robin selection using SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("RoundRobinPrefs", MODE_PRIVATE);
                    int lastIndex = prefs.getInt("lastIndex", -1);
                    int nextIndex = (lastIndex + 1) % deliveryMen.size();

                    DocumentSnapshot selectedDeliveryMan = deliveryMen.get(nextIndex);
                    String delManId = selectedDeliveryMan.getString("del_man_id");

                    if (delManId == null || delManId.trim().isEmpty()) {
                        Log.e("DeliveryAssign", "Selected delivery man has no 'del_man_id' field");
                        Toast.makeText(this, "Invalid delivery man data. Try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Prepare updates for the order
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("assignedDeliveryManId", delManId);
                    updates.put("status", "Delivery Man Assigned");

                    db.collection("orders").document(orderId)
                            .update(updates)
                            .addOnSuccessListener(unused -> {
                                Log.d("DeliveryAssign", "Order assigned to delivery man: " + delManId);
                                Toast.makeText(this, "Order assigned to: " + delManId, Toast.LENGTH_SHORT).show();

                                // Save updated round robin index
                                prefs.edit().putInt("lastIndex", nextIndex).apply();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DeliveryAssign", "Failed to assign order", e);
                                Toast.makeText(this, "Failed to assign delivery man. Try again.", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e("DeliveryAssign", "Error fetching delivery men", e);
                    Toast.makeText(this, "Error fetching delivery men. Please check your connection.", Toast.LENGTH_SHORT).show();
                });
    }

        private void storePaymentDetails(String userId, String paymentId, double amount, String orderId) {
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", amount);
        paymentData.put("createdAt", new Date());
        paymentData.put("orderId", orderId);
        paymentData.put("paymentId", paymentId);
        paymentData.put("status", "success");
        paymentData.put("userId", userId);

        db.collection("payments")
                .document(paymentId)
                .set(paymentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PaymentPage.this, "Payment details saved!", Toast.LENGTH_SHORT).show();
                    clearCart();

                    Intent intent = new Intent(PaymentPage.this, ConfirmOrderActivity.class);
                    intent.putExtra("ORDER_ID", orderId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(PaymentPage.this, "Failed to save payment details", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_LONG).show();
    }

    private void clearCart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("carts").document(user.getUid()).collection("items")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                    });
        }
    }
}
