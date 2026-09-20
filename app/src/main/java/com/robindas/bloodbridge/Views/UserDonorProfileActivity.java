package com.robindas.bloodbridge.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDonorProfileActivity extends AppCompatActivity {


    private TextView tvDonorName;
    private TextView tvBloodGroup;
    private TextView tvCity;
    private TextView tvDistrict;
    private TextView tvPhone;
    private TextView tvLastDonateDate;
    private TextView tvAvailable;

    private Button btnUpdate, btnDelete;

    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_donor_profile);

        // Initialize TextViews
        tvDonorName = findViewById(R.id.tvDonorName);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvCity = findViewById(R.id.tvCity);
        tvDistrict = findViewById(R.id.tvDistrict);
        tvPhone = findViewById(R.id.tvPhone);
        tvLastDonateDate = findViewById(R.id.tvLastDonateDate);
        tvAvailable = findViewById(R.id.tvAvailable);

        btnUpdate = findViewById(R.id.btnUpdateProfile);
        btnDelete = findViewById(R.id.btnDeleteProfile);

        // Retrofit API
        apiServices = RetrofitClient
                .getRetrofitInstance(this)
                .create(APIServices.class);

        // Load donor profile
        loadDonorProfile();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDonorProfileActivity.this, UpdateDonorActivity.class);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDonorProfile();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Donor Profile")
                .setMessage("Are you sure you want to delete your donor profile?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProfile();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProfile() {
        apiServices.deleteDonorProfile().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserDonorProfileActivity.this, "Profile Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UserDonorProfileActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(UserDonorProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDonorProfile() {

        apiServices.getMyDonorProfile().enqueue(
                new Callback<DonorResponse>() {

                    @Override
                    public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            DonorResponse donor = response.body();

                            // Set donor information
                            tvDonorName.setText(donor.getDonorName());

                            tvBloodGroup.setText(donor.getBldGroup());

                            tvCity.setText(donor.getCity());

                            tvDistrict.setText(donor.getDistrict());

                            tvPhone.setText(donor.getPhone());

                            tvLastDonateDate.setText(donor.getLastDonateDate());

                            if (donor.isAvailable()) {
                                tvAvailable.setText("Available");
                            } else {
                                tvAvailable.setText("Not Available");
                            }

                            Log.d("DONOR_PROFILE", "Donor profile loaded successfully");

                        }
                        else {

                            Log.e("DONOR_PROFILE", "Response Code: " + response.code());

                            Toast.makeText(UserDonorProfileActivity.this, "Failed to load donor profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DonorResponse> call, Throwable throwable) {

                        Log.e("DONOR_PROFILE", "Request failed", throwable);

                        Toast.makeText(UserDonorProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}