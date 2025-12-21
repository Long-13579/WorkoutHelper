package com.example.myapplication.ApiClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatApiClient {
    // Update this URL with your Spring Boot backend URL
    // If running locally on emulator: "http://10.0.2.2:8080/api/"
    // If running locally on real device: "http://YOUR_LOCAL_IP:8080/api/"
    // If deployed: "https://your-backend-url.com/api/"
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}

