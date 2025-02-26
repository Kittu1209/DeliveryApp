package com.example.fooddeliveryapp_student;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class ProductEditActivity extends AppCompatActivity {

    private EditText editTextProductName, editTextProductCategory, editTextProductPrice, editTextProductDescription;
    private ImageView imageViewProduct;
    private Button buttonUpdateProduct;
    private FirebaseFirestore db;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        db = FirebaseFirestore.getInstance();

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductCategory = findViewById(R.id.editTextProductCategory);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        buttonUpdateProduct = findViewById(R.id.buttonUpdateProduct);

        productId = getIntent().getStringExtra("PRODUCT_ID");
        getProductById(productId);

        buttonUpdateProduct.setOnClickListener(v -> updateProduct(productId));
    }

    private void getProductById(String productId) {
        db.collection("products").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editTextProductName.setText(documentSnapshot.getString("name"));
                        editTextProductCategory.setText(documentSnapshot.getString("category"));
                        editTextProductPrice.setText(String.valueOf(documentSnapshot.getDouble("price")));
                        editTextProductDescription.setText(documentSnapshot.getString("description"));
                        Picasso.get().load(documentSnapshot.getString("imageUrl")).into(imageViewProduct);
                    }
                });
    }

    private void updateProduct(String productId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", editTextProductName.getText().toString().trim());
        updates.put("category", editTextProductCategory.getText().toString().trim());
        updates.put("price", Double.parseDouble(editTextProductPrice.getText().toString().trim()));
        updates.put("description", editTextProductDescription.getText().toString().trim());
        updates.put("updatedAt", Timestamp.now());

        db.collection("products").document(productId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Product Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
