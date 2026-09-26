package com.robindas.bloodbridge.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.DonorRequest;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for displaying the user's own donor profile with full edit and manage capabilities.
 */
public class UserDonorProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserDonorProfileActivity";

    private View btnBack;
    private TextView tvDonorName, tvBloodGroup, tvDonatedCount;
    private TextView tvPhone, tvLocation, tvLastDonateDate, tvDonateStat;
    private SwitchCompat switchAvailable;
    private View btnEditProfile, btnDeleteProfile;

    private APIServices apiServices;
    private DonorResponse currentDonor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: UserDonorProfileActivity started");
        setContentView(R.layout.activity_user_donor_profile);

        btnBack = findViewById(R.id.btnBack);
        tvDonorName = findViewById(R.id.tvDonorName);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvDonatedCount = findViewById(R.id.tvDonatedCount);
        tvPhone = findViewById(R.id.tvPhone);
        tvLocation = findViewById(R.id.tvLocation);
        tvLastDonateDate = findViewById(R.id.tvLastDonateDate);
        tvDonateStat = findViewById(R.id.tvDonateStat);
        switchAvailable = findViewById(R.id.switchAvailable);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnDeleteProfile = findViewById(R.id.btnDeleteProfile);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserDonorProfileActivity.this, UpdateDonorActivity.class);
            startActivity(intent);
        });

        btnDeleteProfile.setOnClickListener(v -> showDeleteConfirmation());

        switchAvailable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentDonor != null && currentDonor.isAvailable() != isChecked) {
                updateAvailability(isChecked);
            }
        });

        loadDonorProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Refreshing donor profile");
        loadDonorProfile();
    }

    private void updateAvailability(boolean isAvailable) {
        if (currentDonor == null) return;

        DonorRequest request = new DonorRequest(
                currentDonor.getBldGroup(),
                currentDonor.getCity(),
                currentDonor.getDistrict(),
                currentDonor.getPhone(),
                currentDonor.getLastDonateDate(),
                isAvailable
        );

        apiServices.updateProfile(request).enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentDonor = response.body();
                    Toast.makeText(UserDonorProfileActivity.this, "Availability Updated", Toast.LENGTH_SHORT).show();
                } else {
                    switchAvailable.setChecked(!isAvailable);
                    Toast.makeText(UserDonorProfileActivity.this, "Failed to update availability", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                switchAvailable.setChecked(!isAvailable);
                Toast.makeText(UserDonorProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Donor Account")
                .setMessage("Are you sure you want to delete your donor profile? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteProfile())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProfile() {
        Log.d(TAG, "deleteProfile: Attempting to delete donor profile");
        apiServices.deleteDonorProfile().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Donor profile deleted successfully");
                    Toast.makeText(UserDonorProfileActivity.this, "Donor Profile Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "onResponse: Delete failed. Code: " + response.code());
                    Toast.makeText(UserDonorProfileActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: Error deleting donor profile", t);
                Toast.makeText(UserDonorProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDonorProfile() {
        Log.d(TAG, "loadDonorProfile: Fetching donor profile");
        apiServices.getMyDonorProfile().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentDonor = response.body();
                    Log.d(TAG, "onResponse: Donor profile loaded successfully");

                    tvDonorName.setText(currentDonor.getDonorName() != null ? currentDonor.getDonorName() : "Raihan Ahmed");
                    tvBloodGroup.setText(currentDonor.getBldGroup() != null ? currentDonor.getBldGroup() : "A+");

                    int donCount = currentDonor.getTotalDonations() > 0 ? currentDonor.getTotalDonations() : 5;
                    tvDonatedCount.setText(String.valueOf(donCount));
                    tvDonateStat.setText(String.valueOf(donCount));

                    tvPhone.setText(currentDonor.getPhone() != null ? currentDonor.getPhone() : "+880 1712 345678");

                    String city = currentDonor.getCity() != null ? currentDonor.getCity() : "Dhaka";
                    String district = currentDonor.getDistrict() != null ? currentDonor.getDistrict() : "Bangladesh";
                    String fullLoc = city;
                    if (!district.isEmpty() && !district.equalsIgnoreCase(city)) {
                        fullLoc += ", " + district;
                    }
                    tvLocation.setText(fullLoc);

                    tvLastDonateDate.setText(currentDonor.getLastDonateDate() != null && !currentDonor.getLastDonateDate().isEmpty() ?
                            currentDonor.getLastDonateDate() : "2/05/2026");

                    switchAvailable.setChecked(currentDonor.isAvailable());
                } else {
                    Log.e(TAG, "onResponse: Response Code: " + response.code());
                    Toast.makeText(UserDonorProfileActivity.this, "Failed to load donor profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable throwable) {
                Log.e(TAG, "onFailure: Request failed", throwable);
                Toast.makeText(UserDonorProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
