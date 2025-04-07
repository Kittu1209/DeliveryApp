package com.example.fooddeliveryapp_student;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddCategoryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 101;
    private EditText etName, etDescription;
    private Switch switchActive;
    private ImageView imgCategory;
    private Button btnSelectImage, btnAddCategory;

    private String base64Image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        switchActive = findViewById(R.id.switchActive);
        imgCategory = findViewById(R.id.imgCategory);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        btnSelectImage.setOnClickListener(v -> openGallery());

        btnAddCategory.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            boolean isActive = switchActive.isChecked();

            if (name.isEmpty() || base64Image.isEmpty()) {
                Toast.makeText(this, "Name and image required", Toast.LENGTH_SHORT).show();
                return;
            }

            String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-");

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("slug", slug);
            data.put("description", description);
            data.put("image", base64Image);
            data.put("color", "#FF4B2B"); // Static color value
            data.put("isActive", isActive);
            data.put("createdAt", Timestamp.now());
            data.put("updatedAt", Timestamp.now());

            db.collection("categories")
                    .add(data)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Category Added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgCategory.setImageBitmap(bitmap);
                base64Image = encodeImageToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
