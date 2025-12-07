package com.example.workout.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.workout.Domain.DTO.LoginResponse;
import com.example.workout.R;
import com.example.workout.Service.UserService;
import com.example.workout.Utils.ValidationUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextView errorTextView, registerTextView;
    private Button loginButton;

    private final UserService userService = new UserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
        errorTextView = findViewById(R.id.errorTextView);
        errorTextView.setTextColor(Color.RED);
        errorTextView.setVisibility(View.GONE);

        setupRegisterSpan();

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            String usernameError = ValidationUtil.validateUsername(username);
            String passwordError = ValidationUtil.validatePassword(password);

            if (usernameError != null) {
                showError(usernameError);
                return;
            }

            if (passwordError != null) {
                showError(passwordError);
                return;
            }

            login(username, password);
        });
    }

    private void login(String username, String password) {
        userService.login(username, password, new UserService.LoginDataListener() {
            @Override
            public void onLoginSuccess(LoginResponse response) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_id", response.getUserId());
                editor.putString("full_name", response.getFullName());
                editor.apply();

                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(intent);
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

    private void setupRegisterSpan() {
        String fullText = "Don't have an account? Register now";
        SpannableString spannable = new SpannableString(fullText);

        ForegroundColorSpan whiteColor = new ForegroundColorSpan(getResources().getColor(R.color.white));
        spannable.setSpan(whiteColor, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ForegroundColorSpan orangeColor = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        spannable.setSpan(clickableSpan, 24, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(orangeColor, 24, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerTextView.setText(spannable);
        registerTextView.setMovementMethod(LinkMovementMethod.getInstance());
        registerTextView.setHighlightColor(Color.TRANSPARENT);
    }
}