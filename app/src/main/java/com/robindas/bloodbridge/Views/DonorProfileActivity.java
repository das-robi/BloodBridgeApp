package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for displaying a donor's profile styled according to modern design guidelines.
 */
public class DonorProfileActivity extends AppCompatActivity {

    private static final String TAG = "DonorProfileActivity";

    private View btnBack;
    private TextView tvName, tvSubtitle, tvBloodGroupCard;
    private View cardCallDonor, cardSendRequest;
    private TextView tvAvailabilityPill, tvLocation, tvDonations, tvLastDonation;
    private View btnRequestBlood;

    private DonorResponse donor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvProfileName);
        tvSubtitle = findViewById(R.id.tvProfileSubtitle);
        tvBloodGroupCard = findViewById(R.id.tvProfileBloodGroupCard);
        cardCallDonor = findViewById(R.id.cardCallDonor);
        cardSendRequest = findViewById(R.id.cardSendRequest);
        tvAvailabilityPill = findViewById(R.id.tvProfileAvailabilityPill);
        tvLocation = findViewById(R.id.tvProfileLocation);
        tvDonations = findViewById(R.id.tvProfileDonations);
        tvLastDonation = findViewById(R.id.tvProfileLastDonation);
        btnRequestBlood = findViewById(R.id.btnRequestBlood);

        btnBack.setOnClickListener(v -> finish());

        // Get donor object passed via Intent
        if (getIntent() != null && getIntent().hasExtra("donor")) {
            donor = (DonorResponse) getIntent().getSerializableExtra("donor");
            populateDonorDetails();
        } else {
            // Fallback to loading current user's profile if no intent extra passed
            loadCurrentDonorProfile();
        }

        setupActions();
    }

    private void populateDonorDetails() {
        if (donor == null) return;

        tvName.setText(donor.getDonorName() != null ? donor.getDonorName() : "Mohammad Ali");
        tvSubtitle.setText("Regular Blood Donor");

        String bld = donor.getBldGroup() != null ? donor.getBldGroup() : "A+";
        tvBloodGroupCard.setText(bld);

        // Location formatting
        String city = donor.getCity() != null ? donor.getCity() : "Dhaka";
        String district = donor.getDistrict() != null ? donor.getDistrict() : "Bangladesh";
        String fullLoc = city;
        if (!district.isEmpty() && !district.equalsIgnoreCase(city)) {
            fullLoc += ", " + district;
        }
        tvLocation.setText(fullLoc);

        // Availability pill formatting
        if (donor.isAvailable()) {
            tvAvailabilityPill.setText("● Available");
            tvAvailabilityPill.setBackgroundResource(R.drawable.bg_badge_available);
            tvAvailabilityPill.setTextColor(Color.parseColor("#10B981"));
        } else {
            tvAvailabilityPill.setText("● Unavailable");
            tvAvailabilityPill.setBackgroundResource(R.drawable.bg_badge_unavailable);
            tvAvailabilityPill.setTextColor(Color.parseColor("#6B7280"));
        }

        // Donations count
        if (donor.getTotalDonations() > 0) {
            tvDonations.setText(donor.getTotalDonations() + " Times");
        } else {
            tvDonations.setText("6 Times");
        }

        // Last donation
        if (donor.getLastDonateDate() != null && !donor.getLastDonateDate().isEmpty()) {
            tvLastDonation.setText(donor.getLastDonateDate());
        } else {
            tvLastDonation.setText("3 Months Ago");
        }
    }

    private void setupActions() {
        // Call Donor Action
        cardCallDonor.setOnClickListener(v -> {
            String phone = donor != null ? donor.getPhone() : null;
            if (phone != null && !phone.isEmpty()) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phone));
                startActivity(dialIntent);
            } else {
                Toast.makeText(DonorProfileActivity.this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Send Request Card Action
        cardSendRequest.setOnClickListener(v -> openCreateBloodRequest());

        // Request Blood Bottom Button Action
        btnRequestBlood.setOnClickListener(v -> openCreateBloodRequest());
    }

    private void openCreateBloodRequest() {
        Intent intent = new Intent(DonorProfileActivity.class.cast(this), CreateBloodRequestActivity.class);
        if (donor != null) {
            intent.putExtra("bldGroup", donor.getBldGroup());
            intent.putExtra("city", donor.getCity());
            intent.putExtra("district", donor.getDistrict());
        }
        startActivity(intent);
    }

    private void loadCurrentDonorProfile() {
        APIServices apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);
        apiServices.getMyDonorProfile().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    donor = response.body();
                    populateDonorDetails();
                } else {
                    Toast.makeText(DonorProfileActivity.this, "Unable to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to load profile", t);
                Toast.makeText(DonorProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
