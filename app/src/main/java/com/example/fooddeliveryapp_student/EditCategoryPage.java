package com.example.fooddeliveryapp_student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditCategoryPage extends AppCompatActivity {

    EditText editCategoryName, editCategorySlug, editCategoryDescription, editCategoryColor;
    ImageView categoryImageView;
    CheckBox checkBoxIsActive;
    Button btnSaveChanges, btnUploadImage;

    FirebaseFirestore db;
    String categoryId;
    String base64Image = "";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getData().getData());
                        categoryImageView.setImageBitmap(bitmap);
                        base64Image = convertBitmapToBase64(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category_page);

        db = FirebaseFirestore.getInstance();

        editCategoryName = findViewById(R.id.editCategoryName);
        editCategorySlug = findViewById(R.id.editCategorySlug);
        editCategoryDescription = findViewById(R.id.editCategoryDescription);
        editCategoryColor = findViewById(R.id.editCategoryColor);
        checkBoxIsActive = findViewById(R.id.checkboxIsActive);
        btnSaveChanges = findViewById(R.id.btnSaveCategoryChanges);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        categoryImageView = findViewById(R.id.imageCategoryEdit);

        // Get data from intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");

        editCategoryName.setText(intent.getStringExtra("name"));
        editCategorySlug.setText(intent.getStringExtra("slug"));
        editCategoryDescription.setText(intent.getStringExtra("description"));
        editCategoryColor.setText(intent.getStringExtra("color"));
        checkBoxIsActive.setChecked(intent.getBooleanExtra("isActive", true));
        base64Image = intent.getStringExtra("image");

        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            categoryImageView.setImageBitmap(decodedByte);
        }

        btnUploadImage.setOnClickListener(v -> openImagePicker());

        btnSaveChanges.setOnClickListener(v -> updateCategory());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void updateCategory() {
        String name = editCategoryName.getText().toString().trim();
        String slug = editCategorySlug.getText().toString().trim();
        String description = editCategoryDescription.getText().toString().trim();
        String color = editCategoryColor.getText().toString().trim();
        boolean isActive = checkBoxIsActive.isChecked();

        // Validation
        if (name.isEmpty()) {
            Toast.makeText(this, "Category name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (slug.isEmpty()) {
            Toast.makeText(this, "Category slug is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Category description is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (color.isEmpty()) {
            Toast.makeText(this, "Category color is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (base64Image.isEmpty()) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Category...");
        dialog.setCancelable(false);
        dialog.show();

        DocumentReference categoryRef = db.collection("categories").document(categoryId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("slug", slug);
        updates.put("description", description);
        updates.put("color", color);
        updates.put("isActive", isActive);
        updates.put("image", base64Image);
        updates.put("updatedAt", Timestamp.now());

        categoryRef.update(updates)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
