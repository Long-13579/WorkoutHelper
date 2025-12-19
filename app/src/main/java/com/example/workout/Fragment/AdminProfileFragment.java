package com.example.myapplication.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Activity.LoginActivity;
import com.example.myapplication.Domain.User;
import com.example.myapplication.R;
import com.example.myapplication.Service.UserService;

public class AdminProfileFragment extends Fragment {
    private EditText usernameEditText;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private Button saveButton;
    private Button logoutButton;
    private TextView errorTextView;
    private UserService userService;
    private String currentUserId;
    private User currentUser;
    private boolean isEditing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameEditText = view.findViewById(R.id.profileUsernameEditText);
        fullNameEditText = view.findViewById(R.id.profileFullNameEditText);
        emailEditText = view.findViewById(R.id.profileEmailEditText);
        phoneEditText = view.findViewById(R.id.profilePhoneEditText);
        passwordEditText = view.findViewById(R.id.profilePasswordEditText);
        saveButton = view.findViewById(R.id.saveProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        errorTextView = view.findViewById(R.id.profileErrorTextView);

        // Username is view-only
        usernameEditText.setEnabled(false);
        setEditingState(false);

        userService = new UserService();
        loadCurrentUserId();
        loadUserProfile();

        saveButton.setOnClickListener(v -> {
            if (isEditing) {
                saveProfile();
            } else {
                setEditingState(true);
            }
        });

        logoutButton.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadCurrentUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", null);
    }

    private void loadUserProfile() {
        if (currentUserId == null) {
            errorTextView.setText("User ID not found");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        userService.getUserById(currentUserId, new UserService.UserDataListener() {
            @Override
            public void onUsersLoaded(User user) {
                currentUser = user;
                displayUserData();
            }

            @Override
            public void onError(String message) {
                errorTextView.setText("Error loading profile: " + message);
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayUserData() {
        if (currentUser == null) return;

        usernameEditText.setText(currentUser.getUsername());
        fullNameEditText.setText(currentUser.getFullName());
        emailEditText.setText(currentUser.getEmail());
        phoneEditText.setText(currentUser.getPhoneNumber());
        // Password field is left empty for security
        setEditingState(false);
    }

    private void saveProfile() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty()) {
            errorTextView.setText("Please fill required fields");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (currentUser == null) {
            errorTextView.setText("User data not loaded");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        errorTextView.setVisibility(View.GONE);

        // Update user object
        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setPhoneNumber(phone);
        
        // Only update password if provided
        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        userService.update(currentUser, new UserService.UserDataListener() {
            @Override
            public void onUsersLoaded(User updatedUser) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                
                // Update SharedPreferences
                SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("full_name", updatedUser.getFullName());
                editor.apply();
                
                // Clear password field
                passwordEditText.setText("");
                setEditingState(false);
                currentUser = updatedUser;
            }

            @Override
            public void onError(String message) {
                errorTextView.setText("Error: " + message);
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setEditingState(boolean editing) {
        isEditing = editing;
        fullNameEditText.setEnabled(editing);
        emailEditText.setEnabled(editing);
        phoneEditText.setEnabled(editing);
        passwordEditText.setEnabled(editing);
        saveButton.setText(editing ? "Save Profile" : "Edit Profile");

        if (!editing) {
            passwordEditText.setText("");
        }
    }
}

