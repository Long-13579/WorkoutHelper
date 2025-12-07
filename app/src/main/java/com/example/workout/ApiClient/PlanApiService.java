package com.example.workout.ApiClient;

import com.example.workout.Domain.Plan;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface PlanApiService {
    @GET("plans")
    Call<List<Plan>> getByUserId(
            @Query("userId") int userId
    );

    @POST("plans")
    Call<Plan> create(@Body Plan plan);

    @PUT("plans")
    Call<Plan> update(@Body Plan plan);

    @DELETE("plans/{id}")
    Call<Void> delete(@Path("id") int id);
}
