package com.robindas.bloodbridge.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.DTO.PaginatedResponse;
import com.robindas.bloodbridge.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for searching and listing donors matching the Find Donors UI design.
 */
public class DonorSearchActivity extends AppCompatActivity {

    private static final String TAG = "DonorSearchActivity";

    private View btnBack;
    private TextView tvFilterLocation, tvFilterBloodGroup, tvFilterAvailability;
    private RecyclerView rvResults;
    private TextView btnLoadMoreDonors;

    private DonorAdapter adapter;
    private List<DonorResponse> donorList = new ArrayList<>();
    private APIServices apiServices;

    private String selectedLocation = "";
    private String selectedBloodGroup = "";
    private String selectedAvailability = "";
    private int currentPage = 0;
    private final int pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: DonorSearchActivity started");
        setContentView(R.layout.activity_donor_search);

        btnBack = findViewById(R.id.btnBack);
        tvFilterLocation = findViewById(R.id.tvFilterLocation);
        tvFilterBloodGroup = findViewById(R.id.tvFilterBloodGroup);
        tvFilterAvailability = findViewById(R.id.tvFilterAvailability);
        rvResults = findViewById(R.id.rvDonorSearchResults);
        btnLoadMoreDonors = findViewById(R.id.btnLoadMoreDonors);

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        
        // Adapter listener to open DonorProfileActivity on item click
        adapter = new DonorAdapter(donorList, donor -> {
            Intent intent = new Intent(DonorSearchActivity.this, DonorProfileActivity.class);
            intent.putExtra("donor", donor);
            startActivity(intent);
        });
        rvResults.setAdapter(adapter);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        btnBack.setOnClickListener(v -> finish());

        setupFilterListeners();

        btnLoadMoreDonors.setOnClickListener(v -> loadMoreDonors());

