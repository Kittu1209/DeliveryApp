package com.example.fooddeliveryapp_student;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class CartHelper {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addToCart(String productId, String name, double price, String imageUrl, String shopId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference cartRef = db.collection("carts").document(userId);

        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            List<Map<String, Object>> cartItems = new ArrayList<>();

            if (documentSnapshot.exists()) {
                cartItems = (List<Map<String, Object>>) documentSnapshot.get("items");
                if (cartItems == null) cartItems = new ArrayList<>();
            }

            boolean itemExists = false;
            for (Map<String, Object> item : cartItems) {
                if (item.get("productId").equals(productId)) {
                    int newQuantity = ((Long) item.get("quantity")).intValue() + 1;
                    item.put("quantity", newQuantity);
                    item.put("total", newQuantity * price);
                    item.put("updatedAt", new Date());
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                Map<String, Object> newItem = new HashMap<>();
                newItem.put("productId", productId);
                newItem.put("name", name);
                newItem.put("price", price);
                newItem.put("quantity", 1);
                newItem.put("imageUrl", imageUrl);
                newItem.put("shopId", shopId);
                newItem.put("total", price);
                newItem.put("updatedAt", new Date());
                cartItems.add(newItem);
            }

            cartRef.set(Collections.singletonMap("items", cartItems))
                    .addOnSuccessListener(aVoid -> System.out.println("Added to cart"))
                    .addOnFailureListener(e -> System.err.println("Error adding to cart"));
        });
    }
}
