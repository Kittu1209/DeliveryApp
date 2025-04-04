package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentPage extends AppCompatActivity implements PaymentResultListener {

    private TextView tvTotalAmount;
    private Button btnPayNow;
    private int totalAmountInPaise;
    private FirebaseFirestore db;
    private double totalAmount = 0.0; // Holds total cart amount

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPayNow = findViewById(R.id.btnPayNow);
        db = FirebaseFirestore.getInstance();

        fetchTotalCartAmount(); // Fetch total amount from Firestore

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
                        double price = doc.getDouble("price");
                        long quantity = doc.getLong("quantity");
                        totalAmount += price * quantity;
                    }

                    totalAmountInPaise = (int) (totalAmount * 100); // Convert to paise
                    tvTotalAmount.setText("Total Amount: ₹" + totalAmount);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch cart total", Toast.LENGTH_SHORT).show());
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_9DWCyV2eFgw3N8"); // Replace with actual Razorpay key

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
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("carts").document(uid).collection("items")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {

                            List<Map<String, Object>> itemsList = new ArrayList<>();
                            final double[] finalAmount = {0};

                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Map<String, Object> item = new HashMap<>();
                                item.put("productId", doc.getString("productId"));
                                item.put("name", doc.getString("name"));
                                item.put("price", doc.getDouble("price"));
                                item.put("quantity", doc.getLong("quantity"));
                                item.put("shopId", doc.getString("shopId"));
                                item.put("imageUrl", doc.getString("imageUrl"));

                                finalAmount[0] += doc.getDouble("price") * doc.getLong("quantity");
                                itemsList.add(item);
                            }

                            Map<String, Object> orderData = new HashMap<>();
                            orderData.put("userId", uid);
                            orderData.put("orderId", razorpayPaymentID);
                            orderData.put("status", "pending");
                            orderData.put("createdAt", new Date());
                            orderData.put("totalAmount", finalAmount[0]);
                            orderData.put("items", itemsList);

                            db.collection("orders")
                                    .document(razorpayPaymentID)
                                    .set(orderData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(PaymentPage.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                        clearCart();

                                        Intent intent = new Intent(PaymentPage.this, ConfirmOrderActivity.class);
                                        intent.putExtra("PAYMENT_ID", razorpayPaymentID);
                                        intent.putExtra("TOTAL_AMOUNT", finalAmount[0]);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(PaymentPage.this, "Failed to save order", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(this, "Cart is empty. No items to order.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error fetching cart items", Toast.LENGTH_LONG).show());
        }
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
                            FirebaseFirestore.getInstance().collection("carts").document(user.getUid())
                                    .collection("items").document(document.getId()).delete();
                        }
                    });
        }
    }
}
