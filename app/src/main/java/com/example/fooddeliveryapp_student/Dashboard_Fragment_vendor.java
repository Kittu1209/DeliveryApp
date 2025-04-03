package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Dashboard_Fragment_vendor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard_Fragment_vendor extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button shop_add,categoryBtn;

    public Dashboard_Fragment_vendor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboard_Fragment_vendor.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard_Fragment_vendor newInstance(String param1, String param2) {
        Dashboard_Fragment_vendor fragment = new Dashboard_Fragment_vendor();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard__vendor, container, false);
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

        return view;

       // return inflater.inflate(R.layout.fragment_dashboard__vendor, container, false);
    }
}