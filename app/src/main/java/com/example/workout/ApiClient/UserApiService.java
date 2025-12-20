package com.example.myapplication.ApiClient;

import com.example.myapplication.Domain.DTO.LoginDTO;
import com.example.myapplication.Domain.DTO.LoginResponse;
import com.example.myapplication.Domain.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {
    @GET("users/{id}")
    Call<User> getUserById(
            @Path("id") String id
    );

    @GET("users")
    Call<List<User>> getAllUsers(@Query(value = "all", encoded = true) String all);

    @POST("users")
    Call<User> createUser(@Body User user);

    @PUT("users")
    Call<User> updateUser(@Body User user);

    @retrofit2.http.DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String id);

    @POST("users/login")
    Call<LoginResponse> login(@Body LoginDTO loginDTO);
}