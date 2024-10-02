package com.example.springclient.fragment.login;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.springclient.MyApplication;
import com.example.springclient.model.Users;
import com.example.springclient.retrofit.ServerApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;
import com.example.springclient.retrofit.ServerApi;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<Map<String,String>> loginResult = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private ServerApi serverApi;
    private String accessToken;

    public LoginViewModel(Application application) {
        super(application);
        serverApi = MyApplication.getServerAPI();
    }

    public LiveData<Map<String, String>> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(String username, String password) {
        serverApi.login(username, password).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> responseBody = response.body();
                    String accessToken = (String) responseBody.get("accessToken");
                    String refreshToken = (String) responseBody.get("refreshToken");

                    SharedPreferences.Editor prefEditor = MyApplication.getSharedPreferences().edit();
                    prefEditor.putString("accessToken", accessToken);
                    prefEditor.putString("refreshToken", refreshToken);
                    prefEditor.apply();

                    String role = (String) responseBody.get("role");
                    Gson gson = new Gson();
                    String userJson = (String) responseBody.get("user");
                    Users user = gson.fromJson(userJson, Users.class);

                    MyApplication.setRolename(role);
                    MyApplication.setUser(user);
                    loginResult.setValue(responseBody);
                    errorMessage.setValue(null);
                } else if (response.code() == 403) {
                    loginResult.setValue(null);
                    errorMessage.setValue("User is not active");
                } else {
                    loginResult.setValue(null);
                    errorMessage.setValue("Login failed");
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                loginResult.setValue(null);
                errorMessage.setValue("Login failed");
            }
        });
    }


    public String getAccessToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }
}
