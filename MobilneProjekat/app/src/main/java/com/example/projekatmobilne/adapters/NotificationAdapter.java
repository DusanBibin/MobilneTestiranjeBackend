package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.model.Notification;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

    private Context context;
    private List<Notification> dataList;

    public NotificationAdapter(List<Notification> notifications, Context context) {
        this.dataList = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    public List<Notification> getDataList() {
        return dataList;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = dataList.get(position);
        holder.txtNotificationMessage.setText(notification.getMessage());
        holder.txtNotificationDate.setText(notification.getCreatedAt().toString());

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            Call<ResponseBody> call = ClientUtils.apiService.toggleNotification(notification.getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        notification.setRead(!notification.getRead());
                        notifyItemChanged(adapterPosition);
                    } else {
                        Toast.makeText(context, "Failed to update notification status", Toast.LENGTH_SHORT).show();
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

class NotificationViewHolder extends RecyclerView.ViewHolder {
    TextView txtNotificationMessage, txtNotificationDate;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
        txtNotificationMessage = itemView.findViewById(R.id.txtNotificationMessage);
        txtNotificationDate = itemView.findViewById(R.id.txtNotificationDate);
    }
}