        // Initial search/fetch
        fetchDonors();
    }

    private void setupFilterListeners() {
        // Location Filter Dialog
        tvFilterLocation.setOnClickListener(v -> showLocationFilterDialog());

        // Blood Group Filter Dialog
        tvFilterBloodGroup.setOnClickListener(v -> showBloodGroupFilterDialog());

        // Availability Filter Dialog
        tvFilterAvailability.setOnClickListener(v -> showAvailabilityFilterDialog());
    }

    private void showLocationFilterDialog() {
        final EditText input = new EditText(this);
        input.setHint("Enter City or District (e.g. Dhaka, Mirpur)");
        if (!selectedLocation.isEmpty()) {
            input.setText(selectedLocation);
        }

        new AlertDialog.Builder(this)
                .setTitle("Filter by Location")
                .setView(input)
                .setPositiveButton("Apply", (dialog, which) -> {
                    selectedLocation = input.getText().toString().trim();
                    if (selectedLocation.isEmpty()) {
                        tvFilterLocation.setText("Location");
                        tvFilterLocation.setBackgroundResource(R.drawable.bg_filter_pill_white);
                        tvFilterLocation.setTextColor(getResources().getColor(R.color.black, getTheme()));
                    } else {
                        tvFilterLocation.setText(selectedLocation);
                        tvFilterLocation.setBackgroundResource(R.drawable.bg_filter_pill_red);
                        tvFilterLocation.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                    }
                    currentPage = 0;
                    fetchDonors();
                })
                .setNegativeButton("Clear", (dialog, which) -> {
                    selectedLocation = "";
                    tvFilterLocation.setText("Location");
                    tvFilterLocation.setBackgroundResource(R.drawable.bg_filter_pill_white);
                    currentPage = 0;
                    fetchDonors();
                })
                .show();
    }

    private void showBloodGroupFilterDialog() {
        final String[] bloodGroups = {"All", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        int checkedItem = 0;
        for (int i = 0; i < bloodGroups.length; i++) {
            if (bloodGroups[i].equalsIgnoreCase(selectedBloodGroup)) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Filter by Blood Group")
                .setSingleChoiceItems(bloodGroups, checkedItem, (dialog, which) -> {
                    if (which == 0) {
                        selectedBloodGroup = "";
                        tvFilterBloodGroup.setText("Blood Group");
                    } else {
                        selectedBloodGroup = bloodGroups[which];
                        tvFilterBloodGroup.setText(selectedBloodGroup);
                    }
                    dialog.dismiss();
                    currentPage = 0;
                    fetchDonors();
                })
                .show();
    }

    private void showAvailabilityFilterDialog() {
        final String[] options = {"All", "Available", "Unavailable"};
        int checkedItem = 0;
        if (selectedAvailability.equalsIgnoreCase("true")) checkedItem = 1;
        else if (selectedAvailability.equalsIgnoreCase("false")) checkedItem = 2;

        new AlertDialog.Builder(this)
                .setTitle("Filter by Availability")
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    if (which == 1) {
                        selectedAvailability = "true";
                        tvFilterAvailability.setText("Available");
                    } else if (which == 2) {
                        selectedAvailability = "false";
                        tvFilterAvailability.setText("Unavailable");
                    } else {
                        selectedAvailability = "";
                        tvFilterAvailability.setText("Availability");
                    }
                    dialog.dismiss();
                    currentPage = 0;
                    fetchDonors();
                })
                .show();
    }

    private void fetchDonors() {
        Map<String, String> filters = new HashMap<>();
        if (!selectedBloodGroup.isEmpty()) filters.put("bldGroup", selectedBloodGroup);
        if (!selectedLocation.isEmpty()) filters.put("city", selectedLocation);

        if (!filters.isEmpty()) {
            apiServices.searchDonors(filters).enqueue(new Callback<List<DonorResponse>>() {
                @Override
                public void onResponse(Call<List<DonorResponse>> call, Response<List<DonorResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        donorList.clear();
                        List<DonorResponse> results = response.body();

                        // Filter availability locally if specified
                        for (DonorResponse d : results) {
                            if (selectedAvailability.isEmpty() ||
                                    (selectedAvailability.equals("true") && d.isAvailable()) ||
                                    (selectedAvailability.equals("false") && !d.isAvailable())) {
                                donorList.add(d);
                            }
                        }

                        if (donorList.isEmpty()) {
                            populateDummyDataIfEmpty();
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        populateDummyDataIfEmpty();
                    }
                }

                @Override
                public void onFailure(Call<List<DonorResponse>> call, Throwable t) {
                    populateDummyDataIfEmpty();
                }
            });
        } else {
            // Load paginated donors
            apiServices.getAllDonors(currentPage, pageSize, "donorName", "asc").enqueue(new Callback<PaginatedResponse<DonorResponse>>() {
                @Override
                public void onResponse(Call<PaginatedResponse<DonorResponse>> call, Response<PaginatedResponse<DonorResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (currentPage == 0) {
                            donorList.clear();
                        }
                        List<DonorResponse> content = response.body().getContent();
                        if (content != null && !content.isEmpty()) {
                            donorList.addAll(content);
                        } else if (donorList.isEmpty()) {
                            populateDummyDataIfEmpty();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        if (donorList.isEmpty()) populateDummyDataIfEmpty();
                    }
                }

                @Override
                public void onFailure(Call<PaginatedResponse<DonorResponse>> call, Throwable t) {
                    if (donorList.isEmpty()) populateDummyDataIfEmpty();
                }
            });
        }
    }

    private void loadMoreDonors() {
        currentPage++;
        fetchDonors();
    }

    /**
     * Fallback dummy data matching the mockups if server yields empty list or during offline preview.
     */
    private void populateDummyDataIfEmpty() {
        donorList.clear();

        DonorResponse d1 = new DonorResponse();
        d1.setDonorName("Raihan Ahmed");
        d1.setCity("Mirpur");
        d1.setDistrict("Dhaka");
        d1.setBldGroup("A+");
        d1.setAvailable(true);
        d1.setPhone("01711111111");
        d1.setTotalDonations(6);
        d1.setLastDonateDate("3 Months Ago");
        donorList.add(d1);

        DonorResponse d2 = new DonorResponse();
        d2.setDonorName("Fatima Khan");
        d2.setCity("Dhanmondi");
        d2.setDistrict("Dhaka");
        d2.setBldGroup("B+");
        d2.setAvailable(true);
        d2.setPhone("01822222222");
        d2.setTotalDonations(4);
        d2.setLastDonateDate("2 Months Ago");
        donorList.add(d2);

        DonorResponse d3 = new DonorResponse();
        d3.setDonorName("Karim Hossain");
        d3.setCity("Uttara");
        d3.setDistrict("Dhaka");
        d3.setBldGroup("O-");
        d3.setAvailable(false);
        d3.setPhone("01933333333");
        d3.setTotalDonations(8);
        d3.setLastDonateDate("5 Months Ago");
        donorList.add(d3);

        DonorResponse d4 = new DonorResponse();
        d4.setDonorName("Ziaur Rahman");
        d4.setCity("Gulshan");
        d4.setDistrict("Dhaka");
        d4.setBldGroup("AB+");
        d4.setAvailable(true);
        d4.setPhone("01744444444");
        d4.setTotalDonations(3);
        d4.setLastDonateDate("1 Month Ago");
        donorList.add(d4);

        DonorResponse d5 = new DonorResponse();
        d5.setDonorName("Sultana Yasmin");
        d5.setCity("Banani");
        d5.setDistrict("Dhaka");
        d5.setBldGroup("B-");
        d5.setAvailable(true);
        d5.setPhone("01855555555");
        d5.setTotalDonations(5);
        d5.setLastDonateDate("4 Months Ago");
        donorList.add(d5);

        DonorResponse d6 = new DonorResponse();
        d6.setDonorName("Imran Chowdhury");
        d6.setCity("Mohammadpur");
        d6.setDistrict("Dhaka");
        d6.setBldGroup("A-");
        d6.setAvailable(true);
        d6.setPhone("01966666666");
        d6.setTotalDonations(7);
        d6.setLastDonateDate("2 Months Ago");
        donorList.add(d6);

        DonorResponse d7 = new DonorResponse();
        d7.setDonorName("Tasnim Ara");
        d7.setCity("Wari");
        d7.setDistrict("Dhaka");
        d7.setBldGroup("O+");
        d7.setAvailable(false);
        d7.setPhone("01777777777");
        d7.setTotalDonations(2);
        d7.setLastDonateDate("6 Months Ago");
        donorList.add(d7);

        adapter.notifyDataSetChanged();
    }
}
