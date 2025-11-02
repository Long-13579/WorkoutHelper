package com.example.workout.ApiClient;

import com.example.workout.Domain.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ExerciseApiService {
    @GET("exercises")
    Call<List<Exercise>> GetAll();

    @GET("exercises")
    Call<Exercise> GetById(int id);
}

