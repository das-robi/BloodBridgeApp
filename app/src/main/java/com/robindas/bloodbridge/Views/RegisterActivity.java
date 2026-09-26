package com.robindas.bloodbridge.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.RegisterRequest;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for user registration.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //Widgets
    private EditText etUsername;
    private EditText etUserEmail;
    private EditText etPassword;
    private Button regiBtn;

    //API
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: RegisterActivity started");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.userName);
        etUserEmail = findViewById(R.id.useremail);
        etPassword = findViewById(R.id.passWord);
        regiBtn = findViewById(R.id.lgnBtn);

        findViewById(R.id.ivBackReg).setOnClickListener(v -> finish());
        findViewById(R.id.tvSignIn).setOnClickListener(v -> finish());

        regiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registration();
            }
        });


        apiServices = RetrofitClient.getRetrofitInstance(this)
                .create(APIServices.class);
    }

    /**
     * Processes user registration by validating input and calling the registration API.
     */
    private void Registration() {

        String username = etUsername.getText().toString().trim();
        String useremail = etUserEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()){
            etUsername.setError("Enter name");
            return;
        }
        if (useremail.isEmpty()){
            etUserEmail.setError("Enter email");
            return;
        }
        if (password.isEmpty()){
            etPassword.setError("Enter password");
            return;
        }

        Log.d(TAG, "Registration: Attempting register for " + username);
        regiBtn.setEnabled(false);

        RegisterRequest request = new RegisterRequest(username, useremail, password);

        apiServices.register(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                regiBtn.setEnabled(true);

                if (response.isSuccessful()){
                    Log.d(TAG, "onResponse: Register Success: " + response.body());
                    Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                    
                    // After register, redirect to Home (assuming login happens automatically or via token)
                    // Usually you'd log them in or send to Login.
                    // For now, let's send to Login for security.
                    finish();
                }
                else {
                    Log.e(TAG, "onResponse: Register failed. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(RegisterActivity.this, "Register failed try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

                regiBtn.setEnabled(true);
                Log.e(TAG, "onFailure: Register Failed", throwable);
                Toast.makeText(RegisterActivity.this, "Network error " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}