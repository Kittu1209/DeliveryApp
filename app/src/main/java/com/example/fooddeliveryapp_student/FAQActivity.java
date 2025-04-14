package com.example.fooddeliveryapp_student;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FAQAdapter faqAdapter;
    private List<FAQ> faqList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqactivity);

        recyclerView = findViewById(R.id.recyclerViewFAQ);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        faqList = new ArrayList<>();
        faqList.add(new FAQ("How to register?", "You can register using your email and password."));
        faqList.add(new FAQ("How to reset password?", "Go to login page and click on 'Forgot Password'."));
        faqList.add(new FAQ("How to contact support?", "You can email us at support@example.com."));
        faqList.add(new FAQ("How to add items to cart?", "Browse products, open details, and tap 'Add to Cart'."));
        faqList.add(new FAQ("How to change delivery address?", "Go to cart and tap 'Pay now' then u will get page to Add/Edit delivery option."));
        faqList.add(new FAQ("Can I cancel my order?", "Once placed, orders cannot be canceled."));
        faqList.add(new FAQ("How do I know if my order is delivered?", "You’ll get updated once delivered. Go amd check My Orders."));
        faqList.add(new FAQ("How are payments handled?", "We use Razorpay for secure online payments."));
        faqList.add(new FAQ("How to edit my profile?", "Go to Settings > My Profile and tap edit icon."));
        faqList.add(new FAQ("What if I entered wrong information?", "You can update it anytime from your profile."));

        faqAdapter = new FAQAdapter(faqList);
        recyclerView.setAdapter(faqAdapter);
    }
}
