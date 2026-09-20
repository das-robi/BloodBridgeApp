package com.robindas.bloodbridge.Views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.DTO.BloodRequestResponse;
import com.robindas.bloodbridge.R;

import java.util.List;

/**
 * Adapter for displaying a list of blood requests in a RecyclerView.
 */
public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.ViewHolder> {

    private List<BloodRequestResponse> requestList;
    private OnItemClickListener listener;

    /**
     * Interface for handling item click events.
     */
    public interface OnItemClickListener {
        /**
         * Called when a blood request item is clicked.
         * @param request The clicked blood request.
         */
        void onItemClick(BloodRequestResponse request);
    }

    public BloodRequestAdapter(List<BloodRequestResponse> requestList, OnItemClickListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodRequestResponse request = requestList.get(position);
        holder.tvPatientName.setText(request.getPatientName());
        holder.tvBloodGroup.setText(request.getBldGroup());
        holder.tvLocation.setText(request.getCity() + ", " + request.getDistrict());
        holder.tvStatus.setText("Status: " + request.getStatus());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(request));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvBloodGroup, tvLocation, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvBloodGroup = itemView.findViewById(R.id.tvBloodGroup);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
