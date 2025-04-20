package com.example.fooddeliveryapp_student;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ShopManager {
    private static final String VENDORS_COLLECTION = "Vendors";
    private static final String SHOPS_COLLECTION = "shops";

    public interface ShopCheckCallback {
        void onShopExists(String shopId);
        void onShopDoesNotExist();
        void onError(String message);
    }

    public static void checkAndCreateShop(Context context, ShopCheckCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError("User not authenticated");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        // Check if shop document exists directly using the userId as document ID
        db.collection(SHOPS_COLLECTION)
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot shopDoc = task.getResult();
                        if (shopDoc.exists()) {
                            callback.onShopExists(userId);
                        } else {
                            // No shop exists - check vendor details and create
                            fetchVendorAndCreateShop(db, userId, context, callback);
                        }
                    } else {
                        callback.onError("Failed to check shop existence: " +
                                task.getException().getMessage());
                    }
                });
    }

    private static void fetchVendorAndCreateShop(FirebaseFirestore db, String userId,
                                                 Context context, ShopCheckCallback callback) {
        db.collection(VENDORS_COLLECTION)
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DocumentSnapshot vendorDoc = task.getResult();
                            createNewShop(db, userId, vendorDoc, context, callback);
                        } else {
                            callback.onError("Vendor details not found");
                            Toast.makeText(context,
                                    "Please complete vendor registration first",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        callback.onError("Failed to fetch vendor details: " +
                                task.getException().getMessage());
                    }
                });
    }

    private static void createNewShop(FirebaseFirestore db, String userId,
                                      DocumentSnapshot vendorDoc, Context context,
                                      ShopCheckCallback callback) {
        Map<String, Object> shopData = new HashMap<>();

        // Required fields from Vendor collection
        shopData.put("ownerId", userId);
        shopData.put("name", vendorDoc.getString("shopName"));
        shopData.put("vendorName", vendorDoc.getString("vendorName"));
        shopData.put("email", vendorDoc.getString("vendorEmail"));
        shopData.put("phone", vendorDoc.getString("vendorPhone"));

        // Default shop fields
        shopData.put("isActive", true);
        shopData.put("promoted", false);
        shopData.put("rating", 0.0);
        shopData.put("cuisine", "General");
        shopData.put("deliveryTime", "30-40 min");

        // Timestamps
        shopData.put("createdAt", FieldValue.serverTimestamp());
        shopData.put("updatedAt", FieldValue.serverTimestamp());

        db.collection(SHOPS_COLLECTION)
                .document(userId)
                .set(shopData)
                .addOnSuccessListener(aVoid -> {
                    callback.onShopExists(userId);
                    Toast.makeText(context,
                            "New shop profile created. Please complete your shop details.",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to create shop: " + e.getMessage());
                    Toast.makeText(context,
                            "Failed to create shop: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // Additional helper method to update shop details
    public static void updateShopDetails(String shopId, Map<String, Object> updates,
                                         Context context, Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add update timestamp
        updates.put("updatedAt", FieldValue.serverTimestamp());

        db.collection(SHOPS_COLLECTION)
                .document(shopId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context,
                            "Shop details updated successfully",
                            Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context,
                                "Failed to update shop: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}