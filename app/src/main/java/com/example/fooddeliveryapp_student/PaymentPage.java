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

public class PaymentPage extends AppCompatActivity implements PaymentResultListener {

    private TextView tvTotalAmount;
    private Button btnPayNow;
    private int totalAmountInPaise;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPayNow = findViewById(R.id.btnPayNow);

        // Get total amount from intent
        double totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
        totalAmountInPaise = (int) (totalAmount * 100); // Convert to paise

        tvTotalAmount.setText("Total Amount: ₹" + totalAmount);

        btnPayNow.setOnClickListener(v -> startPayment());
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

        // Set total amount to 0 in the UI
        tvTotalAmount.setText("Total Amount: ₹0");

        // Clear the cart
        clearCart();
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
                        // After clearing the cart, go back to HomeFragment/HomeActivity
                        goToHome();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to clear cart", Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomePage_Student.class); // Replace with your main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close payment activity
    }
}
