package com.robindas.bloodbridge.Views;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for creating a donor profile.
 */
public class CreateDonorActivity extends AppCompatActivity {

    private static final String TAG = "CreateDonorActivity";

    private EditText etBloodGroup, etCity, etDistrict, etPhone, etLastDonateDate;
    private CheckBox cbAvailable;
    private Button btnSubmit;
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: CreateDonorActivity started");
        setContentView(R.layout.activity_create_donor);

        etBloodGroup = findViewById(R.id.etBloodGroup);
        etCity = findViewById(R.id.etCity);
        etDistrict = findViewById(R.id.etDistrict);
        etPhone = findViewById(R.id.etPhone);
        etLastDonateDate = findViewById(R.id.etLastDonateDate);
        cbAvailable = findViewById(R.id.cbAvailable);
        btnSubmit = findViewById(R.id.btnSubmitDonor);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        etLastDonateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDonor();
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
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format(Locale.US, "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    etLastDonateDate.setText(date);
                }, year, month, day);

        // Prevent future dates to avoid @Past validation error
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    /**
     * Creates a new donor profile by calling the API after validation.
     */
    private void createDonor() {
        String blood = etBloodGroup.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String lastDate = etLastDonateDate.getText().toString().trim();
        boolean available = cbAvailable.isChecked();

        if (blood.isEmpty() || city.isEmpty() || district.isEmpty() || phone.isEmpty() || lastDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "createDonor: Attempting to create donor profile");
        DonorRequest request = new DonorRequest(blood, city, district, phone, lastDate, available);

        apiServices.createDonor(request).enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Donor profile created successfully");
                    Toast.makeText(CreateDonorActivity.this, "Donor Profile Created!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to Donor Profile Activity
                    Intent intent = new Intent(CreateDonorActivity.this, UserDonorProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "onResponse: Failed to create donor profile. Code: " + response.code());
                    Toast.makeText(CreateDonorActivity.this, "Failed to create profile: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DonorResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error creating donor profile", t);
                Toast.makeText(CreateDonorActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
