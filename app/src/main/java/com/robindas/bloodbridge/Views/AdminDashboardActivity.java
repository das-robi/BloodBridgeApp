package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.robindas.bloodbridge.R;

/**
 * Main entry point for Admin-only features.
 */
public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnUsers = findViewById(R.id.btnManageUsers);
        Button btnDonors = findViewById(R.id.btnManageDonorsAdmin);
        Button btnRequests = findViewById(R.id.btnAllBloodRequestsAdmin);

        btnUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminUserListActivity.class);
            startActivity(intent);
        });

        btnDonors.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminDonorListActivity.class);
            startActivity(intent);
        });

        btnRequests.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminBloodRequestListActivity.class);
            startActivity(intent);
        });
    }
}
