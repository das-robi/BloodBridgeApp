package com.robindas.bloodbridge.Views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for searching donors based on blood group, city, and district.
 */
public class DonorSearchActivity extends AppCompatActivity {

    private static final String TAG = "DonorSearchActivity";

    private EditText etBlood, etCity, etDistrict;
    private Button btnSearch;
    private RecyclerView rvResults;
    private DonorAdapter adapter;
    private List<DonorResponse> donorList = new ArrayList<>();
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: DonorSearchActivity started");
        setContentView(R.layout.activity_donor_search);

        etBlood = findViewById(R.id.etSearchBloodGroup);
        etCity = findViewById(R.id.etSearchCity);
        etDistrict = findViewById(R.id.etSearchDistrict);
        btnSearch = findViewById(R.id.btnSearchSubmit);
        rvResults = findViewById(R.id.rvDonorSearchResults);

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonorAdapter(donorList);
        rvResults.setAdapter(adapter);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        btnSearch.setOnClickListener(v -> searchDonors());
    }

    /**
     * Executes the donor search based on the provided filters.
     */
    private void searchDonors() {
        Map<String, String> filters = new HashMap<>();
        String blood = etBlood.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();

        if (!blood.isEmpty()) filters.put("bldGroup", blood);
        if (!city.isEmpty()) filters.put("city", city);
        if (!district.isEmpty()) filters.put("district", district);

        Log.d(TAG, "searchDonors: Searching with filters: " + filters);
        apiServices.searchDonors(filters).enqueue(new Callback<List<DonorResponse>>() {
            @Override
            public void onResponse(Call<List<DonorResponse>> call, Response<List<DonorResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Search successful. Found " + response.body().size() + " donors");
                    donorList.clear();
                    donorList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "onResponse: Search failed. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<DonorResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: Search request failed", t);
                Toast.makeText(DonorSearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
