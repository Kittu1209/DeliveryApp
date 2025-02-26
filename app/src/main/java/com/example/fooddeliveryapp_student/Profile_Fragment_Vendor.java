package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Profile_Fragment_Vendor extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, shopEditText;
    private Button changePasswordButton, editProfileButton, saveButton, saveAddressButton;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference userRef;
    private FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile___vendor, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            // Get reference to the vendor's document in Firestore
            userRef = firestore.collection("Vendors").document(user.getUid());
        }

        // Get UI elements
        nameEditText = view.findViewById(R.id.name_v);
        emailEditText = view.findViewById(R.id.email_v);
        shopEditText = view.findViewById(R.id.shopname_v);
        phoneEditText = view.findViewById(R.id.phone_v);
        changePasswordButton = view.findViewById(R.id.change_pass_btn_v);
        editProfileButton = view.findViewById(R.id.edit_profile_btn_v);
        saveButton = view.findViewById(R.id.save_btn_v);
        saveAddressButton = view.findViewById(R.id.save_address_vendor);

        // Disable editing initially
        disableEditing();

        // Fetch user data from Firestore
        fetchUserData();

        // Edit Profile button click
        editProfileButton.setOnClickListener(v -> enableEditing());

        // Save Profile button click
        saveButton.setOnClickListener(v -> saveUpdatedData());

        // Change Password button click
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivityStudent.class);
            startActivity(intent);
        });

        // Save Address button click
        saveAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Shops_Address.class);
            startActivity(intent);
        });

        return view;
    }

    // Fetch user data from Firestore
    private void fetchUserData() {
        if (userRef != null) {
            userRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                // Retrieve data from Firestore and set it in the EditTexts
                                nameEditText.setText(documentSnapshot.getString("vendorName"));
                                emailEditText.setText(documentSnapshot.getString("vendorEmail"));
                                phoneEditText.setText(documentSnapshot.getString("vendorPhone"));
                                shopEditText.setText(documentSnapshot.getString("shopName"));
                            }
                        } else {
                            Log.e("Firestore", "Error fetching document", task.getException());
                        }
                    });
        }
    }

    // Enable editing
    private void enableEditing() {
        nameEditText.setFocusableInTouchMode(true);
        phoneEditText.setFocusableInTouchMode(true);
        emailEditText.setFocusableInTouchMode(true);
        shopEditText.setFocusableInTouchMode(true);
        saveButton.setVisibility(View.VISIBLE);
    }

    // Save updated data to Firestore
    private void saveUpdatedData() {
        if (userRef != null) {
            // Get values from the EditTexts
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String shopName = shopEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || shopName.isEmpty()) {
                Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a map of the updated data
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("vendorName", name);
            updatedData.put("vendorEmail", email);
            updatedData.put("vendorPhone", phone);
            updatedData.put("shopName", shopName);

            // Update Firestore document
            userRef.update(updatedData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                            disableEditing();
                        } else {
                            Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Error updating document", task.getException());
                        }
                    });
        }
    }

    // Disable editing initially
    private void disableEditing() {
        nameEditText.setFocusableInTouchMode(false);
        emailEditText.setFocusableInTouchMode(false);
        phoneEditText.setFocusableInTouchMode(false);
        shopEditText.setFocusableInTouchMode(false);
        saveButton.setVisibility(View.GONE);
    }
}
