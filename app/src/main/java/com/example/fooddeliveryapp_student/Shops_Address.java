package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Shops_Address extends AppCompatActivity {

    // UI Components
    private TextInputLayout addressLayout, descriptionLayout, deliveryTimeLayout,
            priceForTwoLayout, cuisineLayout;
    private TextInputEditText addressInput, descriptionInput, deliveryTimeInput,
            priceForTwoInput;
    private Spinner cuisineSpinner;
    private Button saveBtn, updateBtn, uploadImageBtn;
    private ImageView shopImageView;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String ownerId;
    private Uri imageUri;
    private String imageUrl = "";

    // Constants
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String SHOPS_COLLECTION = "shops";
    private static final String VENDORS_COLLECTION = "Vendors";
    private static final String SHOP_IMAGES_FOLDER = "shops/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops_address);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI components
        initViews();

        // Check authentication
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            ownerId = user.getUid();
            initializeShop();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup cuisine spinner
        setupCuisineSpinner();

        // Set click listeners
        saveBtn.setOnClickListener(v -> saveShopDetails());
        updateBtn.setOnClickListener(v -> enableEditing());
        uploadImageBtn.setOnClickListener(v -> openImageChooser());
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        addressLayout = findViewById(R.id.address_layout);
        descriptionLayout = findViewById(R.id.description_layout);
        deliveryTimeLayout = findViewById(R.id.delivery_time_layout);
        priceForTwoLayout = findViewById(R.id.price_for_two_layout);
        cuisineLayout = findViewById(R.id.cuisine_layout);

        addressInput = findViewById(R.id.address_input);
        descriptionInput = findViewById(R.id.description_input);
        deliveryTimeInput = findViewById(R.id.delivery_time_input);
        priceForTwoInput = findViewById(R.id.price_for_two_input);

        cuisineSpinner = findViewById(R.id.cuisine_spinner);
        shopImageView = findViewById(R.id.shop_image_view);
        uploadImageBtn = findViewById(R.id.upload_image_btn);

        saveBtn = findViewById(R.id.save_btn);
        updateBtn = findViewById(R.id.update_btn);
    }

    private void setupCuisineSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.cuisine_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuisineSpinner.setAdapter(adapter);
    }

    private void initializeShop() {
        ShopManager.checkAndCreateShop(this, new ShopManager.ShopCheckCallback() {
            @Override
            public void onShopExists(String shopId) {
                loadShopData();
            }

            @Override
            public void onShopDoesNotExist() {
                // Shouldn't happen as we create if doesn't exist
            }

            @Override
            public void onError(String message) {
                Toast.makeText(Shops_Address.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadShopData() {
        DocumentReference shopRef = db.collection(SHOPS_COLLECTION).document(ownerId);
        shopRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Set existing values
                if (documentSnapshot.contains("address")) {
                    addressInput.setText(documentSnapshot.getString("address"));
                }
                if (documentSnapshot.contains("description")) {
                    descriptionInput.setText(documentSnapshot.getString("description"));
                }
                if (documentSnapshot.contains("deliveryTime")) {
                    deliveryTimeInput.setText(documentSnapshot.getString("deliveryTime"));
                }
                if (documentSnapshot.contains("priceForTwo")) {
                    priceForTwoInput.setText(String.valueOf(documentSnapshot.getLong("priceForTwo")));
                }
                if (documentSnapshot.contains("cuisine")) {
                    String cuisine = documentSnapshot.getString("cuisine");
                    int position = Arrays.asList(getResources().getStringArray(R.array.cuisine_types))
                            .indexOf(cuisine);
                    if (position >= 0) {
                        cuisineSpinner.setSelection(position);
                    }
                }
                if (documentSnapshot.contains("image")) {
                    imageUrl = documentSnapshot.getString("image");
                    Picasso.get().load(imageUrl).into(shopImageView);
                }

                // Check if all required fields are filled
                boolean isComplete = documentSnapshot.contains("address") &&
                        documentSnapshot.contains("description") &&
                        documentSnapshot.contains("deliveryTime") &&
                        documentSnapshot.contains("priceForTwo") &&
                        documentSnapshot.contains("cuisine") &&
                        documentSnapshot.contains("image");

                if (isComplete) {
                    disableEditing();
                } else {
                    enableEditing();
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load shop data", Toast.LENGTH_SHORT).show());
    }

    private void enableEditing() {
        addressInput.setEnabled(true);
        descriptionInput.setEnabled(true);
        deliveryTimeInput.setEnabled(true);
        priceForTwoInput.setEnabled(true);
        cuisineSpinner.setEnabled(true);
        uploadImageBtn.setEnabled(true);

        saveBtn.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.GONE);
    }

    private void disableEditing() {
        addressInput.setEnabled(false);
        descriptionInput.setEnabled(false);
        deliveryTimeInput.setEnabled(false);
        priceForTwoInput.setEnabled(false);
        cuisineSpinner.setEnabled(false);
        uploadImageBtn.setEnabled(false);

        saveBtn.setVisibility(View.GONE);
        updateBtn.setVisibility(View.VISIBLE);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(shopImageView);
        }
    }

    private void saveShopDetails() {
        String address = addressInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String deliveryTime = deliveryTimeInput.getText().toString().trim();
        String priceForTwoStr = priceForTwoInput.getText().toString().trim();
        String cuisine = cuisineSpinner.getSelectedItem().toString();

        // Validate inputs
        if (address.isEmpty()) {
            addressInput.setError("Address is required");
            return;
        }
        if (description.isEmpty()) {
            descriptionInput.setError("Description is required");
            return;
        }
        if (deliveryTime.isEmpty()) {
            deliveryTimeInput.setError("Delivery time is required");
            return;
        }
        if (priceForTwoStr.isEmpty()) {
            priceForTwoInput.setError("Price for two is required");
            return;
        }
        if (imageUri == null && imageUrl.isEmpty()) {
            Toast.makeText(this, "Please upload a shop image", Toast.LENGTH_SHORT).show();
            return;
        }

        long priceForTwo;
        try {
            priceForTwo = Long.parseLong(priceForTwoStr);
        } catch (NumberFormatException e) {
            priceForTwoInput.setError("Invalid price");
            return;
        }

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Shop Details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Upload image first if new image is selected
        if (imageUri != null) {
            uploadImage(progressDialog, address, description, deliveryTime, priceForTwo, cuisine);
        } else {
            // No new image, just save other details
            saveShopDataToFirestore(progressDialog, address, description, deliveryTime, priceForTwo, cuisine, imageUrl);
        }
    }

    private void uploadImage(ProgressDialog progressDialog, String address, String description,
                             String deliveryTime, long priceForTwo, String cuisine) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(SHOP_IMAGES_FOLDER + UUID.randomUUID().toString());

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveShopDataToFirestore(progressDialog, address, description, deliveryTime,
                        priceForTwo, cuisine, imageUrl);
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveShopDataToFirestore(ProgressDialog progressDialog, String address, String description,
                                         String deliveryTime, long priceForTwo, String cuisine, String imageUrl) {
        // Create update data
        Map<String, Object> shopData = new HashMap<>();
        shopData.put("address", address);
        shopData.put("description", description);
        shopData.put("deliveryTime", deliveryTime);
        shopData.put("priceForTwo", priceForTwo);
        shopData.put("cuisine", cuisine);
        shopData.put("image", imageUrl);
        shopData.put("updatedAt", FieldValue.serverTimestamp());
        shopData.put("isActive", true);

        // Add data from Vendors collection
        db.collection(VENDORS_COLLECTION).document(ownerId).get()
                .addOnSuccessListener(vendorDocument -> {
                    if (vendorDocument.exists()) {
                        shopData.put("name", vendorDocument.getString("shopName"));
                        shopData.put("email", vendorDocument.getString("vendorEmail"));
                        shopData.put("phone", vendorDocument.getString("vendorPhone"));
                        shopData.put("ownerId", ownerId);

                        // Save to shops collection
                        db.collection(SHOPS_COLLECTION).document(ownerId)
                                .set(shopData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, "Shop details saved successfully!", Toast.LENGTH_SHORT).show();

                                    // Clear all fields
                                    clearFormFields();

                                    // Navigate to home page
                                    navigateToHomePage();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, "Failed to save shop details", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to load vendor data", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFormFields() {
        addressInput.setText("");
        descriptionInput.setText("");
        deliveryTimeInput.setText("");
        priceForTwoInput.setText("");
        cuisineSpinner.setSelection(0);
        shopImageView.setImageResource(R.drawable.ic_image_placeholder); // Set a default placeholder image
        imageUri = null;
        imageUrl = "";
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, HomePageVendor.class); // Replace with your actual home activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fragment", "vendor_home");
        startActivity(intent);
        finish(); // Finish this activity so user can't go back
    }
}