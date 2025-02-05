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

public class Fragment_ProfileStudent extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, idEditText;
    private Button changePasswordButton, editProfileButton, saveButton;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__profile_student, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            userRef = database.getReference("Student_Registration").child(user.getUid());
        }

        // Get UI elements
        nameEditText = view.findViewById(R.id.profile_name);
        emailEditText = view.findViewById(R.id.profile_email);
        phoneEditText = view.findViewById(R.id.profile_phone);
        idEditText = view.findViewById(R.id.profile_id);
        changePasswordButton = view.findViewById(R.id.change_password_button);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        saveButton = view.findViewById(R.id.save_button);

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
                        nameEditText.setText(snapshot.child("stuname").getValue(String.class));
                        emailEditText.setText(snapshot.child("stuemail").getValue(String.class));
                        phoneEditText.setText(snapshot.child("stuphno").getValue(String.class));
                        idEditText.setText(snapshot.child("stuid").getValue(String.class));
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
        idEditText.setFocusableInTouchMode(true);
        phoneEditText.setFocusableInTouchMode(true);
        emailEditText.setFocusableInTouchMode(true);
        saveButton.setVisibility(View.VISIBLE);
    }

    // Save updated data to Firebase
    private void saveUpdatedData() {
        if (userRef != null) {
            userRef.child("stuname").setValue(nameEditText.getText().toString());
            userRef.child("stuid").setValue(idEditText.getText().toString());
            userRef.child("stuemail").setValue(emailEditText.getText().toString());
            userRef.child("stuphno").setValue(phoneEditText.getText().toString())
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
        idEditText.setFocusable(false);
        saveButton.setVisibility(View.GONE);
    }
}
