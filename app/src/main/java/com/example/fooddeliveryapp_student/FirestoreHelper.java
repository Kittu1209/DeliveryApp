package com.example.fooddeliveryapp_student;

import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private final FirebaseFirestore firestore;
    private final CollectionReference addressRef;

    public FirestoreHelper() {
        firestore = FirebaseFirestore.getInstance();
        addressRef = firestore.collection("Delivery_address");
    }

    public void addAddress(AddressModel address, FirestoreCallback callback) {
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("studentName", address.getStudentName());
        addressData.put("phoneNumber", address.getPhoneNumber());
        addressData.put("hostel", address.getHostel());
        addressData.put("room", address.getRoom());
        addressData.put("userId", address.getUserId());

        addressRef.add(addressData)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding address", e));
    }

    public interface FirestoreCallback {
        void onSuccess();
    }
}
