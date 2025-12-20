package com.example.myapplication.ApiClient;

import com.example.myapplication.Domain.DTO.ChatRequest;
import com.example.myapplication.Domain.DTO.ChatResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatApiService {
    @POST("chat/advice")
    Call<ChatResponse> getExerciseAdvice(@Body ChatRequest request);
}

