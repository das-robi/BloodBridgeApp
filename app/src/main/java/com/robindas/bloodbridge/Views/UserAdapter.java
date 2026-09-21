package com.robindas.bloodbridge.Views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robindas.bloodbridge.DTO.UserResponse;
import com.robindas.bloodbridge.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserResponse> users;

    public UserAdapter(List<UserResponse> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserResponse user = users.get(position);
        holder.tvName.setText(user.getUserName());
        holder.tvEmail.setText(user.getUserEmail());
        holder.tvRole.setText("Role: " + user.getRole());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminUserName);
            tvEmail = itemView.findViewById(R.id.tvAdminUserEmail);
            tvRole = itemView.findViewById(R.id.tvAdminUserRole);
        }
    }
}
