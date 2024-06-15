package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.AdapterItems.AccommodationHostItem;

import java.util.List;

public class AccommodationHostViewAdapter extends RecyclerView.Adapter<AccommodationHostViewHolder> {
    private Context context;
    private List<AccommodationHostItem> dataList;


    public void setList(List<AccommodationHostItem> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }


    public AccommodationHostViewAdapter(Context context, List<AccommodationHostItem> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public AccommodationHostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accommodation_view, parent, false);
        return new AccommodationHostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccommodationHostViewHolder holder, int position) {
        holder.txtAddress.setText(dataList.get(position).getAddress());
        holder.txtName.setText(dataList.get(position).getName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "iksdebro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}


class AccommodationHostViewHolder extends RecyclerView.ViewHolder{
    TextView txtName, txtAddress;
    LinearLayout linearLayout;
    public AccommodationHostViewHolder(@NonNull View itemView) {
        super(itemView);

        linearLayout = itemView.findViewById(R.id.linearLayoutAccommodationHostView);
        txtAddress = itemView.findViewById(R.id.txtAddressAccommodationItem);
        txtName = itemView.findViewById(R.id.txtNameAccommodationItem);
    }
}

