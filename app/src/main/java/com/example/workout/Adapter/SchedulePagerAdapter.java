package com.example.myapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.Domain.Workout;
import com.example.myapplication.Fragment.CalendarFragment;
import com.example.myapplication.Fragment.MyPlanFragment;
import com.example.myapplication.Fragment.TodayWorkoutFragment;

import java.util.ArrayList;

public class SchedulePagerAdapter extends FragmentStateAdapter {
    public SchedulePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new TodayWorkoutFragment();
            case 1: return new CalendarFragment();
            case 2: return new MyPlanFragment();
            default: return new TodayWorkoutFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}