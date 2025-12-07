package com.example.workout.ApiClient;

import com.example.workout.Domain.Set;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SetApiService {
    @GET("sets")
    Call<List<Set>> getBySessionId(@Query("sessionId") int sessionId);

    @POST("sets")
    Call<Set> create(@Body Set set);

    @PUT("sets")
    Call<Set> update(@Body Set set);

    @DELETE("sets/{id}")
    Call<Void> delete(@Path("id") int id);
}
