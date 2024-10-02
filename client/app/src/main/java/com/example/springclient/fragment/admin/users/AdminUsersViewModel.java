package com.example.springclient.fragment.admin.users;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.springclient.MyApplication;
import com.example.springclient.model.AuthData;
import com.example.springclient.model.Users;
import com.example.springclient.model.Roles;
import com.example.springclient.retrofit.ServerApi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsersViewModel extends ViewModel {
    private static final String TAG = "UsersFragment";
    private MutableLiveData<List<Users>> usersList = new MutableLiveData<>();
    private MutableLiveData<List<Roles>> rolesList = new MutableLiveData<>();

    private MutableLiveData<List<Roles>> userRolesList = new MutableLiveData<>();
    private MutableLiveData<String> webSocketMessage = new MutableLiveData<>();


    private ServerApi serverApi;

    public AdminUsersViewModel() {
        serverApi = MyApplication.getServerAPI();
        loadUsers();
        loadRoles();
    }

    public LiveData<List<Users>> getUsersList() {
        return usersList;
    }

    public LiveData<List<Roles>> getRolesList() {
        return rolesList;
    }
    public LiveData<List<Roles>> getUserRolesList() {
        return userRolesList;
    }

    public LiveData<String> getWebSocketMessage() {
        return webSocketMessage;
    }

    public void setWebSocketMessage(String message) {
        webSocketMessage.setValue(message);
    }

    public void loadUsers() {
        serverApi.getAllUsers().enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usersList.setValue(response.body());
                    Log.d(TAG, "Users loaded successfully" + "List size: " + getUsersList().getValue().size());
                } else {
                    Log.e(TAG, "Failed to load users: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading users: " + t.getMessage(), t);
            }
        });
    }

    public void loadRolesByUserId(int userId){
        serverApi.getRolesByUserId(userId).enqueue(new Callback<List<Roles>>() {
            @Override
            public void onResponse(Call<List<Roles>> call, Response<List<Roles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userRolesList.setValue(response.body());
                    Log.d(TAG, "Roles user loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load roles: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Roles>> call, Throwable t) {

            }
        });
    }


    public void loadRoles() {
        serverApi.getAllRoles().enqueue(new Callback<List<Roles>>() {
            @Override
            public void onResponse(Call<List<Roles>> call, Response<List<Roles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rolesList.setValue(response.body());
                    Log.d(TAG, "Roles loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load roles: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Roles>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading roles: " + t.getMessage(), t);
            }
        });
    }

    public LiveData<Response<Users>> saveUser(RequestBody body) {
        MutableLiveData<Response<Users>> result = new MutableLiveData<>();
        serverApi.userSave(body).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                result.setValue(response);
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<Response<AuthData>> saveUserRole(RequestBody body) {
        MutableLiveData<Response<AuthData>> resilt = new MutableLiveData<>();
        serverApi.addUserRole(body).enqueue(new Callback<AuthData>() {
            @Override
            public void onResponse(Call<AuthData> call, Response<AuthData> response) {
                if (response.isSuccessful()) {
                    resilt.setValue(response);
                } else {
                    resilt.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<AuthData> call, Throwable t) {
                resilt.setValue(null);
            }
        });
        return resilt;
    }

    public void deleteUserRole(int userId, int roleId) {
        serverApi.deleteUserRole(userId, roleId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Roles> currentRolesList = userRolesList.getValue();
                    if (currentRolesList != null) {
                        for (int i = 0; i < currentRolesList.size(); i++) {
                            if (currentRolesList.get(i).getId() == roleId) {
                                currentRolesList.remove(i);
                                userRolesList.setValue(currentRolesList);
                                Log.i("DeleteRole", "Role deleted successfully");
                                break;
                            }
                        }
                    }
                } else {
                    Log.e("DeleteRole", "Failed to delete role: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DeleteRole", "Error deleting role", t);
            }
        });
    }

    public void deleteUser(int userId) {
        serverApi.userDelete(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Users> currentList = usersList.getValue();
                    if (currentList != null) {
                        for (int i = 0; i < currentList.size(); i++) {
                            if (currentList.get(i).getId() == userId) {
                                currentList.remove(i);
                                usersList.setValue(currentList);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }



    public LiveData<Response<Users>> updateUser(RequestBody body) {
        MutableLiveData<Response<Users>> result = new MutableLiveData<>();
        serverApi.userUpdate(body).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    result.setValue(response);
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }


}