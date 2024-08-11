package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.adapters.ReservationHostViewAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityReservationDetailsHostBinding;
import com.example.projekatmobilne.model.Enum.ReservationStatus;
import com.example.projekatmobilne.model.Enum.Role;
import com.example.projekatmobilne.model.requestDTO.ReservationDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.ReviewDTOPageItem;
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
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("reservationId")){
            reservationId = (Long) intent.getSerializableExtra("reservationId");
            accommodationId = (Long) intent.getSerializableExtra("accommodationId");
        }



        binding.btnAccommodationRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationDetailsHostActivity.this, AccommodationDetailsActivity.class);
                intent.putExtra("accommodationId", accommodationId);
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();

        loadReservationDetails();

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


                    System.out.println("IKSDEBRO");
                    System.out.println(responseDTO.getContent().size());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}