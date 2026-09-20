package com.robindas.bloodbridge.Views;

import android.content.Intent;
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
import com.robindas.bloodbridge.API.TokenManager;
import com.robindas.bloodbridge.DTO.LoginRequest;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for user login.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //Widgets
    private EditText etUsername;
    private EditText etUserEmail;
    private EditText etPassword;
    private Button loginBtn;

    //API
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: LoginActivity started");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        etUsername = findViewById(R.id.userName);
        etUserEmail = findViewById(R.id.useremail);
        etPassword = findViewById(R.id.passWord);
        loginBtn = findViewById(R.id.lgnBtn);


        apiServices = RetrofitClient.getRetrofitInstance(this)
                .create(APIServices.class);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginProcess();
            }});


    }

    /**
     * Processes user login by validating input and calling the login API.
     */
    private void LoginProcess() {

        String username = etUsername.getText().toString().trim();
        String useremail = etUserEmail.getText().toString().trim();
        String passwords = etPassword.getText().toString().trim();

        //check empty value
        if (username.isEmpty()){
            etUsername.setError("Enter username");
            return;
        }

        if (useremail.isEmpty()){
            etUserEmail.setError("Enter email");
            return;
        }

        if (passwords.isEmpty()){
            etPassword.setError("Enter password");
            return;
        }

        Log.d(TAG, "LoginProcess: Attempting login for " + username);
        LoginRequest request = new LoginRequest(username, useremail, passwords);

        apiServices.login(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                loginBtn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null){

                    String token = response.body();

                    TokenManager tokenManager = new TokenManager(LoginActivity.this);
                    //save token
                    tokenManager.saveToken(token);
                    //Check Token save
                    Log.d(TAG, "onResponse: JWT_Token saved: " + tokenManager.getToken());

                    Toast.makeText(LoginActivity.this, "login successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
                    startActivity(intent);

                    finish();
                }
                else {
                    Log.e(TAG, "onResponse: Login Failed. Code: " + response.code());
                    Toast.makeText(LoginActivity.this, "login Failed " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

                loginBtn.setEnabled(true);
                Log.e(TAG, "onFailure: Login request failed", throwable);
                Toast.makeText(LoginActivity.this, "Network Error! Check Your Connection" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}