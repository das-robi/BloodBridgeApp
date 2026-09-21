package com.robindas.bloodbridge.API;

import android.content.Context;
import android.util.Log;

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
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        String path = originalRequest.url().encodedPath();

        Request.Builder requestBuilder =
                originalRequest.newBuilder();

        // Don't attach JWT to login/register
        if (!path.equals("/api/v1/auth/login")
                && !path.equals("/api/v1/auth/register")) {

            String token = tokenManager.getToken();

            if (token != null && !token.isEmpty()) {

                requestBuilder.addHeader(
                        "Authorization",
                        "Bearer " + token
                );

                Log.d("AUTH_INTERCEPTOR", "Header added for path: " + path);
                Log.d("AUTH_INTERCEPTOR", "Token: " + token.substring(0, Math.min(token.length(), 10)) + "...");
            } else {
                Log.w("AUTH_INTERCEPTOR", "No token found for path: " + path);
            }
        }

        return chain.proceed(requestBuilder.build());
    }
}