package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.AccommodationDetailsActivity;
import com.example.projekatmobilne.activities.AccommodationsDifferencesCompareActivity;
import com.example.projekatmobilne.adapters.AdapterItems.AccommodationSearchItem;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.AccommodationRequestPreviewDTO;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

public class AccommodationRequestPreviewAdapter extends RecyclerView.Adapter<AccommodationRequestPreviewViewHolder> {

    private Context context;
    private List<AccommodationRequestPreviewDTO> dataList;

    public void setSearchList(List<AccommodationRequestPreviewDTO> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }

    public List<AccommodationRequestPreviewDTO> getDataList() {
        return dataList;
    }

    public void addMoreData(List<AccommodationRequestPreviewDTO> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void addItem(AccommodationRequestPreviewDTO item){
        this.dataList.add(item);
        notifyItemInserted(dataList.size() - 1);
    }

    public AccommodationRequestPreviewAdapter(Context context, List<AccommodationRequestPreviewDTO> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public AccommodationRequestPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accommodation_request_preview_item, parent, false);
        return new AccommodationRequestPreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccommodationRequestPreviewViewHolder holder, int position) {

        AccommodationRequestPreviewDTO preview = dataList.get(holder.getAdapterPosition());
        holder.txtName.setText(preview.getAccommodationName());
        holder.txtAddress.setText(preview.getAccommodationAddress());
        holder.txtType.setText(preview.getRequestType().toString());
        holder.txtStatus.setText(preview.getStatus().toString());
        if(preview.getExistingAccommodationName() != null && !Objects.equals(preview.getAccommodationName(), "")){
            holder.txtExistingName.setVisibility(View.VISIBLE);
            holder.txtExistingAddress.setVisibility(View.VISIBLE);
            holder.txtExistingAddress.setText(preview.getExistingAddress());
            holder.txtExistingName.setText(preview.getExistingAccommodationName());
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "KLIK RADI", Toast.LENGTH_SHORT).show();
                AccommodationRequestPreviewDTO clickedRequest = dataList.get(holder.getAdapterPosition());

                Intent intent = new Intent(context, AccommodationsDifferencesCompareActivity.class);
                intent.putExtra("requestId", clickedRequest.getRequestId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}


class AccommodationRequestPreviewViewHolder extends RecyclerView.ViewHolder{

    CardView cardView;
    TextView txtName, txtAddress, txtExistingName, txtExistingAddress, txtStatus, txtType;

    public AccommodationRequestPreviewViewHolder(@NonNull View itemView) {
        super(itemView);
            txtName = itemView.findViewById(R.id.txtNameAccommodationItem);
            txtAddress = itemView.findViewById(R.id.txtAddressAccommodationItem);
            txtExistingName = itemView.findViewById(R.id.txtExistingNameAccommodationItem);
            txtExistingAddress = itemView.findViewById(R.id.txtExistingAddressAccommodationItem);
            txtStatus = itemView.findViewById(R.id.txtRequestStatus);
            txtType = itemView.findViewById(R.id.txtTypeOfRequest);
            cardView = itemView.findViewById(R.id.cardViewAccommodationRequestPreview);
    }
}
