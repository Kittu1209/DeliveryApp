package com.example.fooddeliveryapp_student;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    private boolean isEditing = false;

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
        setupTextWatchers();
        setBlackTextColor();

        // Check authentication
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            ownerId = user.getUid();
            checkShopStatus();
        } else {
            showAuthErrorAndFinish();
        }

        // Setup cuisine spinner
        setupCuisineSpinner();

        // Set click listeners
        saveBtn.setOnClickListener(v -> validateAndSaveShopDetails());
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

    private void setBlackTextColor() {
        addressInput.setTextColor(Color.BLACK);
        descriptionInput.setTextColor(Color.BLACK);
        deliveryTimeInput.setTextColor(Color.BLACK);
        priceForTwoInput.setTextColor(Color.BLACK);
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs();
            }
        };

        addressInput.addTextChangedListener(textWatcher);
        descriptionInput.addTextChangedListener(textWatcher);
        deliveryTimeInput.addTextChangedListener(textWatcher);
        priceForTwoInput.addTextChangedListener(textWatcher);
    }

    private void validateInputs() {
        boolean isValid = !addressInput.getText().toString().trim().isEmpty() &&
                !descriptionInput.getText().toString().trim().isEmpty() &&
                !deliveryTimeInput.getText().toString().trim().isEmpty() &&
                !priceForTwoInput.getText().toString().trim().isEmpty() &&
                (imageUri != null || !imageUrl.isEmpty());

        saveBtn.setEnabled(isValid);
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

    private void checkShopStatus() {
        db.collection(SHOPS_COLLECTION).document(ownerId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        loadShopData();
                    } else {
                        createNewShopDocument();
                    }
                })
                .addOnFailureListener(e -> {
                    showErrorAndFinish("Failed to check shop status");
                });
    }

    private void createNewShopDocument() {
        Map<String, Object> initialData = new HashMap<>();
        initialData.put("ownerId", ownerId);
        initialData.put("isActive", false);
        initialData.put("createdAt", FieldValue.serverTimestamp());

        db.collection(SHOPS_COLLECTION).document(ownerId)
                .set(initialData)
                .addOnSuccessListener(aVoid -> enableEditing())
                .addOnFailureListener(e -> showErrorAndFinish("Failed to create shop"));
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
        isEditing = true;
        addressInput.setEnabled(true);
        descriptionInput.setEnabled(true);
        deliveryTimeInput.setEnabled(true);
        priceForTwoInput.setEnabled(true);
        cuisineSpinner.setEnabled(true);
        uploadImageBtn.setEnabled(true);

        saveBtn.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.GONE);
        validateInputs();
    }

    private void disableEditing() {
        isEditing = false;
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
            validateInputs();
        }
    }

    private void validateAndSaveShopDetails() {
        String address = addressInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String deliveryTime = deliveryTimeInput.getText().toString().trim();
        String priceForTwoStr = priceForTwoInput.getText().toString().trim();
        String cuisine = cuisineSpinner.getSelectedItem().toString();

        // Validate inputs
        if (address.isEmpty()) {
            addressLayout.setError("Address is required");
            return;
        } else {
            addressLayout.setError(null);
        }

        if (description.isEmpty()) {
            descriptionLayout.setError("Description is required");
            return;
        } else {
            descriptionLayout.setError(null);
        }

        if (deliveryTime.isEmpty()) {
            deliveryTimeLayout.setError("Delivery time is required");
            return;
        } else {
            deliveryTimeLayout.setError(null);
        }

        if (priceForTwoStr.isEmpty()) {
            priceForTwoLayout.setError("Price for two is required");
            return;
        } else {
            priceForTwoLayout.setError(null);
        }

        long priceForTwo;
        try {
            priceForTwo = Long.parseLong(priceForTwoStr);
        } catch (NumberFormatException e) {
            priceForTwoLayout.setError("Invalid price");
            return;
        }

        if (imageUri == null && imageUrl.isEmpty()) {
            Toast.makeText(this, "Please upload a shop image", Toast.LENGTH_SHORT).show();
            return;
        }

        saveShopDetails(address, description, deliveryTime, priceForTwo, cuisine);
    }

    private void saveShopDetails(String address, String description, String deliveryTime,
                                 long priceForTwo, String cuisine) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Shop Details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (imageUri != null) {
            uploadImage(progressDialog, address, description, deliveryTime, priceForTwo, cuisine);
        } else {
            saveShopDataToFirestore(progressDialog, address, description, deliveryTime,
                    priceForTwo, cuisine, imageUrl);
        }
    }

    private void uploadImage(ProgressDialog progressDialog, String address, String description,
                             String deliveryTime, long priceForTwo, String cuisine) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(SHOP_IMAGES_FOLDER + UUID.randomUUID().toString());

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                saveShopDataToFirestore(progressDialog, address, description, deliveryTime,
                        priceForTwo, cuisine, uri.toString());
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showErrorDialog("Failed to upload image: " + e.getMessage());
        });
    }

    private void saveShopDataToFirestore(ProgressDialog progressDialog, String address, String description,
                                         String deliveryTime, long priceForTwo, String cuisine, String imageUrl) {
        Map<String, Object> shopData = new HashMap<>();
        shopData.put("address", address);
        shopData.put("description", description);
        shopData.put("deliveryTime", deliveryTime);
        shopData.put("priceForTwo", priceForTwo);
        shopData.put("cuisine", cuisine);
        shopData.put("image", imageUrl);
        shopData.put("updatedAt", FieldValue.serverTimestamp());
        shopData.put("isActive", false);

        // First update the Vendors collection with shopAddress
        Map<String, Object> vendorUpdate = new HashMap<>();
        vendorUpdate.put("shopAddress", address);
        vendorUpdate.put("updatedAt", FieldValue.serverTimestamp());

        db.collection(VENDORS_COLLECTION).document(ownerId)
                .update(vendorUpdate)
                .addOnSuccessListener(aVoid -> {
                    // After updating Vendors, proceed with updating Shops collection
                    db.collection(VENDORS_COLLECTION).document(ownerId).get()
                            .addOnSuccessListener(vendorDocument -> {
                                if (vendorDocument.exists()) {
                                    shopData.put("name", vendorDocument.getString("shopName"));
                                    shopData.put("email", vendorDocument.getString("vendorEmail"));
                                    shopData.put("phone", vendorDocument.getString("vendorPhone"));

                                    db.collection(SHOPS_COLLECTION).document(ownerId)
                                            .set(shopData, SetOptions.merge())
                                            .addOnSuccessListener(aVoid1 -> {
                                                progressDialog.dismiss();
                                                showSuccessDialog();
                                            })
                                            .addOnFailureListener(e -> {
                                                progressDialog.dismiss();
                                                showErrorDialog("Failed to save shop details");
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                showErrorDialog("Failed to load vendor data");
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showErrorDialog("Failed to update vendor address");
                });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Shop details saved successfully!")
                .setPositiveButton("OK", (dialog, which) -> {
                    disableEditing();
                    navigateToHomePage();
                })
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAuthErrorAndFinish() {
        Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, HomePageVendor.class);
        intent.putExtra("fragment", "vendor_home");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isEditing", isEditing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isEditing = savedInstanceState.getBoolean("isEditing", false);
        if (isEditing) {
            enableEditing();
        } else {
            disableEditing();
        }
    }
}