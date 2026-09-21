package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.API.TokenManager;
import com.robindas.bloodbridge.Model.UserProfile;
import com.robindas.bloodbridge.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for the Splash/Landing screen.
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private TokenManager tokenManager;
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();

        // If token exists, try to skip the landing screen and go to the appropriate dashboard
        if (token != null && !token.isEmpty()) {
            Log.d(TAG, "onCreate: Token found, checking profile");
            apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);
            checkSessionAndRedirect();
        } else {
            Log.d(TAG, "onCreate: No token found, showing landing screen");
            showLandingScreen();
        }
    }

    private void showLandingScreen() {
        setContentView(R.layout.activity_splash);

        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v -> {

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkSessionAndRedirect() {

        apiServices.getProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {

                if (response.isSuccessful() && response.body() != null) {

                    UserProfile profile = response.body();
                    String role = profile.getRole();
                    Log.d(TAG, "onResponse: Session valid. Role: " + role);

                    Intent intent;
                    if ("ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role)) {
                        intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, UserProfileActivity.class);
                    }
                    startActivity(intent);
                    finish();

                }
                else {

                    Log.d(TAG, "onResponse: Session invalid or expired. Showing landing.");
                    tokenManager.clearToken();
                    showLandingScreen();

                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "onFailure: Network error checking session", t);
                // On network error, better to stay on landing screen or go to main
                showLandingScreen();
            }
        });
    }
}
