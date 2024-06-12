package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.model.responseDTO.ReviewDTOResponse;
import com.example.projekatmobilne.tools.ImageUtils;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;

public class ImagesAddAdapter extends RecyclerView.Adapter<ImageAddHolder> {

    private Context context;
    private List<File> dataList;

    public void setSearchList(List<File> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }

    public ImagesAddAdapter(Context context, List<File> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ImageAddHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_add, parent, false);
        return new ImageAddHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAddHolder holder, int position) {
        holder.txtImageName.setText(dataList.get(position).getName());

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
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

class ImageAddHolder extends RecyclerView.ViewHolder{

    Button btnRemove;
    TextView txtImageName;
    public ImageAddHolder(@NonNull View itemView) {
        super(itemView);
        txtImageName = itemView.findViewById(R.id.txtImageName);
        btnRemove = itemView.findViewById(R.id.btnRemoveImage);

    }
}