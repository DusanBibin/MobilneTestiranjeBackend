package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityComplaintBinding;
import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.Enum.Role;
import com.example.projekatmobilne.model.responseDTO.ComplaintDTO;
import com.example.projekatmobilne.tools.JWTManager;
import com.example.projekatmobilne.tools.ResponseParser;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityComplaintBinding binding;
    private Long accommodationReviewComplaintId = 0L;
    private Long ownerReviewComplaintId = 0L;
    private Long reviewId = 0L;
    private Long complaintId = 0L;
    private Long reservationId = 0L;
    private Long accommodationId = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding = ActivityComplaintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        binding.btnRedirect.setVisibility(View.GONE);
        binding.linearLayoutButtons.setVisibility(View.GONE);

        Intent intent = getIntent();
        if(intent != null && (intent.hasExtra("accommodationReviewComplaintId") || intent.hasExtra("ownerReviewComplaintId"))){
            accommodationReviewComplaintId = (Long) intent.getSerializableExtra("accommodationReviewComplaintId");
            ownerReviewComplaintId = (Long) intent.getSerializableExtra("ownerReviewComplaintId");
            reviewId = (Long) intent.getSerializableExtra("reviewId");
            accommodationId = (Long) intent.getSerializableExtra("accommodationId");
            reservationId = (Long) intent.getSerializableExtra("reservationId");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if(accommodationReviewComplaintId == null){
            binding.txtReviewType.setText("Owner review complaint");

            if(ownerReviewComplaintId.equals(0L)) binding.linearLayoutExistingComplaint.setVisibility(View.GONE);
            else {
                complaintId = ownerReviewComplaintId;
                binding.linearLayoutNewComplaint.setVisibility(View.GONE);
            }

        }
        if(ownerReviewComplaintId == null){
            binding.txtReviewType.setText("Accommodation review complaint");

            if(accommodationReviewComplaintId.equals(0L)) binding.linearLayoutExistingComplaint.setVisibility(View.GONE);
            else{
                complaintId = accommodationReviewComplaintId;
                binding.linearLayoutNewComplaint.setVisibility(View.GONE);
                if(reservationId != 0L) binding.btnRedirect.setVisibility(View.VISIBLE);
            }
        }



        if(complaintId != 0L){
            binding.progressBarComplaintDetails.setVisibility(View.VISIBLE);
            binding.nestedScrollView.setVisibility(View.GONE);
            Call<ResponseBody> call = ClientUtils.apiService.getComplaint(complaintId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    binding.nestedScrollView.setVisibility(View.VISIBLE);
                    binding.progressBarComplaintDetails.setVisibility(View.GONE);


                    if(response.code() == 200){
                        ComplaintDTO complaintDTO = ResponseParser.parseResponse(response, ComplaintDTO.class, false);

                        binding.txtOwnerNameSurname.setText("Owner: " + complaintDTO.getOwnerNameSurname());
                        binding.txtOwnerEmail.setText("Owner email: " + complaintDTO.getOwnerEmail());
                        binding.txtGuestNameSurname.setText("Guest: " + complaintDTO.getGuestNameSurname());
                        binding.txtGuestEmail.setText("Guest email: " + complaintDTO.getGuestEmail());
                        binding.txtReviewComment.setText("Review comment: " + complaintDTO.getReviewComment());
                        binding.txtReviewRating.setText("Review rating: " + complaintDTO.getReviewRating());
                        binding.txtComplaintReason.setText("Complaint reason: " + complaintDTO.getComplaintReason());
                        binding.txtComplaintStatus.setText("Complaint status: " + complaintDTO.getRequestStatus());

                        if(complaintDTO.getAdminResponse() == null || complaintDTO.getAdminResponse().equals("")){
                            binding.txtComplaintAdminResponse.setVisibility(View.GONE);
                        }else{
                            binding.txtComplaintAdminResponse.setText("Admin response: " + complaintDTO.getAdminResponse());
                        }

                        if(JWTManager.getRoleEnum().equals(Role.ADMIN) &&
                                complaintDTO.getRequestStatus().equals(RequestStatus.PENDING)) binding.linearLayoutButtons.setVisibility(View.VISIBLE);

                        binding.progressBarSendComplaint.setVisibility(View.GONE);
                        binding.linearLayoutNewComplaint.setVisibility(View.GONE);
                        binding.linearLayoutExistingComplaint.setVisibility(View.VISIBLE);
                    }

                    if(response.code() == 400){
                        Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                        if(map.containsKey("message")) Toast.makeText(ComplaintActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(ComplaintActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }



        System.out.println("accommodation complaint");
        System.out.println(accommodationReviewComplaintId);

        System.out.println("owner complaint");
        System.out.println(ownerReviewComplaintId);

        System.out.println("review");
        System.out.println(reviewId);

        System.out.println("reservation");
        System.out.println(reservationId);

        setupButtons();

    }

    private void setupButtons() {
        binding.btnConfirmComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.inputEditTextComplaintReason.getText().toString().isEmpty()){
                    binding.inputLayoutComplaintReason.setError("Reason for complaint must be provided");
                    return;
                }


                binding.btnConfirmComplaint.setVisibility(View.GONE);
                binding.progressBarSendComplaint.setVisibility(View.VISIBLE);
                Call<ResponseBody> call = ClientUtils.apiService
                        .createReviewComplaint(reviewId, binding.inputEditTextComplaintReason.getText().toString());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            ComplaintDTO complaintDTO = ResponseParser.parseResponse(response, ComplaintDTO.class, false);

                            binding.txtOwnerNameSurname.setText("Owner: " + complaintDTO.getOwnerNameSurname());
                            binding.txtOwnerEmail.setText("Owner email: " + complaintDTO.getOwnerEmail());
                            binding.txtGuestNameSurname.setText("Guest: " + complaintDTO.getGuestNameSurname());
                            binding.txtGuestEmail.setText("Guest email: " + complaintDTO.getGuestEmail());
                            binding.txtReviewComment.setText("Review comment: " + complaintDTO.getReviewComment());
                            binding.txtReviewRating.setText("Review rating: " + complaintDTO.getReviewRating());
                            binding.txtComplaintReason.setText("Complaint reason: " + complaintDTO.getComplaintReason());
                            binding.txtComplaintStatus.setText("Complaint status: " + complaintDTO.getRequestStatus());
                            if(complaintDTO.getAdminResponse() == null || complaintDTO.getAdminResponse().equals("")){
                                binding.txtComplaintAdminResponse.setVisibility(View.GONE);
                            }else{
                                binding.txtComplaintAdminResponse.setText("Admin response: " + complaintDTO.getAdminResponse());
                            }
                            binding.progressBarSendComplaint.setVisibility(View.GONE);
                            binding.linearLayoutNewComplaint.setVisibility(View.GONE);
                            binding.linearLayoutExistingComplaint.setVisibility(View.VISIBLE);
                            Toast.makeText(ComplaintActivity.this, "Complaint successfully created", Toast.LENGTH_SHORT).show();
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class, true);
                            if (map.containsKey("message"))
                                Toast.makeText(ComplaintActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ComplaintActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        // You can also log the stack trace for more detailed information
                        t.printStackTrace();
                    }
                });
            }
        });

        binding.btnRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComplaintActivity.this, ReservationDetailsHostActivity.class);
                System.out.println("BBBBBBBBBBBBBBBBBBBBBB");
                System.out.println("accommodationId");
                System.out.println(accommodationId);
                System.out.println("rezervacija id");
                System.out.println(reservationId);
                System.out.println("BBBBBBBBBBBBBBBBBBBBBB");
                intent.putExtra("accommodationId", accommodationId);
                intent.putExtra("reservationId", reservationId);
                startActivity(intent);
            }
        });


        binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String adminResponse = binding.inputEditTextRejectReason.getText().toString();
                if(adminResponse.isEmpty()){
                    binding.inputLayoutRejectReason.setError("This field cannot be empty");
                    return;
                }



                binding.progressBarProcessComplaint.setVisibility(View.GONE);
                binding.linearLayoutButtons.setVisibility(View.VISIBLE);
                Call<ResponseBody> call = ClientUtils.apiService.processReviewCommentComplaint(complaintId, RequestStatus.ACCEPTED, adminResponse);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.code() == 200){
                            String responseMessage =
                                    ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ComplaintActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                            binding.txtComplaintStatus.setText("Complaint status: " + RequestStatus.ACCEPTED.toString());

                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ComplaintActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                        }

                        binding.txtComplaintAdminResponse.setText(adminResponse);
                        binding.progressBarProcessComplaint.setVisibility(View.VISIBLE);
                        binding.linearLayoutButtons.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ComplaintActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });

            }
        });

        binding.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarProcessComplaint.setVisibility(View.GONE);
                binding.linearLayoutButtons.setVisibility(View.VISIBLE);


                String rejectReason = binding.inputEditTextRejectReason.getText().toString();
                if(rejectReason.isEmpty()){
                    binding.inputLayoutRejectReason.setError("This field cannot be empty");
                    return;
                }


                Call<ResponseBody> call = ClientUtils.apiService
                        .processReviewCommentComplaint(complaintId, RequestStatus.REJECTED, rejectReason);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.code() == 200){
                            String responseMessage =
                                    ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(ComplaintActivity.this, responseMessage, Toast.LENGTH_SHORT).show();

                            binding.txtComplaintStatus.setText("Complaint status: " + RequestStatus.REJECTED.toString());
                            binding.txtComplaintAdminResponse.setText("Admin response: " + rejectReason);
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(ComplaintActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                        }

                        binding.progressBarProcessComplaint.setVisibility(View.VISIBLE);
                        binding.linearLayoutButtons.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ComplaintActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });

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