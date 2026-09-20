package com.robindas.bloodbridge.Views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.DTO.NotificationResponse;
import com.robindas.bloodbridge.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for displaying and managing user notifications.
 */
public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<NotificationResponse> notificationList = new ArrayList<>();
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: NotificationActivity started");
        setContentView(R.layout.activity_notification);

        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(notificationList, notification -> {
            if (!notification.isRead()) {
                markAsRead(notification);
            }
        });
        rvNotifications.setAdapter(adapter);

        apiServices = RetrofitClient.getRetrofitInstance(this).create(APIServices.class);

        loadNotifications();
    }

    /**
     * Loads the user's notifications from the API.
     */
    private void loadNotifications() {
        Log.d(TAG, "loadNotifications: Fetching notifications");
        apiServices.getMyNotifications().enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Notifications loaded: " + response.body().size());
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "onResponse: Failed to load notifications. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error loading notifications", t);
                Toast.makeText(NotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Marks a notification as read by calling the API.
     * @param notification The notification to mark as read.
     */
    private void markAsRead(NotificationResponse notification) {
        Log.d(TAG, "markAsRead: Marking notification ID " + notification.getId() + " as read");
        apiServices.markNotificationAsRead(notification.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Notification marked as read");
                    notification.setRead(true);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "onResponse: Failed to mark notification as read. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: Error marking notification as read", t);
                // Ignore failure
            }
        });
    }
}
