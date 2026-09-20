package com.robindas.bloodbridge.API;

import com.robindas.bloodbridge.DTO.DonorRequest;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.DTO.LoginRequest;
import com.robindas.bloodbridge.DTO.RegisterRequest;
import com.robindas.bloodbridge.Model.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface APIServices {

    @POST("api/v1/auth/login")
    Call<String> login(@Body LoginRequest loginRequest);

    @POST("api/v1/auth/register")
    Call<String> register(@Body RegisterRequest registerRequest);


    //Profile
    @GET("api/v1/users/me")
    Call<UserProfile> getProfile();

    @GET("api/v1/donors/me")
    Call<DonorResponse> getMyDonorProfile();

    @POST("api/v1/donors/me")
    Call<DonorResponse> createDonor(@Body DonorRequest request);

    @PUT("api/v1/donors/me")
    Call<DonorResponse> updateProfile(@Body DonorRequest request);

    @DELETE("api/v1/donors/delete")
    Call<String> deleteDonorProfile();
}
