package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.FullScreenImageActivity;

import java.io.File;
import java.util.List;

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
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = dataList.get(holder.getAdapterPosition()).getAbsolutePath();
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("picture", path);
                context.startActivity(intent);

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
    RelativeLayout relativeLayout;
    public ImageAddHolder(@NonNull View itemView) {
        super(itemView);
        relativeLayout = itemView.findViewById(R.id.relativeLayoutImageAdd);
        txtImageName = itemView.findViewById(R.id.txtImageName);
        btnRemove = itemView.findViewById(R.id.btnRemoveImage);

    }
}