package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

public class Fragment_SettingStudent extends Fragment {

    private CardView cardMyProfile, cardChangePassword, cardFAQ, cardlogout;

    public Fragment_SettingStudent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__setting_student, container, false);

        cardMyProfile = view.findViewById(R.id.cardmyprofile);
        cardChangePassword = view.findViewById(R.id.cardchnagepassword);
        cardFAQ = view.findViewById(R.id.cardfaq);
        cardlogout=view.findViewById(R.id.cardlogout);

        cardMyProfile.setOnClickListener(v -> {
            Fragment fragment = new Fragment_ProfileStudent();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        cardChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivityStudent.class);
            startActivity(intent);
        });

        cardFAQ.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FAQActivity.class);
            startActivity(intent);
        });

        cardlogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
            Intent intent = new Intent(getActivity(), LoginPage.class); // Replace with your actual LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
            startActivity(intent);
            requireActivity().finish(); // Finish current activity
        });



        return view;
    }
}
