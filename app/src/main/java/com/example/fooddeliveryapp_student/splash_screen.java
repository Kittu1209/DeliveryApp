package com.example.fooddeliveryapp_student;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class splash_screen extends AppCompatActivity {

    private static final int SPLASH_TIME = 6000; // 6 seconds delay
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Start animations
        startBackgroundAnimations();
        startLogoAnimations();
        startTextAnimations();
        setupFeatures();
        startLoadingAnimation();

        new Handler().postDelayed(this::checkUserStatus, SPLASH_TIME);
    }

    private void startBackgroundAnimations() {
        ImageView orb1 = findViewById(R.id.bg_orb_1);
        ImageView orb2 = findViewById(R.id.bg_orb_2);

        // Orb 1 animation
        AnimatorSet orb1Set = new AnimatorSet();
        ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(orb1, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(orb1, "scaleY", 1f, 1.5f, 1f);
        ObjectAnimator rotate1 = ObjectAnimator.ofFloat(orb1, "rotation", 0f, 180f, 360f);
        ObjectAnimator transX1 = ObjectAnimator.ofFloat(orb1, "translationX", 0f, 100f, 0f);
        ObjectAnimator transY1 = ObjectAnimator.ofFloat(orb1, "translationY", 0f, -50f, 0f);
        orb1Set.playTogether(scaleX1, scaleY1, rotate1, transX1, transY1);
        orb1Set.setDuration(25000);
        orb1Set.setInterpolator(new AccelerateDecelerateInterpolator());
        orb1Set.start();

        // Orb 2 animation
        AnimatorSet orb2Set = new AnimatorSet();
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(orb2, "scaleX", 1.2f, 0.8f, 1.2f);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(orb2, "scaleY", 1.2f, 0.8f, 1.2f);
        ObjectAnimator rotate2 = ObjectAnimator.ofFloat(orb2, "rotation", 0f, -180f, -360f);
        ObjectAnimator transX2 = ObjectAnimator.ofFloat(orb2, "translationX", 0f, -100f, 0f);
        ObjectAnimator transY2 = ObjectAnimator.ofFloat(orb2, "translationY", 0f, 50f, 0f);
        orb2Set.playTogether(scaleX2, scaleY2, rotate2, transX2, transY2);
        orb2Set.setDuration(20000);
        orb2Set.setInterpolator(new AccelerateDecelerateInterpolator());
        orb2Set.start();
    }

    private void startLogoAnimations() {
        ImageView heartIcon = findViewById(R.id.heart_icon);
        ImageView packageIcon = findViewById(R.id.package_icon);
        ImageView truckIcon = findViewById(R.id.truck_icon);
        ImageView bagIcon = findViewById(R.id.bag_icon);
        ImageView mapIcon = findViewById(R.id.map_icon);

        // Heart pulse animation
        AnimatorSet heartSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(heartIcon, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(heartIcon, "scaleY", 1f, 1.2f, 1f);
        heartSet.playTogether(scaleX, scaleY);
        heartSet.setDuration(2000);
        heartSet.setInterpolator(new AccelerateDecelerateInterpolator());
        heartSet.start();

        // Orbiting icons animation
        ObjectAnimator rotation = ObjectAnimator.ofFloat(heartIcon, "rotation", 0f, 360f);
        rotation.setDuration(10000);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();

        // Make orbiting icons follow the rotation
        packageIcon.setRotation(0f);
        truckIcon.setRotation(0f);
        bagIcon.setRotation(0f);
        mapIcon.setRotation(0f);

        heartIcon.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            packageIcon.setRotation(-heartIcon.getRotation());
            truckIcon.setRotation(-heartIcon.getRotation());
            bagIcon.setRotation(-heartIcon.getRotation());
            mapIcon.setRotation(-heartIcon.getRotation());
        });
    }

    private void startTextAnimations() {
        TextView title = findViewById(R.id.title_text);
        TextView subtitle = findViewById(R.id.subtitle_text);
        TextView deliveryText = findViewById(R.id.delivery_text);
        TextView applicationText = findViewById(R.id.application_text);

        // Title animation
        title.setAlpha(0f);
        title.animate()
                .alpha(1f)
                .setDuration(1000)
                .start();

        // Subtitle animation
        subtitle.setTranslationY(50f);
        subtitle.setAlpha(0f);
        subtitle.animate()
                .translationY(0f)
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(800)
                .start();

        // Split text animation
        deliveryText.setTranslationX(-50f);
        deliveryText.setAlpha(0f);
        deliveryText.animate()
                .translationX(0f)
                .alpha(1f)
                .setStartDelay(700)
                .setDuration(800)
                .start();

        applicationText.setTranslationX(50f);
        applicationText.setAlpha(0f);
        applicationText.animate()
                .translationX(0f)
                .alpha(1f)
                .setStartDelay(900)
                .setDuration(800)
                .start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFeatures() {
        LinearLayout featuresContainer = findViewById(R.id.features_container);

        // Reordered to match the image with "Campus Delivery" first
        int[] iconResources = {
                R.drawable.ic_package,    // Campus Delivery
                R.drawable.ic_truck,      // Track Orders
                R.drawable.ic_shopping_bag, // Easy Shopping
                R.drawable.ic_clock       // Quick Service
        };

        String[] featureTexts = {
                "Campus Delivery",  // First item as shown in image
                "Track Orders",
                "Easy Shopping",
                "Quick Service"
        };

        // Clear any existing views if called multiple times
        featuresContainer.removeAllViews();

        for (int i = 0; i < iconResources.length; i++) {
            View featureView = getLayoutInflater().inflate(R.layout.item_feature, featuresContainer, false);
            ImageView icon = featureView.findViewById(R.id.feature_icon);
            TextView text = featureView.findViewById(R.id.feature_text);

            icon.setImageResource(iconResources[i]);
            text.setText(featureTexts[i]);

            // Add hover/click animations
            featureView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return true;
            });

            featuresContainer.addView(featureView);
        }
    }
    private void startLoadingAnimation() {
        View loadingBar = findViewById(R.id.loading_bar);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(2000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            loadingBar.setScaleX(value);
            loadingBar.setTranslationX((value - 0.5f) * loadingBar.getWidth());
        });
        animator.start();
    }

    private void checkUserStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Check if the user is an Admin first (since admins might have higher privileges)
            db.collection("Admins").document(userId).get().addOnCompleteListener(adminTask -> {
                if (adminTask.isSuccessful() && adminTask.getResult().exists()) {
                    // User is an Admin
                    startActivity(new Intent(splash_screen.this, HomePageAdmin.class));
                    finish();
                } else {
                    // If not admin, check if the user is a Vendor
                    db.collection("Vendors").document(userId).get().addOnCompleteListener(vendorTask -> {
                        if (vendorTask.isSuccessful() && vendorTask.getResult().exists()) {
                            // User is a Vendor
                            startActivity(new Intent(splash_screen.this, HomePageVendor.class));
                            finish();
                        } else {
                            // If not vendor, check if the user is a Delivery Man
                            db.collection("delivery_man").document(userId).get().addOnCompleteListener(deliveryTask -> {
                                if (deliveryTask.isSuccessful() && deliveryTask.getResult().exists()) {
                                    // User is a Delivery Man
                                    startActivity(new Intent(splash_screen.this, DeliveryHomeActivity.class));
                                    finish();
                                } else {
                                    // If not delivery man, check if the user is a Student
                                    db.collection("Students").document(userId).get().addOnCompleteListener(studentTask -> {
                                        if (studentTask.isSuccessful() && studentTask.getResult().exists()) {
                                            // User is a Student
                                            startActivity(new Intent(splash_screen.this, HomePage_Student.class));
                                        } else {
                                            // User exists but not categorized, send to login
                                            startActivity(new Intent(splash_screen.this, LoginPage.class));
                                        }
                                        finish();
                                    });
                                }
                            });
                        }
                    });
                }
            });
        } else {
            // No user logged in, redirect to login screen
            startActivity(new Intent(splash_screen.this, LoginPage.class));
            finish();
        }
    }
}