package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etProductName, etProductPrice, etProductDescription;
    private ImageView imgProduct;
    private Spinner spinnerCategory;
    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String shopId = null;  // Store shopId here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize Views
        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductDescription = findViewById(R.id.etProductDescription);
        imgProduct = findViewById(R.id.imgProduct);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        Button btnAddProduct = findViewById(R.id.btnAddProduct);

        // Fetch the shopId from Firestore
        fetchShopId();

        // Load categories from Firestore
        loadCategories();

        // Select Image
        btnUploadImage.setOnClickListener(v -> selectImage());

        // Add Product
        btnAddProduct.setOnClickListener(v -> {
            if (imageUri != null && shopId != null) {
                uploadImage();
            } else if (shopId == null) {
                Toast.makeText(this, "Fetching shop ID. Please wait...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveProductToFirestore(String imageUrl) {
        String productName = etProductName.getText().toString().trim();
        String productPrice = etProductPrice.getText().toString().trim();
        String productDescription = etProductDescription.getText().toString().trim();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        if (productName.isEmpty() || productPrice.isEmpty() || productDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create product object
        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("price", Double.parseDouble(productPrice));
        product.put("description", productDescription);
        product.put("category", selectedCategory);
        product.put("imageUrl", imageUrl);
        product.put("timestamp", FieldValue.serverTimestamp());
        product.put("shopId", shopId);  // Linking product to the shop

        // Save to Firestore under the shop's collection
        db.collection("shops").document(shopId).collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after adding product
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Error adding product", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchShopId() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("shops")
                    .whereEqualTo("ownerId", userId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot shopDoc = queryDocumentSnapshots.getDocuments().get(0);
                            shopId = shopDoc.getId();
                            Toast.makeText(this, "Shop linked: " + shopId, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No shop found. Please set up your shop first.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error fetching shop ID", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<String> categoryList = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String categoryName = document.getString("name");
                            if (categoryName != null) {
                                categoryList.add(categoryName);
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "No categories found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading categories", Toast.LENGTH_SHORT).show());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProduct.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        StorageReference ref = storageReference.child("product_images/" + UUID.randomUUID().toString());
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveProductToFirestore(uri.toString())))
                .addOnFailureListener(e -> Toast.makeText(AddProductActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }
}
