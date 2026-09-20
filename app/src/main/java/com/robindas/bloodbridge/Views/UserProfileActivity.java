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

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvEmail;
    private Button donBtn;

    private APIServices apiServices;
    private boolean hasDonorProfile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);


        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        donBtn = findViewById(R.id.btnDonorProfile);

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

        apiServices = RetrofitClient
                .getRetrofitInstance(this)
                .create(APIServices.class);

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
                    donBtn.setText("Check Donor Profile");
                } else {
                    hasDonorProfile = false;
                    donBtn.setText("Create Donor Profile");
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                hasDonorProfile = false;
                donBtn.setText("Create Donor Profile");
            }
        });
    }

    private void loadProfile() {

        apiServices.getProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {

                if (response.isSuccessful() && response.body() != null){

                    UserProfile userProfile = response.body();

                    tvUsername.setText(userProfile.getUserName());
                    tvEmail.setText(userProfile.getUserEmail());

                    Log.d("UserProfile ", "Profile open successfully");
                }
                else {
                    Toast.makeText(UserProfileActivity.this, "Failed to load Profile", Toast.LENGTH_SHORT).show();
                    Log.e("Profile", "Response Code" + response.code());
                }

            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable throwable) {

                Toast.makeText(UserProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                Log.e("Profile_Error", "Profile request failed", throwable);
            }
        });

    }
}