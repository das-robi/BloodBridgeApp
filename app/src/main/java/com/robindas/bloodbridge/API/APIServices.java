package com.robindas.bloodbridge.API;

import com.robindas.bloodbridge.DTO.BloodRequestRequest;
import com.robindas.bloodbridge.DTO.BloodRequestResponse;
import com.robindas.bloodbridge.DTO.DonorRequest;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.DTO.LoginRequest;
import com.robindas.bloodbridge.DTO.PaginatedResponse;
import com.robindas.bloodbridge.DTO.RegisterRequest;
import com.robindas.bloodbridge.DTO.NotificationResponse;
import com.robindas.bloodbridge.DTO.UserResponse;
import com.robindas.bloodbridge.Model.UserProfile;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Interface defining the API endpoints for the Blood Bridge application.
 */
public interface APIServices {

    /**
     * Authenticates a user.
     */
    @POST("api/v1/auth/login")
    Call<String> login(@Body LoginRequest loginRequest);

    /**
     * Registers a new user.
     */
    @POST("api/v1/auth/register")
    Call<String> register(@Body RegisterRequest registerRequest);


    //Profile
    /**
     * Fetches the current user's profile.
     */
    @GET("api/v1/users/me")
    Call<UserProfile> getProfile();

    /**
     * Fetches the current user's donor profile.
     */
    @GET("api/v1/donors/me")
    Call<DonorResponse> getMyDonorProfile();

    /**
     * Creates a new donor profile for the current user.
     */
    @POST("api/v1/donors/me")
    Call<DonorResponse> createDonor(@Body DonorRequest request);

    /**
     * Updates the current user's donor profile.
     */
    @PUT("api/v1/donors/me")
    Call<DonorResponse> updateProfile(@Body DonorRequest request);

    /**
     * Deletes the current user's donor profile.
     */
    @DELETE("api/v1/donors/delete")
    Call<String> deleteDonorProfile();

    // Blood Requests
    /**
     * Creates a new blood request.
     */
    @POST("api/v1/blood-request/create")
    Call<BloodRequestResponse> createBloodRequest(@Body BloodRequestRequest request);

    /**
     * Fetches all blood requests.
     */
    @GET("api/v1/blood-request/all")
    Call<List<BloodRequestResponse>> getAllBloodRequests();

    /**
     * Fetches a single blood request by ID.
     */
    @GET("api/v1/blood-request/{id}")
    Call<BloodRequestResponse> getRequest(@Path("id") int id);

    /**
     * Deletes a blood request by ID.
     */
    @DELETE("api/v1/blood-request/{id}")
    Call<String> deleteBloodRequest(@Path("id") int id);

    /**
     * Accepts a blood request by ID.
     */
    @POST("api/v1/response/{requestId}/accept")
    Call<String> acceptBloodRequest(@Path("requestId") int requestId);

    /**
     * Rejects a blood request by ID.
     */
    @POST("api/v1/response/{requestId}/reject")
    Call<String> rejectBloodRequest(@Path("requestId") int requestId);

    /**
     * Fetches blood requests matching the donor's profile.
     */
    @GET("api/v1/blood-request/matches")
    Call<List<BloodRequestResponse>> getMatchingRequests();

    // Notifications
    /**
     * Fetches the current user's notifications.
     */
    @GET("api/v1/Notification/all")
    Call<List<NotificationResponse>> getMyNotifications();

    /**
     * Marks a notification as read.
     */
    @PUT("api/v1/Notification/{id}/read")
    Call<String> markNotificationAsRead(@Path("id") int id);

    // Donor Search (Dynamic Filtering)
    /**
     * Searches for donors based on filter criteria.
     */
    @GET("api/v1/donors/search")
    Call<List<DonorResponse>> searchDonors(@QueryMap Map<String, String> filters);

    // Admin Endpoints
    @GET("api/v1/admin/users")
    Call<PaginatedResponse<UserResponse>> getAllUsers(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("ascending") String ascending
    );

    @GET("api/v1/admin/donors")
    Call<PaginatedResponse<DonorResponse>> getAllDonors(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("ascending") String ascending
    );

    @GET("api/v1/admin/blood-request")
    Call<PaginatedResponse<BloodRequestResponse>> getAllBloodRequestsAdmin(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("ascending") String ascending
    );
}
