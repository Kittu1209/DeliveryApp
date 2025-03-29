package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
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

public class PaymentPage extends AppCompatActivity implements PaymentResultListener {

    private TextView tvTotalAmount;
    private Button btnPayNow;
    private int totalAmount = 0; // Total amount in paise (₹1 = 100 paise)

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPayNow = findViewById(R.id.btnPayNow);

        fetchTotalAmount(); // Fetch total price from Firestore

        btnPayNow.setOnClickListener(v -> startPayment());
    }

    private void fetchTotalAmount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("carts")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int sum = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) { // Fix here
                        Long price = document.getLong("price"); // Fix here
                        Long quantity = document.getLong("quantity"); // Fix here
                        if (price != null && quantity != null) {
                            sum += price * quantity;
                        }
                    }
                    totalAmount = sum * 100; // Convert ₹ to paise
                    tvTotalAmount.setText("Total Amount: ₹" + sum);
                });
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_9DWCyV2eFgw3N8"); // Razorpay test key

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Food Delivery App");
            options.put("description", "Payment for Order");
            options.put("currency", "INR");
            options.put("amount", totalAmount); // Amount in paise
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
        clearCart(); // Clear cart after successful payment
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_LONG).show();
    }

    private void clearCart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("carts")
                    .whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) { // Fix here
                            FirebaseFirestore.getInstance().collection("carts").document(document.getId()).delete();
                        }
                    });
        }
    }
}
