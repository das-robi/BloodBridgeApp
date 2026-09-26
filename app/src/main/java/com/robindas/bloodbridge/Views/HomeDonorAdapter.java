package com.robindas.bloodbridge.Views;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.DTO.DonorResponse;
import com.robindas.bloodbridge.R;

import java.util.List;

public class HomeDonorAdapter extends RecyclerView.Adapter<HomeDonorAdapter.ViewHolder> {

    private List<DonorResponse> donors;

    public HomeDonorAdapter(List<DonorResponse> donors) {
        this.donors = donors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donor_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonorResponse donor = donors.get(position);
        holder.tvName.setText(donor.getDonorName() != null ? donor.getDonorName() : "Donor");
        
        String loc = "";
        if (donor.getCity() != null) loc += donor.getCity();
        if (donor.getDistrict() != null && !donor.getDistrict().isEmpty()) {
            if (!loc.isEmpty()) loc += ", ";
            loc += donor.getDistrict();
        }
        holder.tvLoc.setText(loc.isEmpty() ? "Location" : loc);
        holder.tvBlood.setText(donor.getBldGroup() != null ? donor.getBldGroup() : "--");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DonorProfileActivity.class);
            intent.putExtra("donor", donor);
            v.getContext().startActivity(intent);
        });

        if (holder.fabCall != null) {
            holder.fabCall.setOnClickListener(v -> {
                if (donor.getPhone() != null && !donor.getPhone().isEmpty()) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + donor.getPhone()));
                    v.getContext().startActivity(dialIntent);
                } else {
                    Intent intent = new Intent(v.getContext(), DonorProfileActivity.class);
                    intent.putExtra("donor", donor);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLoc, tvBlood;
        View fabCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDonorNameHome);
            tvLoc = itemView.findViewById(R.id.tvDonorLocHome);
            tvBlood = itemView.findViewById(R.id.tvDonorBloodHome);
            fabCall = itemView.findViewById(R.id.fabCallDonor);
        }
    }
}
