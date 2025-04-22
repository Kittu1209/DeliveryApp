package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class EditProductActivity extends AppCompatActivity {

    private EditText etName, etPrice, etDescription, etStock, etCategory;
    private ImageView imgProduct;
    private Button btnSave;
    private VendorProduct product;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        db = FirebaseFirestore.getInstance();
        product = getIntent().getParcelableExtra("product");

        initViews();
        populateData();
        setupClickListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        etStock = findViewById(R.id.etStock);
        imgProduct = findViewById(R.id.imgProduct);
        btnSave = findViewById(R.id.btnSave);
    }

    private void populateData() {
        if (product != null) {
            etName.setText(product.getName());
            etPrice.setText(String.valueOf(product.getPrice()));
            etDescription.setText(product.getDescription());
            etStock.setText(String.valueOf(product.getStockQuantity()));

            // Load image if available
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                if (isBase64(product.getImageUrl())) {
                    // If base64, decode and display
                    byte[] decodedString = Base64.decode(product.getImageUrl(), Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgProduct.setImageBitmap(decodedBitmap);
                } else {
                    // If it's a URL, load it using Glide (Firebase Storage or other URLs)
                    Glide.with(this)
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.ic_image_placeholder)  // Placeholder if image is not available
                            .into(imgProduct);
                }
            } else {
                // If no image URL is available, set the placeholder
                imgProduct.setImageResource(R.drawable.ic_image_placeholder);
            }
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                updateProduct();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Name is required");
            valid = false;
        }

        if (TextUtils.isEmpty(etPrice.getText())) {
            etPrice.setError("Price is required");
            valid = false;
        } else {
            try {
                double price = Double.parseDouble(etPrice.getText().toString());
                if (price <= 0) {
                    etPrice.setError("Price must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                etPrice.setError("Invalid price");
                valid = false;
            }
        }

        if (TextUtils.isEmpty(etStock.getText())) {
            etStock.setError("Stock is required");
            valid = false;
        } else {
            try {
                int stock = Integer.parseInt(etStock.getText().toString());
                if (stock < 0) {
                    etStock.setError("Stock cannot be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                etStock.setError("Invalid stock quantity");
                valid = false;
            }
        }

        return valid;
    }

    private void updateProduct() {
        product.setName(etName.getText().toString());
        product.setPrice(Double.parseDouble(etPrice.getText().toString()));
        product.setDescription(etDescription.getText().toString());
        product.setStockQuantity(Integer.parseInt(etStock.getText().toString()));

        db.collection("products").document(product.getId())
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
                });
    }

    // Helper function to check if the string is base64 encoded
    private boolean isBase64(String str) {
        try {
            Base64.decode(str, Base64.DEFAULT);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
