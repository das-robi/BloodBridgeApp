package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.BloodRequestResponse;
import com.robindas.bloodbridge.DTO.PaginatedResponse;
import com.robindas.bloodbridge.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for Admins to view all blood requests with pagination and sorting.
 */
public class AdminBloodRequestListActivity extends AppCompatActivity {

    private static final String TAG = "AdminBloodRequestList";

    private RecyclerView rvRequests;
    private BloodRequestAdapter adapter;
    private List<BloodRequestResponse> requestList = new ArrayList<>();
    private APIServices apiServices;

    private int currentPage = 0;
    private final int pageSize = 10;
    private String currentSortBy = "bldGroup";
    private String currentOrder = "asc";

    private Button btnNext, btnPrev;
    private TextView tvPageIndicator;
    private Spinner spinnerSort;
    private ToggleButton btnToggleOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_blood_request_list);

        rvRequests = findViewById(R.id.rvAdminRequests);
        btnNext = findViewById(R.id.btnNextPageReq);
        btnPrev = findViewById(R.id.btnPrevPageReq);
        tvPageIndicator = findViewById(R.id.tvPageIndicatorReq);
        spinnerSort = findViewById(R.id.spinnerSortByReq);
        btnToggleOrder = findViewById(R.id.btnSortOrderReq);

        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodRequestAdapter(requestList, request -> {
            Intent intent = new Intent(AdminBloodRequestListActivity.this, BloodRequestDetailActivity.class);
            intent.putExtra("BLD_ID", request.getBldId());
            startActivity(intent);
        });
        rvRequests.setAdapter(adapter);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        setupSorting();
        setupPagination();

        loadRequests();
    }

    private void setupSorting() {
        String[] sortOptions = {"bldGroup", "city", "patientName"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortBy = sortOptions[position];
                currentPage = 0;
                loadRequests();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnToggleOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentOrder = isChecked ? "desc" : "asc";
            currentPage = 0;
            loadRequests();
        });
    }

    private void setupPagination() {
        btnNext.setOnClickListener(v -> {
            currentPage++;
            loadRequests();
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                loadRequests();
            }
        });
    }

    private void loadRequests() {
        Log.d(TAG, "loadRequests: Fetching page " + currentPage);
        apiServices.getAllBloodRequestsAdmin(currentPage, pageSize, currentSortBy, currentOrder).enqueue(new Callback<PaginatedResponse<BloodRequestResponse>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<BloodRequestResponse>> call, Response<PaginatedResponse<BloodRequestResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaginatedResponse<BloodRequestResponse> paginatedData = response.body();
                    requestList.clear();
                    requestList.addAll(paginatedData.getContent());
                    adapter.notifyDataSetChanged();

                    tvPageIndicator.setText("Page " + (currentPage + 1) + " of " + paginatedData.getTotalPages());
                    btnPrev.setEnabled(currentPage > 0);
                    btnNext.setEnabled(currentPage < paginatedData.getTotalPages() - 1);
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<BloodRequestResponse>> call, Throwable t) {
                Toast.makeText(AdminBloodRequestListActivity.this, "Error loading requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
