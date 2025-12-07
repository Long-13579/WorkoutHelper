package com.example.workout.ApiClient;

import com.example.workout.Domain.Session;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SessionApiService {
    @GET("sessions")
    Call<List<Session>> getByPlanId(
            @Query("type") String type,
            @Query("planId") int planId
    );

    @POST("sessions")
    Call<Session> create(@Body Session session);

    @DELETE("sessions/{id}")
    Call<Void> delete(@Path("id") int id);
}
