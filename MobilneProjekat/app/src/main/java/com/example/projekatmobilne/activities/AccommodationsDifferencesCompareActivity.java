package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projekatmobilne.clients.ApiService;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityAccomodationsDifferencesCompareBinding;
import com.example.projekatmobilne.model.Enum.AccommodationType;
import com.example.projekatmobilne.model.Enum.Amenity;
import com.example.projekatmobilne.model.Enum.RequestType;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.model.responseDTO.AccommodationDTOResponse;
import com.example.projekatmobilne.model.responseDTO.AccommodationDifferencesDTO;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationsDifferencesCompareActivity extends AppCompatActivity {

    private Long requestId = 0L;
    private Toolbar toolbar;
    private ActivityAccomodationsDifferencesCompareBinding binding;
    private AccommodationDTOResponse accommodationDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccomodationsDifferencesCompareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("requestId")){
            requestId = (Long) intent.getSerializableExtra("requestId");


            Call<ResponseBody> call = ClientUtils.apiService.getOwnerAccommodationRequest(requestId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200){
                        AccommodationDifferencesDTO responseDTO = ResponseParser.parseResponse(response, AccommodationDifferencesDTO.class, false);
                        System.out.println(responseDTO);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(AccommodationsDifferencesCompareActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }
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