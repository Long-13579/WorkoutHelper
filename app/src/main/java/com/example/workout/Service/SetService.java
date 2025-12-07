package com.example.workout.Service;

import com.example.workout.ApiClient.ApiClient;
import com.example.workout.ApiClient.SetApiService;
import com.example.workout.Domain.Set;
import com.example.workout.Utils.SetValidation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class SetService {
    private final SetApiService apiService;

    public interface ListSetDataListener {
        void onSetsLoaded(List<Set> sets);
        void onError(String message);
    }

    public interface SetDataListener {
        void onSetLoaded(Set set);
        void onError(String message);
    }

    public SetService() {
        apiService = ApiClient.getClient().create(SetApiService.class);
    }

    public void getBySessionId(int sessionId, ListSetDataListener listener) {
        Call<List<Set>> call = apiService.getBySessionId(sessionId);
        call.enqueue(new Callback<List<Set>>() {
            @Override
            public void onResponse(Call<List<Set>> call, Response<List<Set>> response) {
                if (response.isSuccessful()) {
                    List<Set> result = response.body() == null
                            ? new ArrayList<>()
                            : response.body();

                    listener.onSetsLoaded(result);
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Set>> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createSet(Set set, SetDataListener listener) {
        String validateMessage = SetValidation.Validate(set);
        if (validateMessage != null) {
            listener.onError(validateMessage);
            return;
        }

        Call<Set> call = apiService.create(set);
        call.enqueue(new Callback<Set>() {
            @Override
            public void onResponse(Call<Set> call, Response<Set> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSetLoaded(response.body());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Set> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateSet(Set set, SetDataListener listener) {
        String validateMessage = SetValidation.Validate(set);
        if (validateMessage != null) {
            listener.onError(validateMessage);
            return;
        }

        Call<Set> call = apiService.update(set);
        call.enqueue(new Callback<Set>() {
            @Override
            public void onResponse(Call<Set> call, Response<Set> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSetLoaded(response.body());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Set> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteSet(int id, ListSetDataListener listener) {
        Call<Void> call = apiService.delete(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSetsLoaded(new ArrayList<>());
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
