package com.example.myapplication.Service;

import android.util.Log;

import com.example.myapplication.ApiClient.ChatApiClient;
import com.example.myapplication.ApiClient.ChatApiService;
import com.example.myapplication.Domain.DTO.ChatRequest;
import com.example.myapplication.Domain.DTO.ChatResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatService {
    private static final String TAG = "ChatService";
    private final ChatApiService chatApiService;

    public interface ChatDataListener {
        void onSuccess(ChatResponse response);
        void onError(String message);
    }

    public ChatService() {
        chatApiService = ChatApiClient.getClient().create(ChatApiService.class);
    }

    public void getExerciseAdvice(String userNeed, String additionalInfo, ChatDataListener listener) {
        ChatRequest request = new ChatRequest(userNeed, additionalInfo);

        Call<ChatResponse> call = chatApiService.getExerciseAdvice(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();
                    listener.onSuccess(chatResponse);
                } else {
                    String errorMessage = "Unable to receive response from server";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    listener.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                listener.onError("Connection error: " + t.getMessage());
            }
        });
    }
}

