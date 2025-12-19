package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.AdminPagerAdapter;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityAdminBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup ViewPager
        AdminPagerAdapter adapter = new AdminPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Setup TabLayout
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Dashboard");
                            break;
                        case 1:
                            tab.setText("Users");
                            break;
                        case 2:
                            tab.setText("Exercises");
                            break;
                        case 3:
                            tab.setText("Profile");
                            break;
                    }
                }).attach();

        // Back button - Logout
        binding.backBtn.setOnClickListener(v -> {
            // Clear shared preferences and logout
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Method to switch tabs programmatically
    public void switchToTab(int position) {
        binding.viewPager.setCurrentItem(position, true);
    }
}

