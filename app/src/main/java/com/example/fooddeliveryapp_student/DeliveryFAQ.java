package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DeliveryFAQ extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FAQAdapter adapter;
    private List<FAQ> faqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_faq); // Make sure this XML file exists

        recyclerView = findViewById(R.id.recyclerViewDeliveryFAQ); // Make sure the ID matches XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Use "this" for Activity context

        // Sample FAQ data
        faqList = new ArrayList<>();
        faqList.add(new FAQ("How to pick up orders?", "Go to the assigned order and pick up the product."));
        faqList.add(new FAQ("What to do if I can't find the customer?", "Contact the customer directly using the app’s contact feature."));
        faqList.add(new FAQ("How do I mark an order as delivered?", "Click on the 'Delivered' button in your order details."));
        faqList.add(new FAQ("Can I reject a delivery assignment?", "No, deliveries are auto-assigned based on availability."));
        faqList.add(new FAQ("How to update my delivery status?", "The status is updated automatically when you mark an order as delivered."));
        faqList.add(new FAQ("What happens if an order gets canceled?", "No option for order cancellation."));
        faqList.add(new FAQ("How do I get paid for deliveries?", "Payments are automatically credited based on completed deliveries."));
        faqList.add(new FAQ("How to contact support?", "Contact support via the 'Help & Support' section in the app."));

        // Setup adapter
        adapter = new FAQAdapter(faqList);
        recyclerView.setAdapter(adapter);
    }
}
