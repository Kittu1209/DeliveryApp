package com.example.fooddeliveryapp_student;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile_Fragment_Vendor extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, shopeEditText;
    private Button changePasswordButton, editProfileButton, saveButton;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile___vendor, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            userRef = database.getReference("Vendor_Registration").child(user.getUid());
        }

        // Get UI elements
        nameEditText = view.findViewById(R.id.name_v);
        emailEditText = view.findViewById(R.id.email_v);
        shopeEditText=view.findViewById(R.id.shopname_v);
        phoneEditText = view.findViewById(R.id.phone_v);
        changePasswordButton = view.findViewById(R.id.change_pass_btn_v);
        editProfileButton = view.findViewById(R.id.edit_profile_btn_v);
        saveButton = view.findViewById(R.id.save_btn_v);

        // Disable editing initially
        disableEditing();

        // Fetch user data in real-time
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

        return view;
    }

    // Fetch user data in real-time
    private void fetchUserData() {
        if (userRef != null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        nameEditText.setText(snapshot.child("vendorName").getValue(String.class));
                        emailEditText.setText(snapshot.child("vendorEmail").getValue(String.class));
                        phoneEditText.setText(snapshot.child("vendorPhone").getValue(String.class));
                        shopeEditText.setText(snapshot.child("shopName").getValue(String.class));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error fetching data", error.toException());
                }
            });
        }
    }

    // Enable editing
    private void enableEditing() {
        nameEditText.setFocusableInTouchMode(true);
        phoneEditText.setFocusableInTouchMode(true);
        emailEditText.setFocusableInTouchMode(true);
        shopeEditText.setFocusable(true);
        saveButton.setVisibility(View.VISIBLE);
    }

    // Save updated data to Firebase
    private void saveUpdatedData() {
        if (userRef != null) {
            userRef.child("vendorName").setValue(nameEditText.getText().toString());
            userRef.child("vendorEmail").setValue(emailEditText.getText().toString());
            userRef.child("shopName").setValue(shopeEditText.getText().toString());
            userRef.child("vendorPhone").setValue(phoneEditText.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                            disableEditing();
                        } else {
                            Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Disable editing initially
    private void disableEditing() {
        nameEditText.setFocusable(false);
        emailEditText.setFocusable(false);
        phoneEditText.setFocusable(false);
        shopeEditText.setFocusable(false);
        saveButton.setVisibility(View.GONE);
    }
}
