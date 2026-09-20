package com.robindas.bloodbridge.Views;

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
import com.robindas.bloodbridge.DTO.BloodRequestResponse;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for displaying detailed information about a blood request.
 */
public class BloodRequestDetailActivity extends AppCompatActivity {

    private static final String TAG = "BloodRequestDetailActivity";

    private TextView tvPatient, tvBlood, tvLocation, tvHospital, tvUnit, tvRequester, tvStatus;
    private Button btnAccept, btnReject, btnDelete;
    private APIServices apiServices;
    private int bldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: BloodRequestDetailActivity started");
        setContentView(R.layout.activity_blood_request_detail);

        tvPatient = findViewById(R.id.tvDetPatient);
        tvBlood = findViewById(R.id.tvDetBlood);
        tvLocation = findViewById(R.id.tvDetLocation);
        tvHospital = findViewById(R.id.tvDetHospital);
        tvUnit = findViewById(R.id.tvDetUnit);
        tvRequester = findViewById(R.id.tvDetRequester);
        tvStatus = findViewById(R.id.tvDetStatus);

        btnAccept = findViewById(R.id.btnAcceptReq);
        btnReject = findViewById(R.id.btnRejectReq);
        btnDelete = findViewById(R.id.btnDeleteReq);

        bldId = getIntent().getIntExtra("BLD_ID", -1);
        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        loadDetails();

        btnAccept.setOnClickListener(v -> acceptRequest());
        btnReject.setOnClickListener(v -> rejectRequest());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    /**
     * Loads the details of the specific blood request from the API.
     */
    private void loadDetails() {

        Log.d(TAG, "loadDetails: Fetching details for request ID: " + bldId);
        apiServices.getRequest(bldId).enqueue(new Callback<BloodRequestResponse>() {
            @Override
            public void onResponse(Call<BloodRequestResponse> call, Response<BloodRequestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Details loaded successfully");
                    displayRequest(response.body());
                } else {
                    Log.e(TAG, "onResponse: Failed to load details. Code: " + response.code());
                    Toast.makeText(BloodRequestDetailActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BloodRequestResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error loading details", t);
                Toast.makeText(BloodRequestDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Displays the request details in the UI.
     * @param request The blood request data to display.
     */
    private void displayRequest(BloodRequestResponse request) {

        tvPatient.setText("Patient: " + request.getPatientName());
        tvBlood.setText("Blood Group: " + request.getBldGroup());
        tvLocation.setText("Location: " + request.getCity() + ", " + request.getDistrict());
        tvHospital.setText("Hospital: " + request.getHospital());
        tvUnit.setText("Units: " + request.getUnit());
        tvRequester.setText("Requester: " + request.getRequesterName());
        tvStatus.setText("Status: " + request.getStatus());

        // Check if user is the creator to show delete button
        // This requires knowing the current user. For now, let's just show it.
        btnDelete.setVisibility(View.VISIBLE);

    }

    /**
     * Accepts the blood request by calling the API.
     */
    private void acceptRequest() {
        Log.d(TAG, "acceptRequest: Accepting request ID: " + bldId);
        apiServices.acceptBloodRequest(bldId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Request accepted successfully");
                    Toast.makeText(BloodRequestDetailActivity.this, "Request Accepted!", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 409) {
                    Log.w(TAG, "onResponse: Conflict - request already accepted");
                    Toast.makeText(BloodRequestDetailActivity.this, "This request has already been accepted or you have already accepted it.", Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "onResponse: Failed to accept request. Code: " + response.code());
                    Toast.makeText(BloodRequestDetailActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: Error accepting request", t);
                Toast.makeText(BloodRequestDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Rejects the blood request by calling the API.
     */
    private void rejectRequest() {
        Log.d(TAG, "rejectRequest: Rejecting request ID: " + bldId);
        apiServices.rejectBloodRequest(bldId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Request rejected successfully");
                    Toast.makeText(BloodRequestDetailActivity.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "onResponse: Failed to reject request. Code: " + response.code());
                    Toast.makeText(BloodRequestDetailActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: Error rejecting request", t);
                Toast.makeText(BloodRequestDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shows a confirmation dialog before deleting the request.
     */
    private void showDeleteConfirmation() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Request")
                .setMessage("Are you sure you want to delete this blood request?")
                .setPositiveButton("Delete", (dialog, which) -> deleteRequest())
                .setNegativeButton("Cancel", null)
                .show();

    }

    /**
     * Deletes the blood request by calling the API.
     */
    private void deleteRequest() {

        Log.d(TAG, "deleteRequest: Deleting request ID: " + bldId);
        apiServices.deleteBloodRequest(bldId).enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Request deleted successfully");
                    Toast.makeText(BloodRequestDetailActivity.this, "Request Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "onResponse: Failed to delete request. Code: " + response.code());
                    Toast.makeText(BloodRequestDetailActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: Error deleting request", t);
                Toast.makeText(BloodRequestDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
