package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.responseDTO.ReportDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.AccommodationRequestPreviewDTO;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportUserAdapter extends RecyclerView.Adapter<ReportUserAdapterViewHolder> {

    private Context context;
    private List<ReportDTO> dataList;

    public ReportUserAdapter(Context context, List<ReportDTO> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public void setList(List<ReportDTO> dataList){
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public List<ReportDTO> getDataList() {
        return dataList;
    }

    public void clearData(){
        this.dataList = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportUserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_user_item, parent, false);
        return new ReportUserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportUserAdapterViewHolder holder, int position) {
        ReportDTO reportDTO = dataList.get(holder.getAdapterPosition());

        holder.txtReportedUserName.setText(reportDTO.getReporedUser());
        holder.txtReportedUserRole.setText(reportDTO.getReportedUserRole());
        holder.txtReportingUserName.setText(reportDTO.getReporterUser());
        holder.txtReportingUserRole.setText(reportDTO.getReporterUserRole());
        holder.txtReportReason.setText(reportDTO.getReason());

        holder.btnAcceptUser.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            Call<ResponseBody> call = ClientUtils.apiService.reviewUserComplaint(reportDTO.getId(), RequestStatus.ACCEPTED);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Complaint has been accepted.", Toast.LENGTH_SHORT).show();
                        dataList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    } else {
                        Toast.makeText(context, "Error during complaint accepting.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnRejectUser.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            Call<ResponseBody> call = ClientUtils.apiService.reviewUserComplaint(reportDTO.getId(), RequestStatus.REJECTED);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Complaint has been rejected.", Toast.LENGTH_SHORT).show();
                        dataList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    } else {
                        Toast.makeText(context, "Error during complaint rejecting.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class ReportUserAdapterViewHolder extends RecyclerView.ViewHolder{

    Button btnAcceptUser, btnRejectUser;
    CardView cardViewReportUserItem;
    TextView txtReportedUserName, txtReportedUserRole, txtReportingUserName, txtReportingUserRole, txtReportReason;

    public ReportUserAdapterViewHolder(@NonNull View itemView) {
        super(itemView);

        txtReportedUserName = itemView.findViewById(R.id.txtReportedUserName);
        txtReportedUserRole = itemView.findViewById(R.id.txtReportedUserRole);
        txtReportingUserName = itemView.findViewById(R.id.txtReportingUserName);
        txtReportingUserRole = itemView.findViewById(R.id.txtReportingUserRole);
        txtReportReason = itemView.findViewById(R.id.txtReportReason);
        cardViewReportUserItem = itemView.findViewById(R.id.cardViewReportUserItem);
        btnAcceptUser = itemView.findViewById(R.id.btnAcceptUser);
        btnRejectUser = itemView.findViewById(R.id.btnRejectUser);
    }
}
