package com.robindas.bloodbridge.Views;

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
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.DTO.PaginatedResponse;
import com.robindas.bloodbridge.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for Admins to view all donors with pagination and sorting.
 */
public class AdminDonorListActivity extends AppCompatActivity {

    private static final String TAG = "AdminDonorListActivity";

    private RecyclerView rvDonors;
    private DonorAdapter adapter;
    private List<DonorResponse> donorList = new ArrayList<>();
    private APIServices apiServices;

    private int currentPage = 0;
    private final int pageSize = 10;
    private String currentSortBy = "donorName";
    private String currentOrder = "asc";

    private Button btnNext, btnPrev;
    private TextView tvPageIndicator;
    private Spinner spinnerSort;
    private ToggleButton btnToggleOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_donor_list);

        rvDonors = findViewById(R.id.rvAdminDonors);
        btnNext = findViewById(R.id.btnNextPageDonor);
        btnPrev = findViewById(R.id.btnPrevPageDonor);
        tvPageIndicator = findViewById(R.id.tvPageIndicatorDonor);
        spinnerSort = findViewById(R.id.spinnerSortByDonor);
        btnToggleOrder = findViewById(R.id.btnSortOrderDonor);

        rvDonors.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonorAdapter(donorList);
        rvDonors.setAdapter(adapter);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        setupSorting();
        setupPagination();

        loadDonors();
    }

    private void setupSorting() {
        String[] sortOptions = {"donorName", "bldGroup", "city"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortBy = sortOptions[position];
                currentPage = 0;
                loadDonors();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnToggleOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentOrder = isChecked ? "desc" : "asc";
            currentPage = 0;
            loadDonors();
        });
    }

    private void setupPagination() {
        btnNext.setOnClickListener(v -> {
            currentPage++;
            loadDonors();
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                loadDonors();
            }
        });
    }

    private void loadDonors() {
        Log.d(TAG, "loadDonors: Fetching page " + currentPage);
        apiServices.getAllDonors(currentPage, pageSize, currentSortBy, currentOrder).enqueue(new Callback<PaginatedResponse<DonorResponse>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<DonorResponse>> call, Response<PaginatedResponse<DonorResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaginatedResponse<DonorResponse> paginatedData = response.body();
                    donorList.clear();
                    donorList.addAll(paginatedData.getContent());
                    adapter.notifyDataSetChanged();

                    tvPageIndicator.setText("Page " + (currentPage + 1) + " of " + paginatedData.getTotalPages());
                    btnPrev.setEnabled(currentPage > 0);
                    btnNext.setEnabled(currentPage < paginatedData.getTotalPages() - 1);
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<DonorResponse>> call, Throwable t) {
                Toast.makeText(AdminDonorListActivity.this, "Error loading donors", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
