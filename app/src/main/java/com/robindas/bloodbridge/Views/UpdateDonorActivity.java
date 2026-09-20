package com.robindas.bloodbridge.Views;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

public class UpdateDonorActivity extends AppCompatActivity {

    private EditText etBloodGroup, etCity, etDistrict, etPhone, etLastDonateDate;
    private CheckBox cbAvailable;
    private Button btnUpdate;
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void loadCurrentData() {
        apiServices.getMyDonorProfile().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    DonorResponse donor = response.body();
//                    etName.setText(donor.getDonorName());
                    etBloodGroup.setText(donor.getBldGroup());
                    etCity.setText(donor.getCity());
                    etDistrict.setText(donor.getDistrict());
                    etPhone.setText(donor.getPhone());
                    etLastDonateDate.setText(donor.getLastDonateDate());
                    cbAvailable.setChecked(donor.isAvailable());

                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Toast.makeText(UpdateDonorActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDonor() {

//        String name = etName.getText().toString().trim();
        String blood = etBloodGroup.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String lastDate = etLastDonateDate.getText().toString().trim();
        boolean available = cbAvailable.isChecked();

        DonorRequest request = new DonorRequest(blood, city, district, phone, lastDate, available);

        apiServices.updateProfile(request).enqueue(new Callback<DonorResponse>() {

            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(UpdateDonorActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(UpdateDonorActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Toast.makeText(UpdateDonorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
