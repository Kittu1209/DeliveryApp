package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Home_Fragment_Vendor extends Fragment {

    private Button btnAddProduct,shop_add,categoryBtn,editProd; // Declare button

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

    @SuppressLint("MissingInflatedId")
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
        shop_add=view.findViewById(R.id.shop_address_btn_);
        shop_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Shops_Address.class);
                startActivity(intent);
            }
        });
        categoryBtn=view.findViewById(R.id.category_btn);
        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Category Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), CategoryPage.class);
                startActivity(intent);
            }
        });
        editProd=view.findViewById(R.id.btneditProduct);
        editProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Category Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), CategoryPage.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
