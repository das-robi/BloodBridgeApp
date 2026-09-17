package com.robindas.bloodbridge.API;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        tokenManager = new TokenManager(context);
    }


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        // Get JWT token from TokenManager and store in a String value
        String token = tokenManager.getToken();

        //Retrofit request
        Request originalRequest = chain.request();

        Request.Builder requestBuilder = originalRequest.newBuilder();

        //Check here if token is not null or empty and then set bearer token
        if (token != null && token.isEmpty()){
            requestBuilder.addHeader(
                    "Authentication",
                    "Bearer " + token
            );
        }

        Request newRequest = requestBuilder.build();

        return chain.proceed(newRequest);
    }
}
