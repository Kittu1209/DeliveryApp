package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class Home_Fragment_Vendor extends Fragment {

    private static final String TAG = "HomeFragmentVendor";
    private TextView textMenuItemsCount, textEarningsAmount;
    private ToggleButton toggleMute;
    private boolean isMuted = false;

    private FirebaseFirestore db;
    private CollectionReference productsRef, paymentsRef, ordersRef;
    private String currentShopId;
    private ListenerRegistration orderListener;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home___vendor, container, false);

        textMenuItemsCount = view.findViewById(R.id.textMenuItemsCount);
        textEarningsAmount = view.findViewById(R.id.textEarningsAmount);
        toggleMute = view.findViewById(R.id.toggleMute);

        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.cha_ching);

        toggleMute.setOnCheckedChangeListener((buttonView, isChecked) -> isMuted = !isChecked);

        view.postDelayed(() -> {
            fetchMenuItemsCount();
            fetchEarningsAmount();
            setupRealTimeOrderListener();
        }, 500);

        return view;
    }

    private void fetchMenuItemsCount() {
        if (productsRef == null || currentShopId == null) return;

        productsRef.whereEqualTo("shopId", currentShopId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    textMenuItemsCount.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching menu items", e));
    }

    private void fetchEarningsAmount() {
        if (paymentsRef == null || currentShopId == null) return;

        paymentsRef.whereEqualTo("shopId", currentShopId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    double total = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Double amount = doc.getDouble("amount");
                        if (amount != null) total += amount;
                    }

                    String formattedAmount = NumberFormat.getCurrencyInstance(new Locale("en", "IN"))
                            .format(total);
                    textEarningsAmount.setText(formattedAmount);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching earnings", e));
    }

    private void setupRealTimeOrderListener() {
        if (ordersRef == null || currentShopId == null) return;

        orderListener = ordersRef
                .whereEqualTo("status", "pending")
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
                                // Check if the order contains items from current vendor
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
}
