package com.example.fooddeliveryapp_student;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentDistributor {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void distributePayments(String orderId) {
        db.collection("orders").document(orderId).get().addOnSuccessListener(orderSnapshot -> {
            if (!orderSnapshot.exists()) return;

            double totalAmount = roundToTwoDecimals(orderSnapshot.getDouble("totalAmount"));
            String deliveryManId = orderSnapshot.getString("assignedDeliveryManId");
            String userId = orderSnapshot.getString("userId");
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderSnapshot.get("items");

            db.collection("payments")
                    .whereEqualTo("orderId", orderId)
                    .limit(1)
                    .get().addOnSuccessListener(paymentSnapshots -> {
                        if (paymentSnapshots.isEmpty()) return;
                        DocumentSnapshot paymentDoc = paymentSnapshots.getDocuments().get(0);
                        String paymentId = paymentDoc.getString("paymentId");

                        // Calculate shares with rounding
                        double deliveryFee = roundToTwoDecimals(15.0);
                        double adminFee = roundToTwoDecimals(5.0 + (0.045 * totalAmount));
                        double gatewayFee = roundToTwoDecimals(0.01 * totalAmount);
                        double vendorAmount = roundToTwoDecimals(totalAmount - (deliveryFee + adminFee + gatewayFee));
                        double netPlatformEarning = roundToTwoDecimals(adminFee - deliveryFee);

                        // Admin payments
                        Map<String, Object> adminMap = new HashMap<>();
                        adminMap.put("orderId", orderId);
                        adminMap.put("totalAmount", totalAmount);
                        adminMap.put("adminShare", adminFee);
                        adminMap.put("deliveryManEarning", deliveryFee);
                        adminMap.put("paymentGatewayFee", gatewayFee);
                        adminMap.put("netPlatformEarning", netPlatformEarning);
                        adminMap.put("timestamp", FieldValue.serverTimestamp());
                        db.collection("admin_payments").document(orderId).set(adminMap);

                        // Delivery man payments
                        Map<String, Object> delMap = new HashMap<>();
                        delMap.put("paymentId", paymentId);
                        delMap.put("orderId", orderId);
                        delMap.put("deliveryManId", deliveryManId);
                        delMap.put("amount", deliveryFee);
                        delMap.put("createdAt", FieldValue.serverTimestamp());
                        db.collection("delivery_man_payments").document(orderId).set(delMap);

                        // Gateway fee
                        Map<String, Object> gatewayMap = new HashMap<>();
                        gatewayMap.put("paymentId", paymentId);
                        gatewayMap.put("orderId", orderId);
                        gatewayMap.put("amount", gatewayFee);
                        gatewayMap.put("createdAt", FieldValue.serverTimestamp());
                        db.collection("payment_gateway_payments").document(orderId).set(gatewayMap);

                        // Vendor payments
                        Map<String, Double> shopTotals = new HashMap<>();
                        for (Map<String, Object> item : items) {
                            String shopId = (String) item.get("shopId");
                            double price = ((Number) item.get("price")).doubleValue();
                            int quantity = ((Number) item.get("quantity")).intValue();
                            double itemTotal = roundToTwoDecimals(price * quantity);
                           // shopTotals.put(shopId, shopTotals.getOrDefault(shopId, 0.0) + itemTotal);
                            Double currentTotal = shopTotals.get(shopId);
                            if (currentTotal == null) currentTotal = 0.0;
                            shopTotals.put(shopId, currentTotal + itemTotal);

                        }

                        for (String shopId : shopTotals.keySet()) {
                            double share = roundToTwoDecimals((shopTotals.get(shopId) / totalAmount) * vendorAmount);
                            Map<String, Object> vendorMap = new HashMap<>();
                            vendorMap.put("paymentId", paymentId);
                            vendorMap.put("orderId", orderId);
                            vendorMap.put("shopId", shopId);
                            vendorMap.put("amount", share);
                            vendorMap.put("createdAt", FieldValue.serverTimestamp());
                            db.collection("vendor_payments").add(vendorMap);
                        }

                    });
        });
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
