package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FAQs_Fragment_Vendor extends Fragment {

    private RecyclerView recyclerView;
    private FAQAdapter adapter;
    private List<FAQ> faqList;

    public FAQs_Fragment_Vendor() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_a_qs___vendor, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewVendorFAQ);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        faqList = new ArrayList<>();
        faqList.add(new FAQ("How to add a product?", "Go to home, click 'Add Product' button."));
        faqList.add(new FAQ("Can I edit a product later?", "Yes, go to Product List and click Edit."));
        faqList.add(new FAQ("How to check new orders?", "Go to Orders tab in Home Page."));
        faqList.add(new FAQ("How to mark an order as delivered?", "It can only be done by Delivery man."));
        faqList.add(new FAQ("How to change shop details?", "Go to Profile and edit shop information."));
        faqList.add(new FAQ("How to contact the delivery person?", "You can see assigned delivery person's contact in the order details."));
        faqList.add(new FAQ("What if a customer cancels an order?", "Our app don't have any cancel order policy."));
        faqList.add(new FAQ("Can I add discounts to my products?", "Currently, you can edit product prices manually to reflect discounts."));
        faqList.add(new FAQ("How to view my total sales?", "Go to the Sales Report section in the vendor dashboard."));
        faqList.add(new FAQ("Can I change my vendor email?", "Go to My Profile page and update it"));
        faqList.add(new FAQ("How often does the order list refresh?", "It refreshes in real-time, no need to manually reload."));

        adapter = new FAQAdapter(faqList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
