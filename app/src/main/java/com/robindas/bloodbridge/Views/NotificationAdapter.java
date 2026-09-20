package com.robindas.bloodbridge.Views;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.DTO.NotificationResponse;
import com.robindas.bloodbridge.R;

import java.util.List;

/**
 * Adapter for displaying a list of notifications in a RecyclerView.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationResponse> notifications;
    private OnNotificationClickListener listener;

    /**
     * Interface for handling notification click events.
     */
    public interface OnNotificationClickListener {
        /**
         * Called when a notification item is clicked.
         * @param notification The clicked notification.
         */
        void onNotificationClick(NotificationResponse notification);
    }

    public NotificationAdapter(List<NotificationResponse> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationResponse notification = notifications.get(position);
        holder.tvMessage.setText(notification.getNotTitle() + ": " + notification.getNotMessage());
        
        if (notification.getUsernameDonor() != null) {
            holder.tvDate.setText("Donor: " + notification.getUsernameDonor() + " (" + notification.getBloodgrp() + ")");
        } else {
            holder.tvDate.setText("");
        }

        if (!notification.isRead()) {
            holder.rootView.setBackgroundColor(Color.parseColor("#F0F0F0"));
        } else {
            holder.rootView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> listener.onNotificationClick(notification));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvDate;
        LinearLayout rootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvDate = itemView.findViewById(R.id.tvNotificationDate);
            rootView = itemView.findViewById(R.id.llNotificationRoot);
        }
    }
}
