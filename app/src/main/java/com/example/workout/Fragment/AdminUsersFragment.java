package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.AdminUserAdapter;
import com.example.myapplication.ApiClient.MockingDatabase;
import com.example.myapplication.Domain.User;
import com.example.myapplication.Domain.Plan;
import com.example.myapplication.R;
import com.example.myapplication.Service.PlanService;
import com.example.myapplication.Service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AdminUsersFragment extends Fragment {
    private RecyclerView usersRecyclerView;
    private Button addUserButton;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private AdminUserAdapter adapter;
    private List<User> userList;
    private UserService userService;
    private PlanService planService;
    private Set<String> deletedIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
        addUserButton = view.findViewById(R.id.addUserButton);
        progressBar = view.findViewById(R.id.usersProgressBar);
        emptyTextView = view.findViewById(R.id.usersEmptyTextView);

        userService = new UserService();
        planService = new PlanService();
        userList = new ArrayList<>();
        loadDeletedUsersFromPrefs();

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminUserAdapter(userList, new AdminUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                loadUserPlansAndShow(user);
            }

            @Override
            public void onEditClick(User user) {
                showUserDialog(user);
            }

            @Override
            public void onDeleteClick(User user) {
                showDeleteConfirmDialog(user);
            }
        });
        usersRecyclerView.setAdapter(adapter);

        addUserButton.setOnClickListener(v -> {
            android.util.Log.d("AdminUsersFragment", "Add User button clicked");
            if (getContext() != null) {
                showUserDialog(null);
            } else {
                android.util.Log.e("AdminUsersFragment", "Context is null, cannot show dialog");
                Toast.makeText(getContext(), "Cannot open dialog - context is null", Toast.LENGTH_SHORT).show();
            }
        });

        loadUsers();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        showLoading(true);
        emptyTextView.setVisibility(View.GONE);
        android.util.Log.d("AdminUsersFragment", "Loading users...");
        
        userService.getAllUsers(new UserService.ListUserDataListener() {
            @Override
            public void onUsersLoaded(List<User> users) {
                showLoading(false);
                android.util.Log.d("AdminUsersFragment", "Users loaded: " + (users != null ? users.size() : 0));
                
                if (users != null && !users.isEmpty()) {
                    reconcileDeletedWithServer(users);
                    userList.clear();
                    for (User u : users) {
                        if (!isLocallyDeleted(u)) {
                            userList.add(u);
                        }
                    }
                    adapter.updateList(userList);
                    android.util.Log.d("AdminUsersFragment", "Updated userList size: " + userList.size());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            usersRecyclerView.setVisibility(View.VISIBLE);
                            emptyTextView.setVisibility(View.GONE);
                        });
                    }
                } else {
                    android.util.Log.w("AdminUsersFragment", "Users list is null or empty");
                    userList.clear();
                    adapter.updateList(userList);
                    if (users == null || users.isEmpty()) {
                        android.util.Log.d("AdminUsersFragment", "No users found, showing empty state");
                    }
                }
                updateEmptyState();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                android.util.Log.e("AdminUsersFragment", "Error loading users: " + message);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading users: " + message, Toast.LENGTH_LONG).show();
                }
                userList.clear();
                adapter.updateList(userList);
                updateEmptyState();
                
                // Show error message in empty text view
                if (emptyTextView != null) {
                    emptyTextView.setText("Error: " + message + "\n\nPlease check your internet connection and try again.");
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (usersRecyclerView != null) {
            usersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        if (emptyTextView != null) {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    private void updateEmptyState() {
        if (emptyTextView != null && progressBar != null) {
            boolean isLoading = progressBar.getVisibility() == View.VISIBLE;
            emptyTextView.setVisibility(!isLoading && userList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void showUserDialog(User user) {
        if (getContext() == null) {
            android.util.Log.e("AdminUsersFragment", "Context is null, cannot show dialog");
            return;
        }
        
        android.util.Log.d("AdminUsersFragment", "Showing user dialog, isEdit: " + (user != null));
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_form, null);
        builder.setView(dialogView);

        EditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        EditText fullNameEditText = dialogView.findViewById(R.id.fullNameEditText);
        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);
        EditText dobEditText = dialogView.findViewById(R.id.dobEditText);
        Spinner genderSpinner = dialogView.findViewById(R.id.genderSpinner);
        Spinner roleSpinner = dialogView.findViewById(R.id.roleSpinner);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView errorTextView = dialogView.findViewById(R.id.errorTextView);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Setup gender spinner with visible text color on dark background
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item_white,
                new String[]{"Male", "Female"});
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_black);
        genderSpinner.setAdapter(genderAdapter);

        // Setup role spinner with visible text color on dark background
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item_white,
                new String[]{"USER", "ADMIN"});
        roleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_black);
        roleSpinner.setAdapter(roleAdapter);

        boolean isEdit = user != null;
        if (isEdit) {
            dialogTitle.setText("Edit User");
            usernameEditText.setText(user.getUsername() != null ? user.getUsername() : "");
            fullNameEditText.setText(user.getFullName() != null ? user.getFullName() : "");
            emailEditText.setText(user.getEmail() != null ? user.getEmail() : "");
            phoneEditText.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            dobEditText.setText(user.getDateOfBirth() != null ? user.getDateOfBirth() : "");
            
            // Set gender spinner
            if (user.getGender() != null && user.getGender().equals("Male")) {
                genderSpinner.setSelection(0);
            } else {
                genderSpinner.setSelection(1);
            }
            
            // Set role spinner
            String userRole = user.getRole() != null ? user.getRole() : "USER";
            roleSpinner.setSelection(userRole.equals("ADMIN") ? 1 : 0);
            
            usernameEditText.setEnabled(false); // Cannot change username when editing
            passwordEditText.setVisibility(View.GONE); // Hide password field when editing
        } else {
            dialogTitle.setText("Add User");
            passwordEditText.setVisibility(View.VISIBLE);
            // Clear all fields
            usernameEditText.setText("");
            passwordEditText.setText("");
            fullNameEditText.setText("");
            emailEditText.setText("");
            phoneEditText.setText("");
            dobEditText.setText("");
            genderSpinner.setSelection(0);
            roleSpinner.setSelection(0);
        }

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        saveButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getVisibility() == View.VISIBLE 
                ? passwordEditText.getText().toString().trim() 
                : "";
            String fullName = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String dob = dobEditText.getText().toString().trim();
            String gender = genderSpinner.getSelectedItem() != null 
                ? genderSpinner.getSelectedItem().toString() 
                : "Male";
            String role = roleSpinner.getSelectedItem() != null 
                ? roleSpinner.getSelectedItem().toString() 
                : "USER";

            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                errorTextView.setText("Please fill required fields (Username, Full Name, Email)");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            if (!isEdit && password.isEmpty()) {
                errorTextView.setText("Password is required for new users");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            if (!isEdit && isUsernameTaken(username)) {
                errorTextView.setText("Username already exists");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            errorTextView.setVisibility(View.GONE);

            User userToSave;
            if (isEdit) {
                userToSave = user; // Use existing user object
                // Update fields
                userToSave.setFullName(fullName);
                userToSave.setEmail(email);
                userToSave.setPhoneNumber(phone);
                userToSave.setDateOfBirth(dob);
                userToSave.setGender(gender);
                userToSave.setRole(role);
                // Only update password if provided (when editing, password field is hidden, so this won't happen)
            } else {
                userToSave = new User();
                userToSave.setId(generateUniqueUserId());
                userToSave.setUsername(username);
                userToSave.setPassword(password);
                userToSave.setFullName(fullName);
                userToSave.setEmail(email);
                userToSave.setPhoneNumber(phone);
                userToSave.setDateOfBirth(dob);
                userToSave.setGender(gender);
                userToSave.setRole(role);
            }

            if (isEdit) {
                userService.update(userToSave, new UserService.UserDataListener() {
                    @Override
                    public void onUsersLoaded(User updatedUser) {
                        Toast.makeText(getContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadUsers();
                    }

                    @Override
                    public void onError(String message) {
                        errorTextView.setText(message);
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                userService.create(userToSave, new UserService.UserDataListener() {
                    @Override
                    public void onUsersLoaded(User createdUser) {
                        clearDeletion(createdUser);
                        Toast.makeText(getContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadUsers();
                    }

                    @Override
                    public void onError(String message) {
                        errorTextView.setText(message);
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmDialog(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete user: " + user.getFullName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    userService.deleteUser(user.getId(), new UserService.DeleteUserListener() {
                        @Override
                        public void onDeleteSuccess() {
                            Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                            recordDeletion(user);
                            loadUsers();
                        }

                        @Override
                        public void onError(String message) {
                            // Nếu API lỗi, thử xóa local (kể cả mock seed)
                            if (removeMockUser(user) || removeFromCurrentList(user)) {
                                recordDeletion(user);
                                Toast.makeText(getContext(), "User removed locally", Toast.LENGTH_SHORT).show();
                                loadUsers();
                            } else {
                                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean removeMockUser(User target) {
        if (target == null) return false;
        List<User> mockUsers = MockingDatabase.getInstance().users;
        boolean removed = false;
        for (int i = mockUsers.size() - 1; i >= 0; i--) {
            User u = mockUsers.get(i);
            boolean sameId = u.getId() != null && u.getId().equals(target.getId());
            boolean sameUsername = u.getUsername() != null && target.getUsername() != null
                    && u.getUsername().equalsIgnoreCase(target.getUsername());
            if (sameId || sameUsername) {
                mockUsers.remove(i);
                removed = true;
            }
        }
        return removed;
    }

    private boolean removeFromCurrentList(User target) {
        if (target == null) return false;
        boolean removed = false;
        for (int i = userList.size() - 1; i >= 0; i--) {
            User u = userList.get(i);
            boolean sameId = u.getId() != null && u.getId().equals(target.getId());
            boolean sameUsername = u.getUsername() != null && target.getUsername() != null
                    && u.getUsername().equalsIgnoreCase(target.getUsername());
            if (sameId || sameUsername) {
                userList.remove(i);
                removed = true;
            }
        }
        if (removed && adapter != null) {
            adapter.updateList(new ArrayList<>(userList));
        }
        return removed;
    }

    private String generateUniqueUserId() {
        List<User> mockUsers = MockingDatabase.getInstance().users;
        while (true) {
            String id = UUID.randomUUID().toString();
            boolean collision = false;
            for (User u : mockUsers) {
                if (id.equals(u.getId())) {
                    collision = true;
                    break;
                }
            }
            for (User u : userList) {
                if (id.equals(u.getId())) {
                    collision = true;
                    break;
                }
            }
            if (!collision) return id;
        }
    }

    private boolean isBaseMockUser(User target) {
        if (target == null) return false;
        List<User> mockUsers = MockingDatabase.getInstance().users;
        for (User u : mockUsers) {
            boolean sameId = u.getId() != null && u.getId().equals(target.getId());
            boolean sameUsername = u.getUsername() != null && target.getUsername() != null
                    && u.getUsername().equalsIgnoreCase(target.getUsername());
            if (sameId || sameUsername) return true;
        }
        return false;
    }

    private boolean isUsernameTaken(String username) {
        if (username == null || username.isEmpty()) return false;
        // Check current list
        for (User u : userList) {
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        // Check mock seed
        for (User u : MockingDatabase.getInstance().users) {
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLocallyDeleted(User u) {
        if (u == null) return false;
        return u.getId() != null && deletedIds.contains(u.getId());
    }

    private void recordDeletion(User u) {
        if (u == null) return;
        if (u.getId() != null) deletedIds.add(u.getId());
        saveDeletedUsersToPrefs();
    }

    private void clearDeletion(User u) {
        if (u == null) return;
        boolean changed = false;
        if (u.getId() != null && deletedIds.contains(u.getId())) {
            deletedIds.remove(u.getId());
            changed = true;
        }
        if (changed) {
            saveDeletedUsersToPrefs();
        }
    }

    private void reconcileDeletedWithServer(List<User> serverUsers) {
        if (serverUsers == null || serverUsers.isEmpty()) return;
        boolean changed = false;
        for (User u : serverUsers) {
            if (u.getId() != null && deletedIds.contains(u.getId())) {
                deletedIds.remove(u.getId());
                changed = true;
            }
        }
        if (changed) {
            saveDeletedUsersToPrefs();
        }
    }

    private void loadUserPlansAndShow(User user) {
        if (user == null) return;
        int userIdInt;
        try {
            userIdInt = Integer.parseInt(user.getId());
        } catch (Exception e) {
            showUserDetails(user, new ArrayList<>(), "Cannot parse user id to load plans");
            return;
        }

        planService.getPlansByUserId(userIdInt, new PlanService.ListPlanDataListener() {
            @Override
            public void onPlansLoaded(List<Plan> plans) {
                showUserDetails(user, plans, null);
            }

            @Override
            public void onError(String message) {
                showUserDetails(user, new ArrayList<>(), message);
            }
        });
    }

    private void showUserDetails(User user, List<Plan> plans, String planError) {
        if (getContext() == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Username: ").append(nullSafe(user.getUsername())).append("\n");
        sb.append("Full name: ").append(nullSafe(user.getFullName())).append("\n");
        sb.append("Email: ").append(nullSafe(user.getEmail())).append("\n");
        sb.append("Phone: ").append(nullSafe(user.getPhoneNumber())).append("\n");
        sb.append("Gender: ").append(nullSafe(user.getGender())).append("\n");
        sb.append("Date of birth: ").append(nullSafe(user.getDateOfBirth())).append("\n");
        sb.append("Role: ").append(nullSafe(user.getRole())).append("\n\n");

        if (planError != null) {
            sb.append("Plans: failed to load (").append(planError).append(")\n");
        } else if (plans == null || plans.isEmpty()) {
            sb.append("Plans: (none)\n");
        } else {
            sb.append("Plans:\n");
            for (Plan p : plans) {
                sb.append("• ").append(p.getName()).append("\n");
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("User Details")
                .setMessage(sb.toString())
                .setPositiveButton("Close", null)
                .show();
    }

    private String nullSafe(String v) {
        return v != null ? v : "";
    }

    private void loadDeletedUsersFromPrefs() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE);
        deletedIds = prefs.getStringSet("deleted_ids", new HashSet<>());
        if (deletedIds == null) deletedIds = new HashSet<>();
    }

    private void saveDeletedUsersToPrefs() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("deleted_ids", new HashSet<>(deletedIds));
        editor.apply();
    }
}

