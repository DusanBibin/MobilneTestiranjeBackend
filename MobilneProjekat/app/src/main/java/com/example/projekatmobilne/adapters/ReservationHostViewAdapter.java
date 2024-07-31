 package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.AdapterItems.AccommodationHostItem;
import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.Enum.ReservationStatus;
import com.example.projekatmobilne.model.requestDTO.ReservationDTO;

import java.util.List;

public class ReservationHostViewAdapter extends RecyclerView.Adapter<ReservationHostViewHolder>{
    private Context context;
    private List<ReservationDTO> dataList;


    public ReservationHostViewAdapter(Context context, List<ReservationDTO> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ReservationHostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_host_item, parent, false);
        return new ReservationHostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationHostViewHolder holder, int position) {
        ReservationDTO reservation = dataList.get(position);
        holder.txtAccommodationName.setText(reservation.getAccommodationName());
        holder.txtPrice.setText("Price: " + reservation.getPrice().toString());
        holder.txtDateRange.setText("Date range: " + reservation.getReservationStartDate() + " - " + reservation.getReservationEndDate());
        holder.txtRequestStatus.setText("Status: " + reservation.getStatus().toString());
        if(!ReservationStatus.PENDING.equals(reservation.getStatus())) holder.txtRejectReason.setText("Reason: " + reservation.getReason());
    }

    @Override
    public int getItemCount()  {
        return dataList.size();
    }
}


class ReservationHostViewHolder extends RecyclerView.ViewHolder{
    TextView txtAccommodationName, txtDateRange, txtPrice, txtRequestStatus, txtRejectReason;

    public ReservationHostViewHolder(@NonNull View itemView) {
        super(itemView);
        txtAccommodationName = itemView.findViewById(R.id.txtNameAccommodationItem);
        txtDateRange = itemView.findViewById(R.id.txtDateRange);
        txtPrice = itemView.findViewById(R.id.txtPrice);
        txtRequestStatus = itemView.findViewById(R.id.txtRequestStatus);
        txtRejectReason = itemView.findViewById(R.id.txtRejectReason);
    }
}
