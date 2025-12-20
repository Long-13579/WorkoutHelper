package com.example.workout.ApiClient;

import com.example.workout.Domain.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExerciseApiService {
    @GET("exercises")
    Call<List<Exercise>> GetAll();

    @GET("exercises")
    Call<Exercise> GetById(@Query("id") int id);

    @POST("exercises")
    Call<Exercise> createExercise(@Body Exercise exercise);

    @PUT("exercises")
    Call<Exercise> updateExercise(@Body Exercise exercise);

    @retrofit2.http.DELETE("exercises/{id}")
    Call<Void> deleteExercise(@Path("id") int id);
}
