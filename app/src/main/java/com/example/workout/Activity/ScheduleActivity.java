package com.example.myapplication.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.SchedulePagerAdapter;
import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.Set;
import com.example.myapplication.Domain.Workout;
import com.example.myapplication.Enum.MuscleEnum;
import com.example.myapplication.databinding.ActivityScheduleBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Date;

public class ScheduleActivity extends AppCompatActivity {
    private ActivityScheduleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SchedulePagerAdapter adapter = new SchedulePagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Today Workout"); break;
                        case 1: tab.setText("Calendar"); break;
                        case 2: tab.setText("My Plan"); break;
                    }
                }).attach();
    }

}