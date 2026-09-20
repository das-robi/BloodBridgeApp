package com.robindas.bloodbridge.Views;

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

    public DonorAdapter(List<DonorResponse> donors) {
        this.donors = donors;
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
        holder.tvName.setText(donor.getDonorName());
        holder.tvBlood.setText(donor.getBldGroup());
        holder.tvLocation.setText(donor.getCity() + ", " + donor.getDistrict());
        holder.tvPhone.setText(donor.getPhone());
    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBlood, tvLocation, tvPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDonorItemName);
            tvBlood = itemView.findViewById(R.id.tvDonorItemBlood);
            tvLocation = itemView.findViewById(R.id.tvDonorItemLocation);
            tvPhone = itemView.findViewById(R.id.tvDonorItemPhone);
        }
    }
}
