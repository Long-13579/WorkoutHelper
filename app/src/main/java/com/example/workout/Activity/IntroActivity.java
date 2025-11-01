package com.example.workout.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.workout.R;
import com.example.workout.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bỏ giới hạn layout
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Nút "Sign In"
        binding.startBtn.setOnClickListener(v -> {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        });

        // Tạo đoạn văn bản "Don't have an account? Register now"
        String text = "Don't have an account? Register now";
        SpannableString spannableString = new SpannableString(text);

        // Click để chuyển sang trang đăng ký
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(IntroActivity.this, RegisterActivity.class));
            }
        };

        // Tô màu cho chữ "Register now"
        int start = text.indexOf("Register now");
        int end = start + "Register now".length();
        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.orange)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Gán text vào TextView
        binding.registerText.setText(spannableString);
        binding.registerText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}