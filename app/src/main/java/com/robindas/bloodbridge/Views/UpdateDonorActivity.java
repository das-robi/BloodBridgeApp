package com.robindas.bloodbridge.Views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.DonorRequest;
import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.R;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for updating the user's donor profile, with blood group locked (non-editable).
 */
public class UpdateDonorActivity extends AppCompatActivity {

    private static final String TAG = "UpdateDonorActivity";

    private View btnBack;
    private EditText etBloodGroup, etCity, etDistrict, etPhone, etLastDonateDate;
    private CheckBox cbAvailable;
    private Button btnUpdate;
    private APIServices apiServices;
    private String originalBloodGroup = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: UpdateDonorActivity started");
        setContentView(R.layout.activity_update_donor);

        btnBack = findViewById(R.id.btnBackUpdate);
        etBloodGroup = findViewById(R.id.etUpdateBloodGroup);
        etCity = findViewById(R.id.etUpdateCity);
        etDistrict = findViewById(R.id.etUpdateDistrict);
        etPhone = findViewById(R.id.etUpdatePhone);
        etLastDonateDate = findViewById(R.id.etUpdateLastDonateDate);
        cbAvailable = findViewById(R.id.cbUpdateAvailable);
        btnUpdate = findViewById(R.id.btnUpdateDonor);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Lock blood group field
        etBloodGroup.setEnabled(false);
        etBloodGroup.setFocusable(false);
        etBloodGroup.setClickable(false);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        loadCurrentData();

        etLastDonateDate.setOnClickListener(v -> showDatePicker());

        btnUpdate.setOnClickListener(v -> updateDonor());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", selectedYear, monthOfYear + 1, dayOfMonth);
                    etLastDonateDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadCurrentData() {
        Log.d(TAG, "loadCurrentData: Fetching current donor profile");
        apiServices.getMyDonorProfile().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DonorResponse donor = response.body();
                    originalBloodGroup = donor.getBldGroup() != null ? donor.getBldGroup() : "";
                    etBloodGroup.setText(originalBloodGroup);
                    etCity.setText(donor.getCity() != null ? donor.getCity() : "");
                    etDistrict.setText(donor.getDistrict() != null ? donor.getDistrict() : "");
                    etPhone.setText(donor.getPhone() != null ? donor.getPhone() : "");
                    etLastDonateDate.setText(donor.getLastDonateDate() != null ? donor.getLastDonateDate() : "");
                    cbAvailable.setChecked(donor.isAvailable());
                } else {
                    Log.e(TAG, "onResponse: Failed to load donor profile. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error loading donor profile", t);
                Toast.makeText(UpdateDonorActivity.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDonor() {
        String blood = originalBloodGroup.isEmpty() ? etBloodGroup.getText().toString().trim() : originalBloodGroup;
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String lastDate = etLastDonateDate.getText().toString().trim();
        boolean available = cbAvailable.isChecked();

        Log.d(TAG, "updateDonor: Updating donor profile without changing blood group");
        DonorRequest request = new DonorRequest(blood, city, district, phone, lastDate, available);

        apiServices.updateProfile(request).enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Donor profile updated successfully");
                    Toast.makeText(UpdateDonorActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "onResponse: Update failed. Code: " + response.code());
                    Toast.makeText(UpdateDonorActivity.this, "Update Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error updating donor profile", t);
                Toast.makeText(UpdateDonorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
