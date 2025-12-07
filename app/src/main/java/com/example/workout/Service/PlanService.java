package com.example.workout.Service;

import com.example.workout.ApiClient.ApiClient;
import com.example.workout.ApiClient.PlanApiService;
import com.example.workout.Domain.Plan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class PlanService {
    private final PlanApiService apiService;

    public interface ListPlanDataListener {
        void onPlansLoaded(List<Plan> plans);
        void onError(String message);
    }

    public interface PlanDataListener {
        void onPlanLoaded(Plan plan);
        void onError(String message);
    }

    public PlanService() {
        apiService = ApiClient.getClient().create(PlanApiService.class);
    }

    public void getPlansByUserId(int userId, ListPlanDataListener listener) {
        Call<List<Plan>> call = apiService.getByUserId(userId);
        call.enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                if (response.isSuccessful()) {
                    List<Plan> result = response.body() == null
                            ? new ArrayList<>()
                            : response.body();
                    listener.onPlansLoaded(result);
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createPlan(Plan plan, PlanDataListener listener) {
        Call<Plan> call = apiService.create(plan);
        call.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onPlanLoaded(response.body());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updatePlan(Plan plan, PlanDataListener listener) {
        Call<Plan> call = apiService.update(plan);
        call.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onPlanLoaded(response.body());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deletePlan(int id, ListPlanDataListener listener) {
        Call<Void> call = apiService.delete(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onPlansLoaded(new ArrayList<>());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }
}
