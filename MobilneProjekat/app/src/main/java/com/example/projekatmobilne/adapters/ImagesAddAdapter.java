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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.AccommodationsDifferencesCompareActivity;
import com.example.projekatmobilne.activities.FullScreenImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesAddAdapter extends RecyclerView.Adapter<ImageAddHolder> {

    private Context context;
    private List<File> dataList;
    private List<File> imagesToAdd;
    private List<String> imagesToDelete;
    private List<String> differencesImagesToDelete;
    private List<String> differencesImagesToAdd;
    private int red;
    private int green;
    public void setSearchList(List<File> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }

    public ImagesAddAdapter(Context context, List<File> dataList){
        this.context = context;
        this.dataList = dataList;
        this.imagesToAdd = new ArrayList<>();
        this.imagesToDelete = new ArrayList<>();
        green = ContextCompat.getColor(context, R.color.green);
        red = ContextCompat.getColor(context, R.color.red);
    }


    public void setDifferencesImagesToDelete(List<String> imagesToDelete){differencesImagesToDelete = imagesToDelete;}
    public void setDifferencesImagesToAdd(List<String> imagesToAdd){differencesImagesToAdd = imagesToAdd;}
    public void addNewImage(File image){imagesToAdd.add(image);}
    public List<File> getImagesToAdd(){return imagesToAdd;}
    public List<String> getImagesToDelete(){return imagesToDelete;}
    @NonNull
    @Override
    public ImageAddHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_add, parent, false);
        return new ImageAddHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAddHolder holder, int position) {

        if(context instanceof AccommodationsDifferencesCompareActivity){
            holder.btnRemove.setVisibility(View.INVISIBLE);
            for(String str: differencesImagesToAdd){
                if(dataList.get(position).getName().contains(str)){
                    holder.txtImageName.setTextColor(green);
                    break;
                }
            }

            for(String str: differencesImagesToDelete){
                if(dataList.get(position).getName().contains(str)){
                    holder.txtImageName.setTextColor(red);
                    break;
                }

            }

        }

        holder.txtImageName.setText(dataList.get(position).getName());

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = dataList.remove(holder.getAdapterPosition());
                if(imagesToAdd.contains(file)) imagesToAdd.remove(file);
                else imagesToDelete.add(file.getName());

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