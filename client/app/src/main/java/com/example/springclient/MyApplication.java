package com.example.springclient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.springclient.model.Users;
import com.example.springclient.retrofit.ServerApi;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {
    private static SharedPreferences sharedPreferences;
    private static MyApplication context;
    private static ServerApi serverApi;
    private static Users user = null;
    private static String rolename = null;

    @Override
    public void onCreate() {
        super.onCreate();
        createServerAPI();
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        context = this;
    }

    public void createServerAPI() {
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();
            String accessToken = sharedPreferences.getString("accessToken", null);
            if (accessToken != null) {
                builder.addHeader("Authorization", "Bearer " + accessToken);
            }
            Request newRequest = builder.build();
            okhttp3.Response response = chain.proceed(newRequest);
            if (response.code() == 403) {
                String refreshToken = sharedPreferences.getString("refreshToken", null);
                if (refreshToken != null) {
                    Log.d("TOKEN", "Ответ сервера " + refreshToken);
                    retrofit2.Response<Map<String, String>> tokenResponse = refreshTokens(refreshToken);
                    if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
                        String newAccessToken = tokenResponse.body().get("accessToken");
                        String newRefreshToken = tokenResponse.body().get("refreshToken");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("accessToken", newAccessToken);
                        editor.putString("refreshToken", newRefreshToken);
                        editor.apply();
                        builder.header("Authorization", "Bearer " + newAccessToken);
                        newRequest = builder.build();
                        return chain.proceed(newRequest);
                    }
                }
            }
            return response;
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl("http://5.130.148.107:8080")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setPrettyPrinting().create()))
                .build();

        serverApi = retrofit.create(ServerApi.class);
    }

    private retrofit2.Response<Map<String, String>> refreshTokens(String refreshToken) throws IOException {
        Call<Map<String, String>> call = serverApi.refreshToken(refreshToken);
        return call.execute();
    }
    public static ServerApi getServerAPI() {
        return serverApi;
    }

    public static String getRolename() {
        return rolename;
    }
    public static void setRolename(String rolename) {
        MyApplication.rolename = rolename;
    }

    public static Users getUser() {
        return user;
    }

    public static void setUser(Users user) {
        MyApplication.user = user;
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static Context getContext() {
        return context;
    }

}
