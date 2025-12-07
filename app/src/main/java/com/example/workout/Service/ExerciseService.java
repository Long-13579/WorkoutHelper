package com.example.workout.Service;

import com.example.workout.ApiClient.ApiClient;
//import com.example.workout.ApiClient.BodyTrackApiService;
import com.example.workout.ApiClient.ExerciseApiService;
import com.example.workout.Domain.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseService {
    private final ExerciseApiService apiService;

    public interface ExerciseDataListener {
        void onExerciseLoaded(Exercise exercises);
        void onError(String message);
    }

    public interface ListExerciseDataListener {
        void onExerciseLoaded(List<Exercise> exercises);
        void onError(String message);
    }

    public ExerciseService() {
        apiService = ApiClient.getClient().create(ExerciseApiService.class);
    }

    public void GetAll(ListExerciseDataListener listener) {
        Call<List<Exercise>> call = apiService.GetAll();
        call.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful()) {
                    listener.onExerciseLoaded(response.body());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void GetById(int id, ExerciseDataListener listener) {
        Call<Exercise> call = apiService.GetById(id);
        call.enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onExerciseLoaded(response.body());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }
}
