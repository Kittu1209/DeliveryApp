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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etProductName, etProductPrice, etProductDescription;
    private ImageView imgProduct;
    private Spinner spinnerCategory;
    private Uri imageUri;
    private Bitmap selectedBitmap;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String shopId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductDescription = findViewById(R.id.etProductDescription);
        imgProduct = findViewById(R.id.imgProduct);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        Button btnAddProduct = findViewById(R.id.btnAddProduct);

        fetchShopId();
        loadCategories();

        btnUploadImage.setOnClickListener(v -> selectImage());

        btnAddProduct.setOnClickListener(v -> {
            if (!validateInputs()) return;
            if (shopId == null) {
                Toast.makeText(this, "Shop not linked yet. Try again shortly.", Toast.LENGTH_SHORT).show();
                return;
            }
            saveProductToFirestore();
        });
    }

    private boolean validateInputs() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String category = (spinnerCategory.getSelectedItem() != null) ?
                spinnerCategory.getSelectedItem().toString() : "";

        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Product name is required");
            return false;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etProductPrice.setError("Price is required");
            return false;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etProductPrice.setError("Price must be greater than zero");
                return false;
            }
        } catch (NumberFormatException e) {
            etProductPrice.setError("Invalid price format");
            return false;
        }

        if (TextUtils.isEmpty(description)) {
            etProductDescription.setError("Description is required");
            return false;
        }

        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedBitmap == null) {
            Toast.makeText(this, "Please upload a product image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveProductToFirestore() {
        String productName = etProductName.getText().toString().trim();
        double price = Double.parseDouble(etProductPrice.getText().toString().trim());
        String productDescription = etProductDescription.getText().toString().trim();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        // Convert image to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("price", price);
        product.put("description", productDescription);
        product.put("category", selectedCategory);
        product.put("imageUrl", base64Image);
        product.put("shopId", shopId);
        product.put("createdAt", FieldValue.serverTimestamp());
        product.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("AddProduct", "Error adding product", e);
                    Toast.makeText(AddProductActivity.this, "Error adding product: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void fetchShopId() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("shops")
                    .whereEqualTo("ownerId", user.getUid())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            shopId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Log.d("AddProduct", "Shop linked: " + shopId);
                        } else {
                            Toast.makeText(this, "No shop found for this user.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AddProduct", "Error fetching shopId", e);
                        Toast.makeText(this, "Error fetching shop details.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> categoryList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null) categoryList.add(name);
                    }
                    if (categoryList.isEmpty()) categoryList.add("Uncategorized");

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Log.e("AddProduct", "Error loading categories", e);
                    Toast.makeText(this, "Error loading categories", Toast.LENGTH_SHORT).show();
                });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProduct.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
