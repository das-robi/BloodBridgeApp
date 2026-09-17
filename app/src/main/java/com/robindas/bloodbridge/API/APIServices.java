package com.robindas.bloodbridge.API;

import com.robindas.bloodbridge.Model.LoginRequest;
import com.robindas.bloodbridge.Model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIServices {

    @POST("api/v1/auth/login")
    Call<String> login(@Body LoginRequest loginRequest);

    @POST("api/v1/auth/register")
    Call<String> register(@Body RegisterRequest registerRequest);
}
