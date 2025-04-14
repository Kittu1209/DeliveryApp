package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.cardview.widget.CardView;

public class Setting_Fragement_Vendor extends Fragment {

    private CardView cardMyProfile, cardChangePassword, cardFAQ, cardLogout;

    public Setting_Fragement_Vendor() {
        // Required empty public constructor
    }

    public static Setting_Fragement_Vendor newInstance(String param1, String param2) {
        Setting_Fragement_Vendor fragment = new Setting_Fragement_Vendor();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting__fragement__vendor, container, false);

        // Initialize CardViews
        cardMyProfile = view.findViewById(R.id.cardmyprofile);
        cardChangePassword = view.findViewById(R.id.cardchnagepassword);
        cardFAQ = view.findViewById(R.id.cardfaq);
        cardLogout = view.findViewById(R.id.cardlogout);

        // My Profile → Open Profile_Fragment_Vendor
        cardMyProfile.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_vendor, new Profile_Fragment_Vendor());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Change Password → Start ChangePasswordActivityStudent
        cardChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivityStudent.class);
            startActivity(intent);
        });

        // FAQ → Open FAQs_Fragment_Vendor
        cardFAQ.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_vendor, new FAQs_Fragment_Vendor());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Logout
        cardLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Logout from Firebase

            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent going back
            startActivity(intent);
        });

        return view;
    }
}
