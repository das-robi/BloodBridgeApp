package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.BloodRequestResponse;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for displaying a list of blood requests.
 */
public class BloodRequestListActivity extends AppCompatActivity {

    private static final String TAG = "BloodRequestListActivity";
    private RecyclerView rvRequests;
    private BloodRequestAdapter adapter;
    private List<BloodRequestResponse> requestList = new ArrayList<>();
    private APIServices apiServices;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: BloodRequestListActivity started");
        setContentView(R.layout.activity_blood_request_list);

        userName = getIntent().getStringExtra("USER_NAME");

        rvRequests = findViewById(R.id.rvBloodRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new BloodRequestAdapter(requestList, request -> {
            Intent intent = new Intent(BloodRequestListActivity.this, BloodRequestDetailActivity.class);
            intent.putExtra("BLD_ID", request.getBldId());
            startActivity(intent);
        });

        rvRequests.setAdapter(adapter);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        loadRequests();
    }

    /**
     * Initial step to load requests, determining if matching or all requests should be shown.
     */
    private void loadRequests() {

        Log.d(TAG, "loadRequests: Checking donor status");
        apiServices.getMyDonorProfile().enqueue(new Callback<DonorResponse>() {

            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: User is a donor, loading matching requests");
                    loadMatchingRequests();
                }
                else {
                    Log.d(TAG, "onResponse: User is not a donor, loading all requests");
                    loadAllRequests();
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Donor status check failed", t);
                loadAllRequests(); // Fallback to all
            }
        });

    }

    /**
     * Loads matching blood requests for the donor.
     */
    private void loadMatchingRequests() {

        apiServices.getMatchingRequests().enqueue(new Callback<List<BloodRequestResponse>>() {

            @Override
            public void onResponse(Call<List<BloodRequestResponse>> call, Response<List<BloodRequestResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Matching requests loaded: " + response.body().size());
                    displayList(response.body());
                }
                else {
                    Log.e(TAG, "onResponse: Failed to load matching requests. Code: " + response.code());
                    loadAllRequests();
                }

            }

            @Override
            public void onFailure(Call<List<BloodRequestResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error loading matching requests", t);
                loadAllRequests();
            }
        });

    }

    /**
     * Loads all blood requests available in the system.
     */
    private void loadAllRequests() {

        apiServices.getAllBloodRequests().enqueue(new Callback<List<BloodRequestResponse>>() {
            @Override
            public void onResponse(Call<List<BloodRequestResponse>> call, Response<List<BloodRequestResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: All requests loaded: " + response.body().size());
                    displayList(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<BloodRequestResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error loading all requests", t);
                Toast.makeText(BloodRequestListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Filters and displays the list of blood requests in the RecyclerView.
     * @param list The list of blood requests to display.
     */
    private void displayList(List<BloodRequestResponse> list) {
        requestList.clear();

        if (userName != null) {
            for (BloodRequestResponse req : list) {
                if (userName.equals(req.getRequesterName())) {
                    requestList.add(req);
                }
            }
        } else {
            requestList.addAll(list);
        }

        adapter.notifyDataSetChanged();
    }
}
