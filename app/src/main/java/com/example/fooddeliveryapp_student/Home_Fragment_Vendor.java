package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Home_Fragment_Vendor extends Fragment {

    private Button btnAddProduct; // Declare button

    public Home_Fragment_Vendor() {
        // Required empty public constructor
    }

    public static Home_Fragment_Vendor newInstance(String param1, String param2) {
        Home_Fragment_Vendor fragment = new Home_Fragment_Vendor();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home___vendor, container, false);

        // Initialize button
        btnAddProduct = view.findViewById(R.id.btnAddProduct);

        // Handle button click to open AddProductActivity
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
