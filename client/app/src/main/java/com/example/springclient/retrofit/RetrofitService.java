/*
package com.example.springclient.retrofit;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit retrofit;
    private AuthInterceptor authInterceptor;

    public RetrofitService(String accessToken){
        authInterceptor = new AuthInterceptor(accessToken);
        initializeRetrofit();
    }

    public void initializeRetrofit(){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://5.130.148.107:8080")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

    public void updateAccessToken(String accessToken) {
        authInterceptor.setAccessToken(accessToken);
    }
}
*/
