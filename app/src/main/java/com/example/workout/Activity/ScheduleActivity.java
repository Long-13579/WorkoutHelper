package com.example.myapplication.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.SchedulePagerAdapter;
import com.example.myapplication.databinding.ActivityScheduleBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class ScheduleActivity extends AppCompatActivity {

    private ActivityScheduleBinding binding;

    private static final String[] TAB_TITLES = {
            "Today Workout",
            "Calendar",
            "My Plan"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewPager();
        setupTabLayout();
    }

    private void setupViewPager() {
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(
                binding.tabLayout,
                binding.viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();
    }
}