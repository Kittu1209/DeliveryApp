package com.example.fooddeliveryapp_student;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomePageVendor extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerlayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fregmentManager;
    Toolbar toolbar;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page_vendor);

        fab=findViewById(R.id.fab);
        toolbar=findViewById(R.id.toolbar_vendor);
        setSupportActionBar(toolbar);

        drawerlayout= findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerlayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerlayout.addDrawerListener(toogle);
        toogle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer_vendor);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView=findViewById(R.id.bottom_navigation_vendor);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId==R.id.bottom_dashboard_vendor){
                    openFragment(new Dashboard_Fragment_vendor());
                    return true;
                } else if (itemId==R.id.bottom_home_vendor) {
                    openFragment(new Home_Fragment_Vendor());
                    return true;
                }else if (itemId==R.id.orders_vendor) {
                    openFragment(new Orders_Fragment_Vendor());
                    return true;
                }else if (itemId==R.id.bottom_profile_vendor) {
                    openFragment(new Profile_Fragment_Vendor());
                    return true;
                }
                return false;
            }
        });

        fregmentManager = getSupportFragmentManager();
        openFragment(new Fragment_HomeStudent());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomePageVendor.this,"Home Page",Toast.LENGTH_SHORT).show();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.orders_vendor){
            openFragment(new Orders_Fragment_Vendor());
        }else if(itemId == R.id.products) {
            openFragment(new Product_Fragment_Vendor());
        }else if(itemId == R.id.settings_vendor) {
            openFragment(new Setting_Fragement_Vendor());
        }else if(itemId == R.id.faq_side) {
            openFragment(new FAQs_Fragment_Vendor());
        }else if(itemId == R.id.log_out_vendor) {
            openFragment(new Logout_Fragment_Vendor());
        }else if(itemId == R.id.contact_us_vendor) {
            openFragment(new Contactus_Fragment_Vendor());
        }
        drawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerlayout != null && drawerlayout.isDrawerOpen(GravityCompat.START)) {
            drawerlayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
