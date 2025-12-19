package com.example.myapplication.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Domain.Exercise;
import com.example.myapplication.Domain.User;
import com.example.myapplication.R;
import com.example.myapplication.Service.ExerciseService;
import com.example.myapplication.Service.UserService;

import java.util.List;

public class AdminDashboardFragment extends Fragment {
    private TextView totalUsersTextView;
    private TextView totalExercisesTextView;
    private TextView totalAdminUsersTextView;
    private TextView totalRegularUsersTextView;
    private ProgressBar progressBar;
    
    private UserService userService;
    private ExerciseService exerciseService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalUsersTextView = view.findViewById(R.id.totalUsersTextView);
        totalExercisesTextView = view.findViewById(R.id.totalExercisesTextView);
        totalAdminUsersTextView = view.findViewById(R.id.totalAdminUsersTextView);
        totalRegularUsersTextView = view.findViewById(R.id.totalRegularUsersTextView);
        progressBar = view.findViewById(R.id.dashboardProgressBar);

        userService = new UserService();
        exerciseService = new ExerciseService();

        // Setup click listeners for function cards
        // Dashboard card - stay on current tab (refresh data)
        view.findViewById(R.id.dashboardFunctionCard).setOnClickListener(v -> {
            // Refresh dashboard data
            loadDashboardData();
        });

        // Users Management card - switch to Users tab (index 1)
        view.findViewById(R.id.usersFunctionCard).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.myapplication.Activity.AdminActivity) {
                ((com.example.myapplication.Activity.AdminActivity) getActivity()).switchToTab(1);
            }
        });

        // Exercises Management card - switch to Exercises tab (index 2)
        view.findViewById(R.id.exercisesFunctionCard).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.myapplication.Activity.AdminActivity) {
                ((com.example.myapplication.Activity.AdminActivity) getActivity()).switchToTab(2);
            }
        });

        // Profile Management card - switch to Profile tab (index 3)
        view.findViewById(R.id.profileFunctionCard).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.myapplication.Activity.AdminActivity) {
                ((com.example.myapplication.Activity.AdminActivity) getActivity()).switchToTab(3);
            }
        });

        loadDashboardData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        showLoading(true);
        
        // Load users
        userService.getAllUsers(new UserService.ListUserDataListener() {
            @Override
            public void onUsersLoaded(List<User> users) {
                int totalUsers = users.size();
                int adminCount = 0;
                int regularCount = 0;

                for (User user : users) {
                    if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                        adminCount++;
                    } else {
                        regularCount++;
                    }
                }

                totalUsersTextView.setText(String.valueOf(totalUsers));
                totalAdminUsersTextView.setText(String.valueOf(adminCount));
                totalRegularUsersTextView.setText(String.valueOf(regularCount));

                // Load exercises
                loadExercises();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(getContext(), "Error loading users: " + message, Toast.LENGTH_SHORT).show();
                // Still try to load exercises
                loadExercises();
            }
        });
    }

    private void loadExercises() {
        exerciseService.GetAll(new ExerciseService.ListExerciseDataListener() {
            @Override
            public void onExerciseLoaded(List<Exercise> exercises) {
                showLoading(false);
                totalExercisesTextView.setText(String.valueOf(exercises.size()));
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(getContext(), "Error loading exercises: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}

