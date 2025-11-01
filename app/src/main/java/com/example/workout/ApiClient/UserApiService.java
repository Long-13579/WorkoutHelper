package com.example.workout.ApiClient;

import com.example.workout.Domain.DTO.LoginDTO;
import com.example.workout.Domain.DTO.LoginResponse;
import com.example.workout.Domain.User;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApiService {
    @GET("users")
    Call<User> getUserById(
            @Query("id") String id
    );

    @POST("users")
    Call<User> createUser(@Body User user);

    @PUT("users")
    Call<User> updateUser(@Body User user);

    @POST("users/login")
    Call<LoginResponse> login(@Body LoginDTO loginDTO);
}