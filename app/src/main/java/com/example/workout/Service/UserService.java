package com.example.workout.Service;

import com.example.workout.ApiClient.ApiClient;
import com.example.workout.ApiClient.UserApiService;
import com.example.workout.Domain.DTO.LoginDTO;
import com.example.workout.Domain.DTO.LoginResponse;
import com.example.workout.Domain.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private final UserApiService apiService;

    public interface UserDataListener {
        void onUsersLoaded(User users);
        void onError(String message);
    }

    public interface LoginDataListener {
        void onLoginSuccess(LoginResponse response);
        void onError(String message);
    }

    public UserService() {
        apiService = ApiClient.getClient().create(UserApiService.class);
    }

    public void login(String username, String password, LoginDataListener listener) {
        LoginDTO loginDTO = new LoginDTO(username, password);
        Call<LoginResponse> call = apiService.login(loginDTO);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onLoginSuccess(response.body());
                } else {
                    listener.onError("Incorrect username or password.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void create(User user, UserDataListener listener) {
        Call<User> call = apiService.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onUsersLoaded(response.body());
                }
                else if (response.code() == 409) {
                    listener.onError("Username already exists");
                }
                else {
                    listener.onError("Failed to create user.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void update(User user, UserDataListener listener) {
        Call<User> call = apiService.updateUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onUsersLoaded(response.body());
                } else {
                    listener.onError("Failed to update user.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getUserById(String id, UserDataListener listener) {
        Call<User> call = apiService.getUserById(id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onUsersLoaded(response.body());
                } else {
                    listener.onError("User not found.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

}