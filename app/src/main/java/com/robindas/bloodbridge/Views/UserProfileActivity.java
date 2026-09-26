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

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.API.TokenManager;
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

    private TextView tvUsernameLarge, tvEmailProfile, tvBloodBadge;
    private Button donBtn, btnLogout;

    private APIServices apiServices;
    private boolean hasDonorProfile = false;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: UserProfileActivity started");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        tvUsernameLarge = findViewById(R.id.tvUsernameLarge);
        tvEmailProfile = findViewById(R.id.tvEmailProfile);
        tvBloodBadge = findViewById(R.id.tvBloodBadge);
        donBtn = findViewById(R.id.btnDonorProfile);
        btnLogout = findViewById(R.id.btnLogout);

        findViewById(R.id.ivBackProfile).setOnClickListener(v -> finish());

        donBtn.setOnClickListener(v -> {
            if (hasDonorProfile) {
                Intent intent = new Intent(UserProfileActivity.this, UserDonorProfileActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(UserProfileActivity.this, CreateDonorActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(v -> {
            TokenManager tm = new TokenManager(this);
            tm.clearToken();
            Intent intent = new Intent(UserProfileActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        loadProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDonorStatus();
    }

    private void checkDonorStatus() {
        apiServices.getMyDonorProfile().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hasDonorProfile = true;
                    donBtn.setText("View Donor Profile");
                    tvBloodBadge.setText(response.body().getBldGroup());
                    tvBloodBadge.setVisibility(View.VISIBLE);
                } else {
                    hasDonorProfile = false;
                    donBtn.setText("Create Donor Account");
                    tvBloodBadge.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                hasDonorProfile = false;
                donBtn.setText("Create Donor Account");
                tvBloodBadge.setVisibility(View.GONE);
            }
        });
    }

    private void loadProfile() {
        apiServices.getProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null){
                    UserProfile userProfile = response.body();
                    currentUserName = userProfile.getUserName();
                    tvUsernameLarge.setText(userProfile.getUserName());
                    tvEmailProfile.setText(userProfile.getUserEmail());
                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed to load Profile", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserProfile> call, Throwable throwable) {
                Toast.makeText(UserProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
