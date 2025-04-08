package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditVendorPage extends AppCompatActivity {

    private EditText name, email, phone, shop, vendorIdEdit;
    private Button saveBtn;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vendor_page);

        name = findViewById(R.id.editVendorName);
        email = findViewById(R.id.editVendorEmail);
        phone = findViewById(R.id.editVendorPhone);
        shop = findViewById(R.id.editVendorShop);
        vendorIdEdit = findViewById(R.id.editVendorId);
        saveBtn = findViewById(R.id.btnSaveVendor);

        // Getting vendor details passed from intent
        docId = getIntent().getStringExtra("docId");

        name.setText(getIntent().getStringExtra("vendorName"));
        email.setText(getIntent().getStringExtra("vendorEmail"));
        phone.setText(getIntent().getStringExtra("vendorPhone"));
        shop.setText(getIntent().getStringExtra("shopName"));
        vendorIdEdit.setText(getIntent().getStringExtra("vendorId"));

        saveBtn.setOnClickListener(v -> {
            String updatedName = name.getText().toString();
            String updatedEmail = email.getText().toString();
            String updatedPhone = phone.getText().toString();
            String updatedShop = shop.getText().toString();
            String updatedVendorId = vendorIdEdit.getText().toString();

            FirebaseFirestore.getInstance().collection("Vendors")
                    .document(docId)
                    .update(
                            "vendorName", updatedName,
                            "vendorEmail", updatedEmail,
                            "vendorPhone", updatedPhone,
                            "shopName", updatedShop,
                            "vendorId", updatedVendorId,
                            "lastUpdatedAt", FieldValue.serverTimestamp()
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Vendor updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
