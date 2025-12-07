package com.example.workout.Service;

import com.example.workout.ApiClient.ApiClient;
import com.example.workout.ApiClient.SessionApiService;
import com.example.workout.Domain.Session;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class SessionService {
    private final SessionApiService apiService;

    public interface ListSessionDataListener {
        void onSessionLoaded(List<Session> sessions);
        void onError(String message);
    }

    public interface SessionDataListener {
        void onSessionLoaded(Session session);
        void onError(String message);
    }

    public SessionService() {
        apiService = ApiClient.getClient().create(SessionApiService.class);
    }

    public void getByPlanId(int planId, ListSessionDataListener listener) {
        Call<List<Session>> call = apiService.getByPlanId("planId", planId);
        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                if (response.isSuccessful()) {
                    List<Session> result = response.body() == null
                            ? new ArrayList<>()
                            : response.body();

                    listener.onSessionLoaded(result);
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createSession(Session session, SessionDataListener listener) {
        Call<Session> call = apiService.create(session);
        call.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSessionLoaded(response.body());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }
}
