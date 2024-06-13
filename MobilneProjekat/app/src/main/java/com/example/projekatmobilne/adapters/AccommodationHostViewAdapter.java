package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;

public class AccommodationHostViewAdapter {
    private Context context;
}


class AccommodationHostViewHolder extends RecyclerView.ViewHolder{
    TextView txtName, txtAddress;
    public AccommodationHostViewHolder(@NonNull View itemView) {
        super(itemView);

        txtAddress = itemView.findViewById(R.id.txtAddressAccommodationItem);
        txtName = itemView.findViewById(R.id.txtNameAccommodationItem);

    }
}

