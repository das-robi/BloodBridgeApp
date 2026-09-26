package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.R;

import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    private List<DonorResponse> donors;
    private OnDonorClickListener clickListener;

    public interface OnDonorClickListener {
        void onDonorClick(DonorResponse donor);
    }

    public DonorAdapter(List<DonorResponse> donors) {
        this.donors = donors;
    }

    public DonorAdapter(List<DonorResponse> donors, OnDonorClickListener clickListener) {
        this.donors = donors;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonorResponse donor = donors.get(position);
        
        holder.tvName.setText(donor.getDonorName() != null ? donor.getDonorName() : "Unknown Donor");
        holder.tvBlood.setText(donor.getBldGroup() != null ? donor.getBldGroup() : "--");

        String loc = "";
        if (donor.getCity() != null && !donor.getCity().isEmpty()) {
            loc = donor.getCity();
        }
        if (donor.getDistrict() != null && !donor.getDistrict().isEmpty()) {
            if (!loc.isEmpty()) loc += ", ";
            loc += donor.getDistrict();
        }
        if (loc.isEmpty()) loc = "Location not specified";
        holder.tvLocation.setText(loc);

        if (donor.isAvailable()) {
            holder.tvStatusDot.setTextColor(Color.parseColor("#10B981"));
            holder.tvStatus.setText("Available");
            holder.tvStatus.setTextColor(Color.parseColor("#10B981"));
        } else {
            holder.tvStatusDot.setTextColor(Color.parseColor("#9CA3AF"));
            holder.tvStatus.setText("Unavailable");
            holder.tvStatus.setTextColor(Color.parseColor("#9CA3AF"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDonorClick(donor);
            } else {
                Intent intent = new Intent(v.getContext(), DonorProfileActivity.class);
                intent.putExtra("donor", donor);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBlood, tvLocation, tvStatusDot, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDonorItemName);
            tvBlood = itemView.findViewById(R.id.tvDonorItemBlood);
            tvLocation = itemView.findViewById(R.id.tvDonorItemLocation);
            tvStatusDot = itemView.findViewById(R.id.tvDonorItemStatusDot);
            tvStatus = itemView.findViewById(R.id.tvDonorItemStatus);
        }
    }
}
