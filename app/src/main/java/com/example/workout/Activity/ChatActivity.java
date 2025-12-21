package com.example.myapplication.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.ChatHistoryAdapter;
import com.example.myapplication.Domain.ChatHistory;
import com.example.myapplication.Domain.DTO.ChatResponse;
import com.example.myapplication.R;
import com.example.myapplication.Service.ChatService;
import com.example.myapplication.Utils.ChatHistoryManager;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private EditText userNeedEditText;
    private EditText additionalInfoEditText;
    private TextView responseTextView;
    private TextView errorTextView;
    private TextView historyToggleButton;
    private Button sendButton;
    private ProgressBar progressBar;
    private ChatService chatService;
    private RecyclerView historyRecyclerView;
    private LinearLayout historySection;
    private View scrollView;
    private ChatHistoryAdapter historyAdapter;
    private boolean isHistoryMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupClickListeners();
        chatService = new ChatService();
    }

    private void initViews() {
        userNeedEditText = findViewById(R.id.userNeedEditText);
        additionalInfoEditText = findViewById(R.id.additionalInfoEditText);
        responseTextView = findViewById(R.id.responseTextView);
        errorTextView = findViewById(R.id.errorTextView);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        historyToggleButton = findViewById(R.id.historyToggleButton);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historySection = findViewById(R.id.historySection);
        scrollView = findViewById(R.id.scrollView);

        View backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        // Setup history RecyclerView
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<ChatHistory> historyList = ChatHistoryManager.loadChatHistory(this);
        historyAdapter = new ChatHistoryAdapter(historyList, this::onHistoryItemClick);
        historyRecyclerView.setAdapter(historyAdapter);

        // Setup history toggle
        historyToggleButton.setOnClickListener(v -> toggleHistoryView());
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> {
            String userNeed = userNeedEditText.getText().toString().trim();
            String additionalInfo = additionalInfoEditText.getText().toString().trim();

            if (userNeed.isEmpty()) {
                showError("Please enter your exercise needs");
                return;
            }

            requestAdvice(userNeed, additionalInfo);
        });
    }

    private void requestAdvice(String userNeed, String additionalInfo) {
        showLoading(true);
        hideError();

        chatService.getExerciseAdvice(userNeed, additionalInfo, new ChatService.ChatDataListener() {
            @Override
            public void onSuccess(ChatResponse response) {
                showLoading(false);
                String adviceText = null;
                if (response.getAdvice() != null && !response.getAdvice().isEmpty()) {
                    adviceText = response.getAdvice();
                } else if (response.getExercises() != null && !response.getExercises().isEmpty()) {
                    adviceText = response.getExercises();
                } else {
                    adviceText = "No response received from AI";
                }
                
                responseTextView.setText(adviceText);
                
                // Save to history
                ChatHistory chatHistory = new ChatHistory(userNeed, additionalInfo, adviceText);
                ChatHistoryManager.saveChatHistory(ChatActivity.this, chatHistory);
                
                // Refresh history adapter
                refreshHistoryList();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                showError(message);
                Toast.makeText(ChatActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        sendButton.setEnabled(!show);
        sendButton.setText(show ? "Processing..." : "Send Request");
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorTextView.setVisibility(View.GONE);
    }

    private void toggleHistoryView() {
        isHistoryMode = !isHistoryMode;
        
        if (isHistoryMode) {
            // Show history, hide chat
            scrollView.setVisibility(View.GONE);
            historySection.setVisibility(View.VISIBLE);
            historyToggleButton.setText("New Chat");
            refreshHistoryList();
        } else {
            // Show chat, hide history
            scrollView.setVisibility(View.VISIBLE);
            historySection.setVisibility(View.GONE);
            historyToggleButton.setText("History");
        }
    }

    private void refreshHistoryList() {
        List<ChatHistory> historyList = ChatHistoryManager.loadChatHistory(this);
        historyAdapter.updateList(historyList);
    }

    private void onHistoryItemClick(ChatHistory history) {
        // Switch back to chat mode
        isHistoryMode = false;
        scrollView.setVisibility(View.VISIBLE);
        historySection.setVisibility(View.GONE);
        historyToggleButton.setText("History");
        
        // Display the selected history
        userNeedEditText.setText(history.getUserNeed());
        if (history.getAdditionalInfo() != null && !history.getAdditionalInfo().isEmpty()) {
            additionalInfoEditText.setText(history.getAdditionalInfo());
        }
        responseTextView.setText(history.getResponse());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHistoryList();
    }
}

