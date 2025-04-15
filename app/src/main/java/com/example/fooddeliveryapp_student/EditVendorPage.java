package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditVendorPage extends AppCompatActivity {

    private EditText name, phone, shop, vendorIdEdit;
    private Button saveBtn;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vendor_page);

        name = findViewById(R.id.editVendorName);
        phone = findViewById(R.id.editVendorPhone);
        shop = findViewById(R.id.editVendorShop);
        vendorIdEdit = findViewById(R.id.editVendorId); // this field will be read-only
        saveBtn = findViewById(R.id.btnSaveVendor);

        // Getting vendor details passed from intent
        docId = getIntent().getStringExtra("docId");

        name.setText(getIntent().getStringExtra("vendorName"));
        phone.setText(getIntent().getStringExtra("vendorPhone"));
        shop.setText(getIntent().getStringExtra("shopName"));
        vendorIdEdit.setText(getIntent().getStringExtra("vendorId")); // vendorId is passed but not editable

        // Disable email and vendorId fields to make them non-editable
        vendorIdEdit.setEnabled(false); // Make vendor ID field read-only

        saveBtn.setOnClickListener(v -> {
            String updatedName = name.getText().toString().trim();
            String updatedPhone = phone.getText().toString().trim();
            String updatedShop = shop.getText().toString().trim();

            // Validation for editable fields
            if (TextUtils.isEmpty(updatedName)) {
                name.setError("Name is required");
                return;
            }

            if (TextUtils.isEmpty(updatedPhone) || !updatedPhone.matches("^[+]?[0-9]{10,13}$")) { // Basic phone validation
                phone.setError("Enter a valid phone number");
                return;
            }

            if (TextUtils.isEmpty(updatedShop)) {
                shop.setError("Shop name is required");
                return;
            }

            // Proceed with Firebase update if all validations pass
            FirebaseFirestore.getInstance().collection("Vendors")
                    .document(docId)
                    .update(
                            "vendorName", updatedName,
                            "vendorPhone", updatedPhone,
                            "shopName", updatedShop,
                            "lastUpdatedAt", FieldValue.serverTimestamp()
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Vendor updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
