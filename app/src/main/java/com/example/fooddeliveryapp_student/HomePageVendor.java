package com.example.fooddeliveryapp_student;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomePageVendor extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page_vendor);

        // Initialize Views
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar_vendor);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation_vendor);
        bottomNavigationView.setBackground(null);

        // Setup Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup Navigation Drawer Clicks
        NavigationView navigationView = findViewById(R.id.navigation_drawer_vendor);
        navigationView.setNavigationItemSelectedListener(this);

        // Load Home_Fragment_Vendor by default
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            openFragment(new Home_Fragment_Vendor());
        }

        // Handle Bottom Navigation Clicks
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.bottom_dashboard_vendor) {
                    openFragment(new Dashboard_Fragment_vendor());
                    return true;
                } else if (itemId == R.id.bottom_home_vendor) {
                    openFragment(new Home_Fragment_Vendor());
                    return true;
                } else if (itemId == R.id.bottom_orders_vendor) {
                    openFragment(new Orders_Fragment_Vendor());
                    return true;
                } else if (itemId == R.id.bottom_profile_vendor) {
                    openFragment(new Profile_Fragment_Vendor());
                    return true;
                } else if (itemId == R.id.bottom_addition_vendor) {
                    openFragment(new Product_Fragment_Vendor());
                    return true;
                }
                return false;
            }
        });

        // Floating Button Click (Example: Add New Item)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(new Product_Fragment_Vendor());
                Toast.makeText(HomePageVendor.this, "Add new item", Toast.LENGTH_SHORT).show();
            }
        });

        // Window insets fix for layout padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.orders_vendor) {
            openFragment(new Orders_Fragment_Vendor());
        } else if (itemId == R.id.products) {
            openFragment(new Product_Fragment_Vendor());
        } else if (itemId == R.id.settings_vendor) {
            openFragment(new Setting_Fragement_Vendor());
        } else if (itemId == R.id.faq_side) {
            openFragment(new FAQs_Fragment_Vendor());
        } else if (itemId == R.id.log_out_vendor) {
            openFragment(new Logout_Fragment_Vendor());
        } else if (itemId == R.id.contact_us_vendor) {
            openFragment(new Contactus_Fragment_Vendor());
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_vendor, fragment);
        transaction.commit();
    }
}
