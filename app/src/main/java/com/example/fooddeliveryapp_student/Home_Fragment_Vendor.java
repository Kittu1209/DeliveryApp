package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home_Fragment_Vendor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home_Fragment_Vendor extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
   // private Button cat_btn;

    public Home_Fragment_Vendor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_Fragment_Vendor.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_Fragment_Vendor newInstance(String param1, String param2) {
        Home_Fragment_Vendor fragment = new Home_Fragment_Vendor();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home___vendor, container, false);
//        cat_btn=view.findViewById(R.id.category_btn);
//        cat_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  Fragment categoryFragment=new CategoryFragment();
//             //   getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,categoryFragment).addToBackStack(null).commit();
//                Intent intent = new Intent(getActivity(), CategoryPage.class);
//                startActivity(intent);
//            }
//        });
     //   return inflater.inflate(R.layout.fragment_home___vendor, container, false);
    return view;
    }
}