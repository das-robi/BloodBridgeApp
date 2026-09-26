package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.DTO.PaginatedResponse;
import com.robindas.bloodbridge.Model.UserProfile;
import com.robindas.bloodbridge.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main Home Dashboard Activity.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private TextView tvUserName;
    private RecyclerView rvDonors;
    private HomeDonorAdapter adapter;
    private List<DonorResponse> donorList = new ArrayList<>();
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        tvUserName = findViewById(R.id.tvUserNameHome);
        rvDonors = findViewById(R.id.rvNearbyDonors);
        
        rvDonors.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeDonorAdapter(donorList);
        rvDonors.setAdapter(adapter);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        // Setup Nav
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
        });

        findViewById(R.id.cardFindDonor).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, DonorSearchActivity.class));
        });

        findViewById(R.id.cardBloodRequest).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CreateBloodRequestActivity.class));
        });

        loadUserInfo();
        loadDonors();
    }

    private void loadUserInfo() {
        apiServices.getProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvUserName.setText(response.body().getUserName());
                }
            }
            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "onFailure: User load error", t);
            }
        });
    }

    private void loadDonors() {
        // Fetching first page of donors as "nearby" example
        apiServices.getAllDonors(0, 5, "donorName", "asc").enqueue(new Callback<PaginatedResponse<DonorResponse>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<DonorResponse>> call, Response<PaginatedResponse<DonorResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    donorList.clear();
                    donorList.addAll(response.body().getContent());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<PaginatedResponse<DonorResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: Donor load error", t);
            }
        });
    }
}
