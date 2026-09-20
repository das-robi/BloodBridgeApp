package com.robindas.bloodbridge.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.BloodRequestRequest;
import com.robindas.bloodbridge.DTO.BloodRequestResponse;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for creating a new blood request.
 */
public class CreateBloodRequestActivity extends AppCompatActivity {

    private static final String TAG = "CreateBloodRequestActivity";

    private EditText etPatientName, etBloodGroup, etCity, etDistrict, etHospital, etUnit, etDisease;
    private Button btnSubmit;
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: CreateBloodRequestActivity started");
        setContentView(R.layout.activity_create_blood_request);

        etPatientName = findViewById(R.id.etPatientName);
        etBloodGroup = findViewById(R.id.etReqBloodGroup);
        etCity = findViewById(R.id.etReqCity);
        etDistrict = findViewById(R.id.etReqDistrict);
        etHospital = findViewById(R.id.etHospital);
        etUnit = findViewById(R.id.etUnit);
        etDisease = findViewById(R.id.etDisease);
        btnSubmit = findViewById(R.id.btnSubmitRequest);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBloodRequest();
            }
        });
    }

    /**
     * Submits the blood request to the API after validation.
     */
    private void submitBloodRequest() {

        String patient = etPatientName.getText().toString().trim();
        String blood = etBloodGroup.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String hospital = etHospital.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String disease = etDisease.getText().toString().trim();

        if (patient.isEmpty() || blood.isEmpty() || city.isEmpty() || hospital.isEmpty() || unit.isEmpty() || disease.isEmpty()) {

            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;

        }

        // status must be NotBlank as per backend DTO
        String status = "PENDING"; 

        Log.d(TAG, "submitBloodRequest: Submitting request for patient " + patient);
        BloodRequestRequest request = new BloodRequestRequest(patient, blood, city, district, hospital, unit, disease, status);

        apiServices.createBloodRequest(request).enqueue(new Callback<BloodRequestResponse>() {

            @Override
            public void onResponse(Call<BloodRequestResponse> call, Response<BloodRequestResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Blood request submitted successfully");
                    Toast.makeText(CreateBloodRequestActivity.this, "Blood Request Submitted!", Toast.LENGTH_SHORT).show();
                    finish();

                }
                else {
                    Log.e(TAG, "onResponse: Submission failed. Code: " + response.code());
                    Toast.makeText(CreateBloodRequestActivity.this, "Submission Failed: " + response.code(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<BloodRequestResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error submitting blood request", t);
                Toast.makeText(CreateBloodRequestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
