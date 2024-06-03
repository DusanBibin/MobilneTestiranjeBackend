package com.example.projekatmobilne.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.AccommodationDetailsActivity;
import com.example.projekatmobilne.activities.HomeActivity;
import com.example.projekatmobilne.activities.LoginActivity;

import java.util.List;

public class AccommodationSearchAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<AccommodationCard> dataList;

    public void setSearchList(List<AccommodationCard> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }


    public AccommodationSearchAdapter(Context context, List<AccommodationCard> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accommodation_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.imageView.setImageBitmap(dataList.get(position).getImageBitmap());
        holder.txtAddress.setText(dataList.get(position).getAddress());
        holder.txtName.setText(dataList.get(position).getName());
        holder.txtType.setText(dataList.get(position).getType().toString());
        holder.txtGuests.setText(dataList.get(position).getGuests());
        holder.txtOneNightPrice.setText(dataList.get(position).getOneNightPrice().toString());
        holder.txtTotalPrice.setText(dataList.get(position).getTotalPrice().toString());
        holder.txtIsPerPerson.setText(dataList.get(position).getPerPerson().toString());
        holder.txtAmenities.setText(dataList.get(position).getAmenities());

        Double rating = dataList.get(position).getRating();
        if(rating == 0){
            holder.ratingBar.setVisibility(View.GONE);
            holder.txtNoRatings.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(0);
        }else{
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.txtNoRatings.setVisibility(View.GONE);
            holder.ratingBar.setRating(dataList.get(position).getRating().floatValue());
        }



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AccommodationCard clickedAccommodation = dataList.get(holder.getAdapterPosition());

                Intent intent = new Intent(context, AccommodationDetailsActivity.class);
                intent.putExtra("accommodationId", clickedAccommodation.getAccommodationId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView txtName, txtAddress, txtGuests, txtType, txtIsPerPerson, txtOneNightPrice, txtTotalPrice, txtNoRatings, txtAmenities;
    RatingBar ratingBar;

    CardView cardView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);


        imageView = itemView.findViewById(R.id.imgAccommodation);
        txtName = itemView.findViewById(R.id.txtName);
        txtAddress = itemView.findViewById(R.id.txtAddress);
        txtGuests = itemView.findViewById(R.id.txtGuests);
        txtType = itemView.findViewById(R.id.txtType);
        txtIsPerPerson = itemView.findViewById(R.id.txtIsPerPerson);
        txtOneNightPrice = itemView.findViewById(R.id.txtOneNightPrice);
        txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        cardView = itemView.findViewById(R.id.recCard);
        txtNoRatings = itemView.findViewById(R.id.txtNoRatings);
        txtAmenities = itemView.findViewById(R.id.txtAmenities);

    }
}