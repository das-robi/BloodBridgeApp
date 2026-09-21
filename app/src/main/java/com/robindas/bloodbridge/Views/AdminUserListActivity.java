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
import com.robindas.bloodbridge.DTO.PaginatedResponse;
import com.robindas.bloodbridge.DTO.UserResponse;
import com.robindas.bloodbridge.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for Admins to view all users with pagination and sorting.
 */
public class AdminUserListActivity extends AppCompatActivity {

    private static final String TAG = "AdminUserListActivity";
    
    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private List<UserResponse> userList = new ArrayList<>();
    private APIServices apiServices;

    private int currentPage = 0;
    private final int pageSize = 10;
    private String currentSortBy = "userName";
    private String currentOrder = "asc";

    private Button btnNext, btnPrev;
    private TextView tvPageIndicator;
    private Spinner spinnerSort;
    private ToggleButton btnToggleOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        rvUsers = findViewById(R.id.rvAdminUsers);
        btnNext = findViewById(R.id.btnNextPage);
        btnPrev = findViewById(R.id.btnPrevPage);
        tvPageIndicator = findViewById(R.id.tvPageIndicator);
        spinnerSort = findViewById(R.id.spinnerSortBy);
        btnToggleOrder = findViewById(R.id.btnSortOrder);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userList);
        rvUsers.setAdapter(adapter);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        setupSorting();
        setupPagination();
        
        loadUsers();
    }

    private void setupSorting() {

        String[] sortOptions = {"userName", "userEmail", "userId"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortBy = sortOptions[position];
                currentPage = 0; // Reset to first page when sort changes
                loadUsers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnToggleOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            currentOrder = isChecked ? "desc" : "asc";
            currentPage = 0;
            loadUsers();

        });
    }

    private void setupPagination() {

        btnNext.setOnClickListener(v -> {

            currentPage++;
            loadUsers();

        });

        btnPrev.setOnClickListener(v -> {

            if (currentPage > 0) {
                currentPage--;
                loadUsers();
            }

        });

    }

    private void loadUsers() {

        Log.d(TAG, "loadUsers: Fetching page " + currentPage + " sorted by " + currentSortBy + " " + currentOrder);
        
        apiServices.getAllUsers(currentPage, pageSize, currentSortBy, currentOrder).enqueue(new Callback<PaginatedResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<UserResponse>> call, Response<PaginatedResponse<UserResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    PaginatedResponse<UserResponse> paginatedData = response.body();
                    
                    userList.clear();
                    userList.addAll(paginatedData.getContent());
                    adapter.notifyDataSetChanged();

                    tvPageIndicator.setText("Page " + (currentPage + 1) + " of " + paginatedData.getTotalPages());
                    
                    btnPrev.setEnabled(currentPage > 0);
                    btnNext.setEnabled(currentPage < paginatedData.getTotalPages() - 1);

                } else {

                    Toast.makeText(AdminUserListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<UserResponse>> call, Throwable t) {

                Log.e(TAG, "onFailure: API error", t);
                Toast.makeText(AdminUserListActivity.this, "Network error", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
