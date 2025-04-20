package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Fragment_faqStudent extends Fragment {

    private RecyclerView recyclerView;
    private FAQAdapter faqAdapter;
    private List<FAQ> faqList;

    public Fragment_faqStudent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq_student, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewFAQ);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add divider between items
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // Prepare FAQ data
        faqList = new ArrayList<>();
        faqList.add(new FAQ("How to register?", "You can register using your email and password."));
        faqList.add(new FAQ("How to reset password?", "Go to login page and click on 'Forgot Password'."));
        faqList.add(new FAQ("How to contact support?", "You can email us at support@example.com."));
        faqList.add(new FAQ("How to add items to cart?", "Browse products, open details, and tap 'Add to Cart'."));
        faqList.add(new FAQ("How to change delivery address?", "Go to cart and tap 'Pay now' then you will get page to Add/Edit delivery option."));
        faqList.add(new FAQ("Can I cancel my order?", "Once placed, orders cannot be canceled."));
        faqList.add(new FAQ("How do I know if my order is delivered?", "You'll get updated once delivered. Go check My Orders."));
        faqList.add(new FAQ("How are payments handled?", "We use Razorpay for secure online payments."));
        faqList.add(new FAQ("How to edit my profile?", "Go to Settings > My Profile and tap edit icon."));
        faqList.add(new FAQ("What if I entered wrong information?", "You can update it anytime from your profile."));

        // Set up adapter
        faqAdapter = new FAQAdapter(faqList);
        recyclerView.setAdapter(faqAdapter);

        return view;
    }
}