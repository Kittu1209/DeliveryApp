package com.example.fooddeliveryapp_student;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Fragment_HomeStudent extends Fragment {

    // UI Components
    private RecyclerView categoriesRecyclerView;
    private RecyclerView shopsRecyclerView;
    private EditText searchBox;
    private View emptyState;
    private MaterialCardView filtercard;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerIndicator;

    // Adapters
    private CategoryAdapterhome categoryAdapter;
    private ShopAdapter shopAdapter;

    // Data Lists
    private List<Category> categoryList = new ArrayList<>();
    private List<Shop> shopList = new ArrayList<>();

    // Firebase
    private FirebaseFirestore db;
    private static final String TAG = "Fragment_HomeStudent";
    private final Handler handler = new Handler();

    public Fragment_HomeStudent() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home_student, container, false);

        db = FirebaseFirestore.getInstance();

        initViews(view);
        setupBannerSlider(); // 👈 Banner setup
        setupAdapters();
        setupListeners();

        loadCategories();
        loadShops();

        return view;
    }

    private void initViews(View view) {
        searchBox = view.findViewById(R.id.search_box);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecycler);
        shopsRecyclerView = view.findViewById(R.id.restaurantsRecycler);
        emptyState = view.findViewById(R.id.emptyState);
        filtercard = view.findViewById(R.id.filterCard);
        bannerViewPager = view.findViewById(R.id.bannerViewPager); // 👈 new

    }

    private void setupBannerSlider() {
        List<Banner> bannerList = new ArrayList<>();
        bannerList.add(new Banner(R.drawable.banner1));
        bannerList.add(new Banner(R.drawable.banner2));
        bannerList.add(new Banner(R.drawable.banner3));

        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), bannerList);
        bannerViewPager.setAdapter(bannerAdapter);

        final Runnable autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerList.size();  // Loop back to the first banner
                bannerViewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 2000); // Auto-scroll every 2 seconds
            }
        };

        handler.postDelayed(autoScrollRunnable, 2000); // Start auto-scrolling after 2 seconds
    }


    private void setupAdapters() {
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapterhome(categoryList, category -> {
            Intent intent = new Intent(getContext(), CategoryShowProduct.class);
            intent.putExtra("name", category.getName());
            startActivity(intent);
        });
        categoriesRecyclerView.setAdapter(categoryAdapter);

        shopsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shopAdapter = new ShopAdapter(shopList, shop -> {
            Intent intent = new Intent(getContext(), ShopProductActivity.class);
            intent.putExtra("id", shop.getOwnerId());
            startActivity(intent);
        });
        shopsRecyclerView.setAdapter(shopAdapter);
    }

    private void setupListeners() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> searchShops(s.toString()), 300);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        filtercard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Filter options coming soon!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), ShowProductActivity.class);
            startActivity(intent);
        });
    }

    private void loadCategories() {
        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
            categoryList.clear();
            categoryList.add(new Category("all", "All", ""));
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Category category = new Category(
                        document.getId(),
                        document.getString("name"),
                        document.getString("image"));

                categoryList.add(category);
            }
            categoryAdapter.notifyDataSetChanged();
            updateCategoryEmptyState();
        });
    }

    private void loadShops() {
        db.collection("shops").get().addOnSuccessListener(queryDocumentSnapshots -> {
            shopList.clear();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Shop shop = document.toObject(Shop.class);
                if (shop != null) {
                    shop.setId(document.getId());
                    shopList.add(shop);
                }
            }
            shopAdapter.updateList(shopList);
            updateEmptyState();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading shops", e);
            Toast.makeText(getContext(), "Failed to load shops", Toast.LENGTH_SHORT).show();
        });
    }

    private void searchShops(String query) {
        if (query.isEmpty()) {
            shopAdapter.updateList(shopList);
            updateEmptyState();
            return;
        }

        List<Shop> filteredList = new ArrayList<>();
        for (Shop shop : shopList) {
            if (shop.getName().toLowerCase().contains(query.toLowerCase()) ||
                    shop.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                    shop.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(shop);
            }
        }
        shopAdapter.updateList(filteredList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        emptyState.setVisibility(shopAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void updateCategoryEmptyState() {
        emptyState.setVisibility(categoryAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
