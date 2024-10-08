package com.example.projekatmobilne.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.ReviewDTOPageItem;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.innerDTOPage.ReviewDTOPageItemInner;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsViewHolder> {

    private Context context;
    private List<ReviewDTOPageItem> dataList;

    public void setSearchList(List<ReviewDTOPageItem> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }

    public ReviewsAdapter(Context context, List<ReviewDTOPageItem> dataList){
        this.context = context;
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {


        ReviewDTOPageItemInner ownerReview = dataList.get(position).getOwnerReview();
        ReviewDTOPageItemInner accommodationReview = dataList.get(position).getAccommodationReview();


        if(ownerReview != null){
            holder.txtOwnerReview.setText(dataList.get(position).getOwnerReview().getComment());
            holder.ratingBarOwner.setRating(dataList.get(position).getOwnerReview().getRating());
        }else holder.linearLayoutOwnerReview.setVisibility(View.GONE);

        if(accommodationReview != null){
            holder.txtAccommodationReview.setText(dataList.get(position).getAccommodationReview().getComment());
            holder.ratingBarAccommodation.setRating(dataList.get(position).getAccommodationReview().getRating());
        }else holder.linearLayoutAccommodationReview.setVisibility(View.GONE);

        holder.txtName.setText(dataList.get(position).getGuestName());
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class ReviewsViewHolder extends RecyclerView.ViewHolder{

    TextView txtName, txtAccommodationReview, txtOwnerReview;
    RatingBar ratingBarAccommodation, ratingBarOwner;
    LinearLayout linearLayoutAccommodationReview, linearLayoutOwnerReview;

    public ReviewsViewHolder(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.txtGuestName);
        txtAccommodationReview = itemView.findViewById(R.id.txtCommentAccommodation);
        ratingBarAccommodation = itemView.findViewById(R.id.ratingBarAccommodation);
        txtOwnerReview = itemView.findViewById(R.id.txtCommentOwner);
        ratingBarOwner = itemView.findViewById(R.id.ratingBarOwner);
        linearLayoutAccommodationReview = itemView.findViewById(R.id.linearLayoutAccommodationReview);
        linearLayoutOwnerReview = itemView.findViewById(R.id.linearLayoutOwnerReview);

    }
}


