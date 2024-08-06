package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.FullScreenImageActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Bitmap> imageList;
    private LayoutInflater inflater;
    private Context context;
    private List<File> tempImages;

    public ImageAdapter(Context context, List<Bitmap> imageList) {
        this.imageList = imageList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.tempImages = new ArrayList<>();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.accommodation_image, parent, false);
        return new ImageViewHolder(view);
    }

    public void addImage(Bitmap bitmap, File tempFile) {
        imageList.add(bitmap);
        tempImages.add(tempFile);
        notifyItemInserted(imageList.size() - 1);
    }





    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Bitmap bitmap = imageList.get(position);
        holder.imageView.setImageBitmap(bitmap);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("KURCINAAA IMAGE VIEWWWW ");
                System.out.println(holder.getAdapterPosition());

//                Bitmap imgBitmap = imageList.get(holder.getAdapterPosition());
//
//
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream.toByteArray();
//                String image =  Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//
//                Intent intent = new Intent(context, FullScreenImageActivity.class);
//                intent.putExtra("picture", image);
//                context.startActivity(intent);

                String path = tempImages.get(holder.getAdapterPosition()).getAbsolutePath();
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("picture", path);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void updateImages(List<Bitmap> newImages) {
        this.imageList.clear();
        this.imageList.addAll(newImages);
        notifyDataSetChanged();
    }




    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}