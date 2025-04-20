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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Fragment_HomeStudent extends Fragment {

    // UI Components
    private RecyclerView categoriesRecyclerView;
    private RecyclerView filtersRecyclerView;
    private RecyclerView shopsRecyclerView;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerIndicator;
    private EditText searchBox;
    private TextView seeAllCategories;
    private TextView seeAllShops;
    private View emptyState;

    // Adapters
    private CategoryAdapterhome categoryAdapter;
    private FilterAdapter filterAdapter;
    private ShopAdapter shopAdapter;
    private BannerAdapter bannerAdapter;

    // Data Lists
    private List<Category> categoryList = new ArrayList<>();
    private List<Filter> filterList = new ArrayList<>();
    private List<Shop> shopList = new ArrayList<>();
    private List<Banner> bannerList = new ArrayList<>();

    // Firebase
    private FirebaseFirestore db;
    private static final String TAG = "Fragment_HomeStudent";
    private final Handler handler = new Handler();

    public Fragment_HomeStudent() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home_student, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        initViews(view);

        // Initialize banner adapter with empty list first
        bannerAdapter = new BannerAdapter(bannerList);
        bannerViewPager.setAdapter(bannerAdapter);

        // Setup other adapters
        setupAdapters();

        // Load data
        loadBanners();  // This will now handle TabLayoutMediator attachment
        loadCategories();
        loadShops();
        setupFilters();

        // Setup listeners
        setupListeners();

        return view;
    }



    private void initViews(View view) {
        searchBox = view.findViewById(R.id.search_box);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecycler);
        filtersRecyclerView = view.findViewById(R.id.filtersRecycler);
        shopsRecyclerView = view.findViewById(R.id.restaurantsRecycler);
        seeAllCategories = view.findViewById(R.id.seeAllCategories);
        seeAllShops = view.findViewById(R.id.seeAllRestaurants);
        emptyState = view.findViewById(R.id.emptyState);
    }

    private void setupAdapters() {
        // Categories adapter
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapterhome(categoryList, category -> {
            filterShopsByCategory(category.getName());
        });
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Filters adapter
        filtersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        filterAdapter = new FilterAdapter(filterList, filter -> {
            applyFilter(filter);
        });
        filtersRecyclerView.setAdapter(filterAdapter);

        // Shops adapter
        shopsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shopAdapter = new ShopAdapter(shopList, shop -> {
            openShopDetails(shop);
        });
        shopsRecyclerView.setAdapter(shopAdapter);
    }

    private void setupListeners() {
        // Search functionality
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> searchShops(s.toString()), 300);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // See All click listeners
        seeAllCategories.setOnClickListener(v -> showAllCategories());
        seeAllShops.setOnClickListener(v -> showAllShops());
    }

    private void showAllCategories() {
        // Implement your logic to show all categories
        Toast.makeText(getContext(), "Showing all categories", Toast.LENGTH_SHORT).show();
    }

    private void showAllShops() {
        // Reset any filters and show all shops
        shopAdapter.updateList(shopList);
        updateEmptyState();
    }

    private void showPriceRangeDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Price Range")
                .setItems(new String[]{"Under ₹100", "₹100-₹200", "₹200-₹300", "₹300+"}, (dialog, which) -> {
                    // Handle price range selection
                    filterByPriceRange(which);
                })
                .show();
    }

    private void filterByPriceRange(int range) {
        List<Shop> filteredList = new ArrayList<>();
        for (Shop shop : shopList) {
            double price = shop.getAveragePrice();
            switch (range) {
                case 0: if (price < 100) filteredList.add(shop); break;
                case 1: if (price >= 100 && price < 200) filteredList.add(shop); break;
                case 2: if (price >= 200 && price < 300) filteredList.add(shop); break;
                case 3: if (price >= 300) filteredList.add(shop); break;
            }
        }
        shopAdapter.updateList(filteredList);
        updateEmptyState();
    }

    private void loadBanners() {
        bannerList.clear();
        bannerList.add(new Banner("1", "Banasthali Vidyapith\nSafe & Secure Platform"));
        bannerList.add(new Banner("2", "Fast Delivery\nAnywhere in Campus"));
        bannerList.add(new Banner("3", "Easy Ordering\nAnyone Can Place Orders"));

        // Update adapter and attach TabLayoutMediator after data is loaded
        if (bannerAdapter != null) {
            bannerAdapter.notifyDataSetChanged();
            new TabLayoutMediator(bannerIndicator, bannerViewPager, (tab, position) -> {}).attach();
        }
    }

    private void loadCategories() {
        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
            categoryList.clear();
            categoryList.add(new Category("all", "All", ""));

            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Category category = new Category(
                        document.getId(),
                        document.getString("name"),
                        document.getString("imageUrl")
                );
                categoryList.add(category);
            }
            categoryAdapter.notifyDataSetChanged();
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

    private void setupFilters() {
        filterList.clear();
        filterList.add(new Filter("price_range", "Price Range"));
        filterList.add(new Filter("price_low", "Price: Low to High"));
        filterList.add(new Filter("price_high", "Price: High to Low"));
        filterList.add(new Filter("rating", "Top Rated"));
        filterList.add(new Filter("newest", "Recently Added"));
        filterList.add(new Filter("name_az", "Name A-Z"));
        filterAdapter.notifyDataSetChanged();
    }

    private void filterShopsByCategory(String category) {
        if (category.equalsIgnoreCase("all")) {
            shopAdapter.updateList(shopList);
            updateEmptyState();
            return;
        }

        List<Shop> filteredList = new ArrayList<>();
        for (Shop shop : shopList) {
            if (shop.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(shop);
            }
        }
        shopAdapter.updateList(filteredList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        emptyState.setVisibility(shopAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void applyFilter(Filter filter) {
        switch (filter.getId()) {
            case "price_range":
                showPriceRangeDialog();
                break;
            case "price_low":
                Collections.sort(shopList, new Comparator<Shop>() {
                    @Override
                    public int compare(Shop s1, Shop s2) {
                        return Double.compare(s1.getAveragePrice(), s2.getAveragePrice());
                    }
                });
                break;
            case "price_high":
                Collections.sort(shopList, new Comparator<Shop>() {
                    @Override
                    public int compare(Shop s1, Shop s2) {
                        return Double.compare(s2.getAveragePrice(), s1.getAveragePrice());
                    }
                });
                break;
            case "rating":
                Collections.sort(shopList, new Comparator<Shop>() {
                    @Override
                    public int compare(Shop s1, Shop s2) {
                        return Double.compare(s2.getRating(), s1.getRating());
                    }
                });
                break;
            case "newest":
                Collections.sort(shopList, new Comparator<Shop>() {
                    @Override
                    public int compare(Shop s1, Shop s2) {
                        return Long.compare(
                                s2.getCreatedAt().getSeconds(),
                                s1.getCreatedAt().getSeconds());
                    }
                });
                break;
            case "name_az":
                Collections.sort(shopList, new Comparator<Shop>() {
                    @Override
                    public int compare(Shop s1, Shop s2) {
                        return s1.getName().compareToIgnoreCase(s2.getName());
                    }
                });
                break;
        }
        shopAdapter.notifyDataSetChanged();
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



    private void openShopDetails(Shop shop) {
        Intent intent = new Intent(getContext(), ShowProductActivity.class);
        intent.putExtra("shop_id", shop.getId());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}