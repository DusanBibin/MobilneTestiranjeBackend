 package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.AccommodationDetailsActivity;
import com.example.projekatmobilne.activities.ReservationDetailsHostActivity;
import com.example.projekatmobilne.adapters.AdapterItems.AccommodationHostItem;
import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.Enum.ReservationStatus;
import com.example.projekatmobilne.model.requestDTO.ReservationDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.AccommodationRequestPreviewDTO;

import java.util.ArrayList;
import java.util.List;

public class ReservationHostViewAdapter extends RecyclerView.Adapter<ReservationHostViewHolder>{

    private Context context;
    private List<ReservationDTO> dataList;


    public ReservationHostViewAdapter(Context context, List<ReservationDTO> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    public void removeData(){
        this.dataList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMoreData(List<ReservationDTO> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
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
        holder.txtCancelDeadline.setText("Cancel deadline: " + reservation.getCancelDeadline());
        holder.txtRequestStatus.setText("Status: " + reservation.getStatus().toString());
        holder.txtUserEmail.setText("User email: " + reservation.getUserEmail());
        if(ReservationStatus.DECLINED.equals(reservation.getStatus())){
            holder.txtRejectReason.setText("Reason: " + reservation.getReason());
            holder.txtRejectReason.setVisibility(View.VISIBLE);
        }


        holder.linearLayoutReservationsHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ReservationDetailsHostActivity.class);
                intent.putExtra("reservationId", dataList.get(holder.getAdapterPosition()).getReservationId());
                intent.putExtra("accommodationId", dataList.get(holder.getAdapterPosition()).getAccommodationId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()  {
        return dataList.size();
    }


}


class ReservationHostViewHolder extends RecyclerView.ViewHolder{
    TextView txtAccommodationName, txtDateRange, txtPrice, txtRequestStatus, txtRejectReason,
            txtUserEmail, txtCancelDeadline;
    LinearLayout linearLayoutReservationsHost;

    public ReservationHostViewHolder(@NonNull View itemView) {
        super(itemView);
        txtAccommodationName = itemView.findViewById(R.id.txtNameAccommodationItem);
        txtDateRange = itemView.findViewById(R.id.txtDateRange);
        txtPrice = itemView.findViewById(R.id.txtPrice);
        txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
        txtRequestStatus = itemView.findViewById(R.id.txtRequestStatus);
        txtRejectReason = itemView.findViewById(R.id.txtRejectReason);
        linearLayoutReservationsHost = itemView.findViewById(R.id.linearLayoutReservationHostView);
        txtCancelDeadline = itemView.findViewById(R.id.txtCancelDeadline);
    }
}
