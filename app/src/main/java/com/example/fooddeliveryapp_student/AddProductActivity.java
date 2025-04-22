package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final String TAG = "AddProductActivity";

    // UI Components
    private TextInputEditText etProductName, etProductPrice, etProductDescription, etStockQuantity;
    private ImageView imgProduct;
    private Spinner spinnerCategory;
    private CheckBox cbAvailable;
    private Button btnUploadImage, btnAddProduct;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String shopId;

    // Image Handling
    private Bitmap selectedBitmap;
    private String encodedImage;

    // Category data
    private List<String> categoryNames = new ArrayList<>();
    private List<String> categoryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        initViews();

        // Set Click Listeners
        setupClickListeners();

        // Load Data
        fetchShopId();
        loadCategories();
    }

    private void initViews() {
        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductDescription = findViewById(R.id.etProductDescription);
        etStockQuantity = findViewById(R.id.etStockQuantity);
        imgProduct = findViewById(R.id.imgProduct);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        cbAvailable = findViewById(R.id.cbAvailable);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        // Set default stock quantity
        etStockQuantity.setText("1");
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnAddProduct.setOnClickListener(v -> {
            if (validateForm()) {
                uploadProduct();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        // Validate Product Name
        if (TextUtils.isEmpty(etProductName.getText())) {
            etProductName.setError("Product name is required");
            valid = false;
        }

        // Validate Price
        if (TextUtils.isEmpty(etProductPrice.getText())) {
            etProductPrice.setError("Price is required");
            valid = false;
        } else {
            try {
                double price = Double.parseDouble(etProductPrice.getText().toString());
                if (price <= 0) {
                    etProductPrice.setError("Price must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                etProductPrice.setError("Invalid price format");
                valid = false;
            }
        }

        // Validate Description
        if (TextUtils.isEmpty(etProductDescription.getText())) {
            etProductDescription.setError("Description is required");
            valid = false;
        }

        // Validate Stock Quantity
        if (TextUtils.isEmpty(etStockQuantity.getText())) {
            etStockQuantity.setError("Stock quantity is required");
            valid = false;
        } else {
            try {
                int stock = Integer.parseInt(etStockQuantity.getText().toString());
                if (stock < 0) {
                    etStockQuantity.setError("Stock cannot be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                etStockQuantity.setError("Invalid quantity");
                valid = false;
            }
        }

        // Validate Image
        if (encodedImage == null || encodedImage.isEmpty()) {
            Toast.makeText(this, "Please upload a product image", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void uploadProduct() {
        // Show loading dialog if you have one
        // progressDialog.show();

        // Get form values
        String name = etProductName.getText().toString().trim();
        double price = Double.parseDouble(etProductPrice.getText().toString().trim());
        String description = etProductDescription.getText().toString().trim();
        int stock = Integer.parseInt(etStockQuantity.getText().toString().trim());
        String categoryId = categoryIds.get(spinnerCategory.getSelectedItemPosition()); // Get the ID instead of name
        boolean available = cbAvailable.isChecked();

        // Create product data
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("price", price);
        product.put("description", description);
        product.put("category", categoryId); // Store category ID instead of name
        product.put("imageUrl", encodedImage); // Storing Base64 encoded string
        product.put("shopId", shopId);
        product.put("available", available);
        product.put("stockQuantity", stock);
        product.put("createdAt", FieldValue.serverTimestamp());
        product.put("updatedAt", FieldValue.serverTimestamp());

        // Add to Firestore
        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add product: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error adding product", e);
                });
    }

    private void fetchShopId() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("shops")
                .whereEqualTo("ownerId", user.getUid())
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        shopId = task.getResult().getDocuments().get(0).getId();
                        Log.d(TAG, "Shop ID: " + shopId);
                    } else {
                        Toast.makeText(this, "No shop found for this user", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryNames.clear();
                        categoryIds.clear();

                        for (DocumentSnapshot doc : task.getResult()) {
                            String name = doc.getString("name");
                            if (name != null) {
                                categoryNames.add(name);
                                categoryIds.add(doc.getId()); // Store the document ID
                            }
                        }

                        if (categoryNames.isEmpty()) {
                            categoryNames.add("Uncategorized");
                            categoryIds.add("uncategorized"); // You might want to handle this case properly
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                categoryNames
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading categories", task.getException());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Get and display the bitmap
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProduct.setImageBitmap(selectedBitmap);

                // Convert to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (IOException e) {
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Image processing error", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Clean up bitmap memory
        if (selectedBitmap != null && !selectedBitmap.isRecycled()) {
            selectedBitmap.recycle();
            selectedBitmap = null;
        }
        super.onDestroy();
    }
}