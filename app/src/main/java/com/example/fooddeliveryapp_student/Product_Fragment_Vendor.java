package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Product_Fragment_Vendor extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button btnAddProduct, shop_add, categoryBtn, editProd, editCat;
    private String mParam1;
    private String mParam2;

    public Product_Fragment_Vendor() {
        // Required empty public constructor
    }

    public static Product_Fragment_Vendor newInstance(String param1, String param2) {
        Product_Fragment_Vendor fragment = new Product_Fragment_Vendor();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout only once
        View view = inflater.inflate(R.layout.fragment_product___vendor, container, false);

        // Initialize buttons
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        shop_add = view.findViewById(R.id.shop_address_btn_);
        categoryBtn = view.findViewById(R.id.category_btn);
        editProd = view.findViewById(R.id.btneditProduct);
        editCat = view.findViewById(R.id.btnAddCategory);

        // Set click listeners
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        shop_add.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Shops_Address.class);
            startActivity(intent);
        });

        categoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryPage.class);
            startActivity(intent);
        });

        editProd.setOnClickListener(v -> {
            // Changed this to go to EditProductActivity if that's what you intended
            Intent intent = new Intent(getActivity(), VendorProductsActivity.class);
            startActivity(intent);
        });

        editCat.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
            startActivity(intent);
        });

        // Return the view we set up with all the click listeners
        return view;
    }
}