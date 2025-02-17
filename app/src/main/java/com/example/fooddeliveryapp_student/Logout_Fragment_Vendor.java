package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Logout_Fragment_Vendor extends Fragment {

    private FirebaseAuth mAuth;

    public Logout_Fragment_Vendor() {
        // Required empty public constructor
    }

    // Factory method to create an instance of the fragment
    public static Logout_Fragment_Vendor newInstance(String param1, String param2) {
        Logout_Fragment_Vendor fragment = new Logout_Fragment_Vendor();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Auth
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout___vendor, container, false);

        // Find the logout button and set the click listener
        Button logoutButton = view.findViewById(R.id.logout_button); // Make sure to add this button in your XML

        logoutButton.setOnClickListener(v -> {
            // Perform the logout action
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
            }
        });

        return view;
    }
}
