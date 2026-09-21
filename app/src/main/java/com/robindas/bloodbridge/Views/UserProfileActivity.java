package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.Model.UserProfile;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for displaying the user's main profile and dashboard.
 */
public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    private TextView tvUsername;
    private TextView tvEmail;
    private Button donBtn;
    private Button createReqBtn;
    private Button viewReqBtn;
    private Button findDonorsBtn;
    private Button notificationsBtn;
    private Button adminBtn;

    private APIServices apiServices;
    private boolean hasDonorProfile = false;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: UserProfileActivity started");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);


        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        donBtn = findViewById(R.id.btnDonorProfile);
        createReqBtn = findViewById(R.id.btnCreateBloodRequest);
        viewReqBtn = findViewById(R.id.btnViewAllRequests);
        findDonorsBtn = findViewById(R.id.btnFindDonors);
        notificationsBtn = findViewById(R.id.btnNotifications);
        adminBtn = findViewById(R.id.btnAdminDashboard);

        donBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasDonorProfile) {
                    Intent intent = new Intent(UserProfileActivity.this, UserDonorProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(UserProfileActivity.this, CreateDonorActivity.class);
                    startActivity(intent);
                }
            }
        });

        createReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, CreateBloodRequestActivity.class);
                startActivity(intent);
            }
        });

        viewReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, BloodRequestListActivity.class);
                intent.putExtra("USER_NAME", currentUserName);
                startActivity(intent);
            }
        });

        findDonorsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, DonorSearchActivity.class));
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, NotificationActivity.class));
            }
        });

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, AdminDashboardActivity.class));
            }
        });

        apiServices = RetrofitClient
                .getRetrofitInstance(this)
                .create(APIServices.class);

        loadProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Checking donor status");
        checkDonorStatus();
    }

    /**
     * Checks if the user has a donor profile to update the UI accordingly.
     */
    private void checkDonorStatus() {
        apiServices.getMyDonorProfile().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: User has a donor profile");
                    hasDonorProfile = true;
                    donBtn.setText("Check Donor Profile");
                } else {
                    Log.d(TAG, "onResponse: User does not have a donor profile");
                    hasDonorProfile = false;
                    donBtn.setText("Create Donor Profile");
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error checking donor status", t);
                hasDonorProfile = false;
                donBtn.setText("Create Donor Profile");
            }
        });
    }

    /**
     * Loads the user's basic profile information.
     */
    private void loadProfile() {

        Log.d(TAG, "loadProfile: Fetching user profile");
        apiServices.getProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {

                if (response.isSuccessful() && response.body() != null){

                    UserProfile userProfile = response.body();
                    currentUserName = userProfile.getUserName();
                    Log.d(TAG, "onResponse: Profile loaded for " + currentUserName);

                    tvUsername.setText(userProfile.getUserName());
                    tvEmail.setText(userProfile.getUserEmail());

                    if ("ADMIN".equals(userProfile.getRole())) {
                        adminBtn.setVisibility(View.VISIBLE);
                    } else {
                        adminBtn.setVisibility(View.GONE);
                    }

                }
                else {
                    Log.e(TAG, "onResponse: Failed to load profile. Code: " + response.code());
                    Toast.makeText(UserProfileActivity.this, "Failed to load Profile", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable throwable) {
                Log.e(TAG, "onFailure: Error loading user profile", throwable);
                Toast.makeText(UserProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

    }
}