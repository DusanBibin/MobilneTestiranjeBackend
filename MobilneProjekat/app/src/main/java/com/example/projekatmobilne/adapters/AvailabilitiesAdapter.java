package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.model.responseDTO.ReviewDTOResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AvailabilitiesAdapter extends  RecyclerView.Adapter<AvailabilitiesViewHolder>{
    private Context context;
    private List<AvailabilityDTO> dataList;

    public void setSearchList(List<AvailabilityDTO> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }

    public AvailabilitiesAdapter(Context context, List<AvailabilityDTO> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public AvailabilitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availability_add, parent, false);
        return new AvailabilitiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailabilitiesViewHolder holder, int position) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        AvailabilityDTO item = dataList.get(position);
        holder.txtPrice.setText("Price: " + item.getPrice().toString());
        holder.txtIsPerGuest.setText("Is per guest price: " + item.getPricePerGuest().toString());
        holder.txtDateRange.setText("Date range: " + item.getStartDate().format(formatter) + " " + item.getEndDate().format(formatter));
        holder.txtCancelDate.setText("Cancel date: " + item.getEndDate().format(formatter));

        holder.btnRemoveAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
                notifyItemRemoved(holder.getAdapterPosition());
                System.out.println(dataList.size());
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class AvailabilitiesViewHolder extends RecyclerView.ViewHolder{
    TextView txtDateRange, txtCancelDate, txtPrice, txtIsPerGuest;
    Button btnRemoveAvailability;
    public AvailabilitiesViewHolder(@NonNull View itemView) {
        super(itemView);

        btnRemoveAvailability = itemView.findViewById(R.id.btnRemoveAvailability);
        txtDateRange = itemView.findViewById(R.id.txtAvailabilityDateRange);
        txtCancelDate = itemView.findViewById(R.id.txtAvailabilityCancel);
        txtPrice = itemView.findViewById(R.id.txtAvailabilityUnitPrice);
        txtIsPerGuest = itemView.findViewById(R.id.txtIsPricePerGuest);

    }
}
