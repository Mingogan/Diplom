/*
package com.example.springclient.retrofit;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private String accessToken;

    public AuthInterceptor(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken);

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}*/
