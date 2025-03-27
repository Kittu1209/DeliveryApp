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
    private Button changePasswordButton, editProfileButton, saveButton, addAddressButton,logout;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference userRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile___vendor, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
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
        addAddressButton = view.findViewById(R.id.add_address); // Corrected ID
        logout=view.findViewById(R.id.log_out__vendor_btn);

        // Disable editing initially
        disableEditing();

        // Fetch user data from Firestore
        fetchUserData();
        mAuth = FirebaseAuth.getInstance();

        // Edit Profile button click
        editProfileButton.setOnClickListener(v -> enableEditing());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    mAuth.signOut();

                    // Check if the user is logged out successfully
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) {
                        // If the user is logged out, redirect to login activity
                        Intent intent = new Intent(getActivity(), LoginPage.class); // Replace with your LoginActivity
                        startActivity(intent);
                        getActivity().finish(); // Finish current activity to prevent going back
                        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // If there's an error in logout
                        Toast.makeText(getActivity(), "Logout failed", Toast.LENGTH_SHORT).show();
                    }}


        });
        // Save Profile button click
        saveButton.setOnClickListener(v -> saveUpdatedData());

        // Change Password button click
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivityStudent.class);
            startActivity(intent);
        });

        // Add Address button click
        addAddressButton.setOnClickListener(v -> {
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
        nameEditText.setFocusable(true);
        nameEditText.setFocusableInTouchMode(true);

        phoneEditText.setFocusable(true);
        phoneEditText.setFocusableInTouchMode(true);

        emailEditText.setFocusable(true);
        emailEditText.setFocusableInTouchMode(true);

        shopEditText.setFocusable(true);
        shopEditText.setFocusableInTouchMode(true);

        saveButton.setVisibility(View.VISIBLE);
    }

    // Save updated data to Firestore
    private void saveUpdatedData() {
        if (userRef != null) {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String shopName = shopEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || shopName.isEmpty()) {
                Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("vendorName", name);
            updatedData.put("vendorEmail", email);
            updatedData.put("vendorPhone", phone);
            updatedData.put("shopName", shopName);

            userRef.set(updatedData, SetOptions.merge())  // Merge updates
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
        nameEditText.setFocusable(false);
        nameEditText.setFocusableInTouchMode(false);

        emailEditText.setFocusable(false);
        emailEditText.setFocusableInTouchMode(false);

        phoneEditText.setFocusable(false);
        phoneEditText.setFocusableInTouchMode(false);

        shopEditText.setFocusable(false);
        shopEditText.setFocusableInTouchMode(false);

        saveButton.setVisibility(View.GONE);
    }
}
