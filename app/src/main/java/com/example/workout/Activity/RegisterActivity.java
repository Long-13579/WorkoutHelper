package com.example.workout.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.workout.Domain.User;
import com.example.workout.R;
import com.example.workout.Service.UserService;
import com.example.workout.Utils.ValidationUtil;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private TextView errorTextView, loginRedirectText;
    private Button registerButton;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        errorTextView = findViewById(R.id.errorTextView);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        errorTextView.setTextColor(Color.RED);
        errorTextView.setVisibility(View.GONE);

        userService = new UserService();

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            String usernameError = ValidationUtil.validateUsername(username);
            String passwordError = ValidationUtil.validatePassword(password);

            if (usernameError != null) {
                showError(usernameError);
            } else if (passwordError != null) {
                showError(passwordError);
            } else if (confirmPassword.isEmpty()) {
                showError("Please confirm your password");
            } else if (!password.equals(confirmPassword)) {
                showError("Passwords do not match");
            } else {
                registerUser(username, password);
            }
        });

        setupLoginRedirectText();

        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser(String username, String password) {
        User newUser = new User("0", username, password, "New User", "", "", "", "male");
        userService.create(newUser, new UserService.UserDataListener() {
            @Override
            public void onUsersLoaded(User users) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                showError(message);
            }
        });
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void setupLoginRedirectText() {
        String fullText = "Already have an account? Login now";
        SpannableString spannableString = new SpannableString(fullText);

        ForegroundColorSpan whiteColor = new ForegroundColorSpan(getResources().getColor(R.color.white));
        spannableString.setSpan(whiteColor, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan whiteColorForQuestionMark = new ForegroundColorSpan(getResources().getColor(R.color.white));
        spannableString.setSpan(whiteColorForQuestionMark, 23, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan orangeColor = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        spannableString.setSpan(orangeColor, 25, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginRedirectText.setText(spannableString);
    }
}