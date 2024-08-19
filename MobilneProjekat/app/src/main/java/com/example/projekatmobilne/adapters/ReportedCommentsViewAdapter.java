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
import com.example.projekatmobilne.activities.ComplaintActivity;
import com.example.projekatmobilne.adapters.AdapterItems.AccommodationHostItem;
import com.example.projekatmobilne.model.responseDTO.ComplaintDTO;

import java.util.List;

public class ReportedCommentsViewAdapter extends RecyclerView.Adapter<ReportedCommentsViewHolder> {
    private Context context;
    private List<ComplaintDTO> dataList;


    public void setList(List<ComplaintDTO> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }


    public ReportedCommentsViewAdapter(Context context, List<ComplaintDTO> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ReportedCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reported_comment_preview_item, parent, false);
        return new ReportedCommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedCommentsViewHolder holder, int position) {
        ComplaintDTO complaintDTO = dataList.get(position);
        holder.txtReportType.setText(complaintDTO.getReviewType() + " review complaint");
        holder.txtOwnerNameSurname.setText(complaintDTO.getOwnerNameSurname());
        holder.txtOwnerEmail.setText(complaintDTO.getOwnerEmail());
        holder.txtStatus.setText(complaintDTO.getRequestStatus().toString());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ComplaintActivity.class);

                String idType = "accommodationReviewComplaintId";
                if(complaintDTO.getReviewType().equals("Owner")) idType = "ownerReviewComplaintId";

                intent.putExtra(idType, complaintDTO.getComplaintId());
                intent.putExtra("accommodationId", complaintDTO.getAccommodationId());
                intent.putExtra("reservationId", complaintDTO.getReservationId());
                intent.putExtra("reviewId", complaintDTO.getReviewId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}


class ReportedCommentsViewHolder extends RecyclerView.ViewHolder{
    TextView txtReportType, txtOwnerNameSurname, txtOwnerEmail, txtStatus;
    LinearLayout linearLayout;
    public ReportedCommentsViewHolder(@NonNull View itemView) {
        super(itemView);
        linearLayout = itemView.findViewById(R.id.linearLayoutReportedCommentView);
        txtReportType = itemView.findViewById(R.id.txtReportType);
        txtOwnerNameSurname = itemView.findViewById(R.id.txtOwnerNameSurname);
        txtOwnerEmail = itemView.findViewById(R.id.txtOwnerEmail);
        txtStatus = itemView.findViewById(R.id.txtReportStatus);
    }
}
