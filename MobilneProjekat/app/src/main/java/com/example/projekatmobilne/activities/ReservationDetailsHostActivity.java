package com.example.projekatmobilne.activities;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.ReservationHostViewAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityReservationDetailsHostBinding;
import com.example.projekatmobilne.model.Enum.ReservationStatus;
import com.example.projekatmobilne.model.Enum.Role;
import com.example.projekatmobilne.model.requestDTO.ReservationDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.ReviewDTOPageItem;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.innerDTOPage.ReviewDTOPageItemInner;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.ReservationHostDTOPagedResponse;
import com.example.projekatmobilne.tools.JWTManager;
import com.example.projekatmobilne.tools.ResponseParser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationDetailsHostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityReservationDetailsHostBinding binding;
    private ReservationHostViewAdapter adapter;
    private Long reservationId;
    private Long accommodationId;

    private Long accommodationReviewComplaintId = 0L;
    private Long ownerReviewComplaintId = 0L;
    private ReservationDTO reservation;
    private ReviewDTOPageItem review;

    private Integer currentPage = 0;
    private Boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationDetailsHostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.cardViewReview.setVisibility(View.GONE);
        binding.btnRemoveOwnerReview.setVisibility(View.GONE);
        binding.btnRemoveAccommodationReview.setVisibility(View.GONE);
        binding.btnCreateComplaintOwner.setVisibility(View.GONE);
        binding.btnCreateComplaintAccommodation.setVisibility(View.GONE);


        binding.cardViewReport.setVisibility(View.GONE);
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("reservationId")){
            reservationId = (Long) intent.getSerializableExtra("reservationId");
            accommodationId = (Long) intent.getSerializableExtra("accommodationId");
        }

        binding.btnRemoveOwnerReview.setVisibility(View.GONE);
        binding.btnRemoveAccommodationReview.setVisibility(View.GONE);


        setupReviewButtons();
        setupReservationButtons();

        binding.recyclerViewReservationConflicts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLastPage && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                    currentPage++;
                    loadConflictedReservationsPage();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.btnCreateComplaintOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationDetailsHostActivity.this, ComplaintActivity.class);
                intent.putExtra("ownerReviewComplaintId", ownerReviewComplaintId);
                intent.putExtra("accommodationId", accommodationId);
                intent.putExtra("reservationId", reservationId);
                intent.putExtra("reviewId", review.getOwnerReview().getReviewId());
                startActivity(intent);
            }
        });

        binding.btnCreateComplaintAccommodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ReservationDetailsHostActivity.this, ComplaintActivity.class);
                intent.putExtra("accommodationReviewComplaintId", accommodationReviewComplaintId);
                intent.putExtra("accommodationId", accommodationId);
                intent.putExtra("reservationId", reservationId);
                intent.putExtra("reviewId", review.getAccommodationReview().getReviewId());
                startActivity(intent);
            }
        });

        loadReservationDetails();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupReviewButtons() {

        binding.btnRemoveOwnerReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarRemoveOwnerReview.setVisibility(View.VISIBLE);
                binding.btnRemoveOwnerReview.setVisibility(View.GONE);
                Call<ResponseBody> call = ClientUtils.apiService.deleteOwnerReview(review.getOwnerReview().getReviewId());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            binding.progressBarRemoveOwnerReview.setVisibility(View.GONE);
                            binding.linearLayoutOwnerReview.setVisibility(View.GONE);
                            binding.linearLayoutAddOwnerReview.setVisibility(View.VISIBLE);
                            binding.btnAddOwnerReview.setVisibility(View.VISIBLE);
                            String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ReservationDetailsHostActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            binding.btnRemoveOwnerReview.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });

        binding.btnRemoveAccommodationReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarRemoveAccommodationReview.setVisibility(View.VISIBLE);
                binding.btnRemoveAccommodationReview.setVisibility(View.GONE);
                Call<ResponseBody> call = ClientUtils.apiService.deleteAccommodationReview(review.getAccommodationReview().getReviewId());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            binding.progressBarRemoveAccommodationReview.setVisibility(View.GONE);
                            String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ReservationDetailsHostActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                            binding.linearLayoutAccommodationReview.setVisibility(View.GONE);
                            binding.linearLayoutAddAccommodationReview.setVisibility(View.VISIBLE);
                            binding.btnAddAccommodationReview.setVisibility(View.VISIBLE);
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            binding.btnRemoveAccommodationReview.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });

        binding.btnAddAccommodationReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarAddAccommodationReview.setVisibility(View.VISIBLE);
                binding.btnAddAccommodationReview.setVisibility(View.GONE);

                binding.inputLayoutAccommodationComment.setError(null);

                boolean isValid = true;
                if(binding.inputEditTextAccommodationComment.getText().toString().isEmpty()){
                    binding.inputLayoutAccommodationComment.setError("This field cannot be empty");
                    isValid = false;
                }

                if(binding.ratingBarAddAccommodation.getRating() == 0) {
                    Toast.makeText(ReservationDetailsHostActivity.this, "Accommodation rating cannot be empty", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if(!isValid) {
                    binding.progressBarAddAccommodationReview.setVisibility(View.GONE);
                    binding.btnAddAccommodationReview.setVisibility(View.VISIBLE);
                    return;
                }

                String comment = binding.inputEditTextAccommodationComment.getText().toString();

                Long rating = (long) binding.ratingBarAddAccommodation.getRating();
                Call<ResponseBody> call = ClientUtils.apiService.createAccommodationReview(accommodationId, reservationId,
                        new ReviewDTOPageItemInner(comment, rating));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            ReviewDTOPageItemInner reviewAccommodation = ResponseParser.parseResponse(response, ReviewDTOPageItemInner.class, false);
                            review.setAccommodationReview(reviewAccommodation);
                            Toast.makeText(ReservationDetailsHostActivity.this, "Successfully created accommodation review", Toast.LENGTH_SHORT).show();
                            binding.progressBarAddAccommodationReview.setVisibility(View.GONE);
                            binding.txtCommentAccommodation.setText(reviewAccommodation.getComment());
                            binding.ratingBarAccommodation.setRating(reviewAccommodation.getRating());
                            binding.progressBarAddAccommodationReview.setVisibility(View.GONE);
                            binding.btnRemoveAccommodationReview.setVisibility(View.VISIBLE);
                            binding.linearLayoutAddAccommodationReview.setVisibility(View.GONE);
                            binding.linearLayoutAccommodationReview.setVisibility(View.VISIBLE);
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            binding.progressBarAddAccommodationReview.setVisibility(View.GONE);
                            binding.btnAddAccommodationReview.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });

            }
        });

        binding.btnAddOwnerReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarAddOwnerReview.setVisibility(View.VISIBLE);
                binding.btnAddOwnerReview.setVisibility(View.GONE);

                binding.inputLayoutOwnerComment.setError(null);

                boolean isValid = true;
                if(binding.inputEditTextOwnerComment.getText().toString().isEmpty()){
                    binding.inputLayoutOwnerComment.setError("This field cannot be empty");
                    isValid = false;
                }

                if(binding.ratingBarAddOwner.getRating() == 0) {
                    Toast.makeText(ReservationDetailsHostActivity.this, "Owner rating cannot be empty", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if(!isValid) {
                    binding.progressBarAddOwnerReview.setVisibility(View.GONE);
                    binding.btnAddOwnerReview.setVisibility(View.VISIBLE);
                    return;
                }

                String comment = binding.inputEditTextOwnerComment.getText().toString();

                Long rating = (long) binding.ratingBarAddOwner.getRating();
                Call<ResponseBody> call = ClientUtils.apiService.createOwnerReview(reservation.getOwnerId(),
                        new ReviewDTOPageItemInner(comment, rating));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            ReviewDTOPageItemInner reviewOwner = ResponseParser.parseResponse(response, ReviewDTOPageItemInner.class, false);
                            review.setOwnerReview(reviewOwner);
                            Toast.makeText(ReservationDetailsHostActivity.this, "Successfully created owner review", Toast.LENGTH_SHORT).show();
                            binding.progressBarAddOwnerReview.setVisibility(View.GONE);
                            binding.txtCommentOwner.setText(reviewOwner.getComment());
                            binding.ratingBarOwner.setRating(reviewOwner.getRating());
                            binding.progressBarAddOwnerReview.setVisibility(View.GONE);
                            binding.btnRemoveOwnerReview.setVisibility(View.VISIBLE);
                            binding.linearLayoutAddOwnerReview.setVisibility(View.GONE);
                            binding.linearLayoutOwnerReview.setVisibility(View.VISIBLE);
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            binding.progressBarAddOwnerReview.setVisibility(View.GONE);
                            binding.btnAddOwnerReview.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    private void setupReservationButtons() {
        binding.btnAccommodationRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationDetailsHostActivity.this, AccommodationDetailsActivity.class);
                intent.putExtra("accommodationId", accommodationId);
                startActivity(intent);
            }
        });

        binding.reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.inputEditTextReport.getText().toString().isEmpty()){
                    binding.inputEditTextReport.setError("Reject reason must be provided");
                    return;
                }

                binding.linearLayoutButtons.setVisibility(View.GONE);
                binding.progressBarButton.setVisibility(View.VISIBLE);

                Call<ResponseBody> call = ClientUtils.apiService.sendHostReport(reservationId,binding.inputEditTextReport.getText().toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ReservationDetailsHostActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                        }

                        binding.progressBarButton.setVisibility(View.GONE);
                        loadReservationDetails();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });


        binding.btnAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                binding.linearLayoutButtons.setVisibility(View.GONE);
                binding.progressBarButton.setVisibility(View.VISIBLE);

                Call<ResponseBody> call = ClientUtils.apiService.processReservation(accommodationId, reservationId, ReservationStatus.ACCEPTED, "");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ReservationDetailsHostActivity.this, responseMessage, Toast.LENGTH_SHORT).show();

                        }

                        binding.progressBarButton.setVisibility(View.GONE);
                        loadReservationDetails();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnCancel.setVisibility(View.GONE);
                binding.progressBarButton.setVisibility(View.VISIBLE);

                Call<ResponseBody> call = ClientUtils.apiService.cancelReservation(accommodationId, reservationId);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ReservationDetailsHostActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            binding.btnCancel.setVisibility(View.VISIBLE);
                        }

                        binding.progressBarButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });

        binding.btnReject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                binding.inputLayoutDateRejectReason.setError(null);
                if(binding.inputEditTextRejectReason.getText().toString().isEmpty()){

                    binding.inputLayoutDateRejectReason.setError("Reject reason must be provided");
                    return;
                }

                binding.linearLayoutButtons.setVisibility(View.GONE);
                binding.progressBarButton.setVisibility(View.VISIBLE);


                Call<ResponseBody> call = ClientUtils.apiService.processReservation(accommodationId, reservationId, ReservationStatus.DECLINED, binding.inputEditTextRejectReason.getText().toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ReservationDetailsHostActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                        }

                        binding.progressBarButton.setVisibility(View.GONE);
                        loadReservationDetails();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });

            }
        });


        binding.btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                binding.btnDelete.setVisibility(View.GONE);
                binding.progressBarButton.setVisibility(View.VISIBLE);

                Call<ResponseBody> call = ClientUtils.apiService.deleteReservation(accommodationId, reservationId);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ReservationDetailsHostActivity.this, responseMessage, Toast.LENGTH_SHORT).show();


                            finish();
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            binding.btnCancel.setVisibility(View.VISIBLE);
                        }

                        binding.progressBarButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });

            }
        });



        binding.recyclerViewReservationConflicts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLastPage && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                    currentPage++;
                    loadConflictedReservationsPage();
                }
            }
        });
    }

    private void loadReservationDetails(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReservationDetailsHostActivity.this);
        binding.recyclerViewReservationConflicts.setLayoutManager(linearLayoutManager);

        adapter = new ReservationHostViewAdapter(ReservationDetailsHostActivity.this, new ArrayList<>());
        binding.recyclerViewReservationConflicts.setAdapter(adapter);

        adapter.removeData();

        binding.linearLayoutConflictReservations.setVisibility(View.GONE);
        Call<ResponseBody> call = ClientUtils.apiService.getReservationDetails(accommodationId, reservationId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 400){
                    Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                    if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                }


                if(response.code() == 200){
                    ReservationDTO r = ResponseParser.parseResponse(response, ReservationDTO.class, false);

                    if(r.getReviewPresent()){

                        binding.progressBarReview.setVisibility(View.VISIBLE);
                        Call<ResponseBody> reviewCall = ClientUtils.apiService.getReservationReview(accommodationId, reservationId);
                        reviewCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.code() == 200){
                                    ReviewDTOPageItem re = ResponseParser.parseResponse(response, ReviewDTOPageItem.class, false);
                                    if(re.getAccommodationReview() == null) binding.linearLayoutAccommodationReview.setVisibility(View.GONE);
                                    if(re.getOwnerReview() == null) binding.linearLayoutOwnerReview.setVisibility(View.GONE);

                                    binding.txtGuestName.setText(r.getNameAndSurname());
                                    binding.txtCommentAccommodation.setText(re.getAccommodationReview().getComment());
                                    binding.txtCommentOwner.setText(re.getOwnerReview().getComment());
                                    binding.ratingBarAccommodation.setRating(re.getAccommodationReview().getRating());
                                    binding.ratingBarOwner.setRating(re.getOwnerReview().getRating());

                                    binding.cardViewReview.setVisibility(View.VISIBLE);
                                    binding.cardViewReport.setVisibility(View.GONE);
                                    binding.progressBarReview.setVisibility(View.GONE);

                                }

                                if(response.code() == 400){
                                    Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                                    if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });
                    }

                    binding.txtName.setText(r.getAccommodationName());
                    binding.txtAddress.setText("Accommodation address: " + r.getAccommodationAddress());
                    binding.txtDateRange.setText("Date range: " + r.getReservationStartDate() + " - " + r.getReservationEndDate());
                    binding.txtCancelDeadline.setText("Cancel deadline: " + r.getCancelDeadline());
                    binding.txtGuestNumber.setText("Number of guests: " + r.getGuestNum());
                    binding.txtUnitPrice.setText("Unit price: " + r.getUnitPrice());
                    binding.txtIsPricePerGuest.setText("Is price per guest: " + r.getPerGuest());
                    binding.txtTotalPrice.setText("Total price: " + r.getPrice());
                    binding.txtStatus.setText("Reservation status: " + r.getStatus());
                    binding.txtRejectReason.setText("Reject reason: " + r.getReason());
                    binding.txtOwnerNameSurname.setText("Name and surname: " + r.getOwnerNameAndSurname());
                    binding.txtOwnerEmail.setText("Email: " + r.getOwnerEmail());
                    binding.txtNameSurname.setText("Name and surname: " + r.getNameAndSurname());
                    binding.txtEmail.setText("Email: " + r.getUserEmail());
                    binding.txtTimeCanceled.setText("Times user canceled past reservations: " + r.getTimesUserCancel());
                    if(r.getStatus().equals(ReservationStatus.DECLINED)) binding.txtRejectReason.setVisibility(View.VISIBLE);
                    if(!(r.getStatus().equals(ReservationStatus.PENDING) && JWTManager.getRoleEnum().equals(Role.OWNER))) binding.linearLayoutButtons.setVisibility(View.GONE);
                    if(!(r.getStatus().equals(ReservationStatus.ACCEPTED) &&
                            JWTManager.getRoleEnum().equals(Role.GUEST) && LocalDate.now().isBefore(r.getCancelDeadline())))
                        binding.btnCancel.setVisibility(View.GONE);
                    if(!(r.getStatus().equals(ReservationStatus.PENDING) && JWTManager.getRoleEnum().equals(Role.GUEST)))
                        binding.btnDelete.setVisibility(View.GONE);
                    if(r.getConflictReservations() && JWTManager.getUserIdLong().equals(r.getOwnerId())) {
                        binding.linearLayoutConflictReservations.setVisibility(View.VISIBLE);
                        loadConflictedReservationsPage();
                    }
                    if(r.getStatus().equals(ReservationStatus.ACCEPTED) && LocalDate.now().isAfter(r.getReservationStartDate())){
                        binding.cardViewReport.setVisibility(View.VISIBLE);
                    } else {
                        binding.cardViewReview.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void loadConflictedReservationsPage() {
        binding.recyclerViewReservationConflicts.setVisibility(View.GONE);
        binding.progressBarConflictReservations.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = ClientUtils.apiService.getConflictReservations(accommodationId, reservationId, currentPage, 10);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code() == 200){
                    ReservationHostDTOPagedResponse responseDTO = ResponseParser.parseResponse(response, ReservationHostDTOPagedResponse.class, false);

                    adapter.addMoreData(responseDTO.getContent());
                    isLastPage = responseDTO.isLast();
                    binding.recyclerViewReservationConflicts.setVisibility(View.VISIBLE);
                    binding.progressBarConflictReservations.setVisibility(View.GONE);
                }
                if(response.code() == 400){
                    Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                    if(map.containsKey("message")) Toast.makeText(ReservationDetailsHostActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ReservationDetailsHostActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}