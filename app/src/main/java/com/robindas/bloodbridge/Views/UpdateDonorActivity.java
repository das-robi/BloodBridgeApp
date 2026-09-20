package com.robindas.bloodbridge.Views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
 * Activity for updating the user's donor profile.
 */
public class UpdateDonorActivity extends AppCompatActivity {

    private static final String TAG = "UpdateDonorActivity";

    private EditText etBloodGroup, etCity, etDistrict, etPhone, etLastDonateDate;
    private CheckBox cbAvailable;
    private Button btnUpdate;
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: UpdateDonorActivity started");
        setContentView(R.layout.activity_update_donor);

//        etName = findViewById(R.id.etUpdateName);
        etBloodGroup = findViewById(R.id.etUpdateBloodGroup);
        etCity = findViewById(R.id.etUpdateCity);
        etDistrict = findViewById(R.id.etUpdateDistrict);
        etPhone = findViewById(R.id.etUpdatePhone);
        etLastDonateDate = findViewById(R.id.etUpdateLastDonateDate);
        cbAvailable = findViewById(R.id.cbUpdateAvailable);
        btnUpdate = findViewById(R.id.btnUpdateDonor);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        loadCurrentData();

        etLastDonateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDonor();
            }
        });
    }

    /**
     * Shows a DatePicker dialog to select the last donation date.
     */
    private void showDatePicker() {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        etLastDonateDate.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();

    }

    /**
     * Loads the current donor profile data into the form.
     */
    private void loadCurrentData() {
        Log.d(TAG, "loadCurrentData: Fetching current donor profile");
        apiServices.getMyDonorProfile().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Donor profile data loaded");
                    DonorResponse donor = response.body();
//                    etName.setText(donor.getDonorName());
                    etBloodGroup.setText(donor.getBldGroup());
                    etCity.setText(donor.getCity());
                    etDistrict.setText(donor.getDistrict());
                    etPhone.setText(donor.getPhone());
                    etLastDonateDate.setText(donor.getLastDonateDate());
                    cbAvailable.setChecked(donor.isAvailable());

                } else {
                    Log.e(TAG, "onResponse: Failed to load donor profile. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error loading donor profile", t);
                Toast.makeText(UpdateDonorActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the donor profile by calling the API.
     */
    private void updateDonor() {

//        String name = etName.getText().toString().trim();
        String blood = etBloodGroup.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String lastDate = etLastDonateDate.getText().toString().trim();
        boolean available = cbAvailable.isChecked();

        Log.d(TAG, "updateDonor: Attempting to update donor profile");
        DonorRequest request = new DonorRequest(blood, city, district, phone, lastDate, available);

        apiServices.updateProfile(request).enqueue(new Callback<DonorResponse>() {

            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {

                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Donor profile updated successfully");
                    Toast.makeText(UpdateDonorActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Log.e(TAG, "onResponse: Update failed. Code: " + response.code());
                    Toast.makeText(UpdateDonorActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
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
