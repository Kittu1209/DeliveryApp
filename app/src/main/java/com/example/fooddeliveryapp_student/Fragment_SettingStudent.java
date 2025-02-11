package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_SettingStudent extends Fragment {

    private CardView cardAccountSettings, cardNotifications, cardPrivacy, cardHelp, cardLogout;

    public Fragment_SettingStudent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__setting_student, container, false);

        // Initialize UI components
        cardAccountSettings = view.findViewById(R.id.cardaccountsetting);
        cardNotifications = view.findViewById(R.id.cardnotification);
        cardPrivacy = view.findViewById(R.id.cardprivacy);
        cardHelp = view.findViewById(R.id.cardhelp);
        cardLogout = view.findViewById(R.id.cardlogout);

        // Click listeners for each setting option
        cardAccountSettings.setOnClickListener(v -> {
            // Navigate to Account Settings
            Intent intent = new Intent(getActivity(), Account_Setting.class);
            startActivity(intent);
        });

        cardNotifications.setOnClickListener(v -> {
            // Navigate to Notifications settings
            Intent intent = new Intent(getActivity(), Notification_Setting.class);
            startActivity(intent);
        });

        cardPrivacy.setOnClickListener(v -> {
            // Navigate to Privacy & Security settings
            Intent intent = new Intent(getActivity(), Privacy_Setting.class);
            startActivity(intent);
        });

        cardHelp.setOnClickListener(v -> {
            // Navigate to Help & Support page
            Intent intent = new Intent(getActivity(), Help_Support_Setting.class);
            startActivity(intent);
        });

        cardLogout.setOnClickListener(v -> {
            // Logout from Firebase Authentication
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            // Redirect to Login Activity
            Intent intent = new Intent(getActivity(), LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
