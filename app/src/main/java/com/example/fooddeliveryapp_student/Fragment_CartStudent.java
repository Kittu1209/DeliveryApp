package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fragment_CartStudent extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private TextView totalPriceText;
    private Button placeOrderButton;
    private double totalAmount = 0.0; // ✅ Variable to store total amount

    public Fragment_CartStudent() {
        // Required empty constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__cart_student, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_cart);
        totalPriceText = view.findViewById(R.id.textTotalPrice);
        placeOrderButton = view.findViewById(R.id.order_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartItemList, db, totalPriceText);
        recyclerView.setAdapter(cartAdapter);

        loadCartItems();

        placeOrderButton.setOnClickListener(v -> openPaymentPage()); // ✅ Open Payment Page

        return view;
    }

    private void loadCartItems() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        CollectionReference cartRef = db.collection("carts").document(userId).collection("items");

        cartRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            cartItemList.clear();
            totalAmount = 0.0;

            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                CartItem item = doc.toObject(CartItem.class);
                if (doc.getId() != null) {
                    item.setDocumentId(doc.getId());
                } else {
                    Log.e("Fragment_CartStudent", "Document ID is null for item");
                    continue;
                }

                cartItemList.add(item);
                totalAmount += item.getPrice() * item.getQuantity();
            }

            totalPriceText.setText("Total: ₹" + totalAmount);
            cartAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show()
        );
    }

    private void openPaymentPage() {
        Intent intent = new Intent(getActivity(), PaymentPage.class);
        intent.putExtra("totalAmount", totalAmount);
        startActivity(intent);
    }
}
