package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.AccommodationCard;
import com.example.projekatmobilne.adapters.ImageAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityAccommodationDetailsBinding;
import com.example.projekatmobilne.databinding.ActivityRegisterBinding;
import com.example.projekatmobilne.model.responseDTO.AccommodationDTOResponse;
import com.example.projekatmobilne.tools.JWTManager;
import com.example.projekatmobilne.tools.ResponseParser;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityAccommodationDetailsBinding binding;

    private ViewPager2 viewPager;
    private Long accommodationId;
    private AccommodationDTOResponse accommodationDTO;
    private int imageResId = R.drawable.iksde;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccommodationDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("accommodationId")){
            accommodationId = (Long) intent.getSerializableExtra("accommodationId");
        }


        ImageAdapter adapter = new ImageAdapter(this, imageResId);
        binding.viewPager.setAdapter(adapter);

        Call<ResponseBody> call = ClientUtils.apiService.getAccommodation(accommodationId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 400){
                    Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);

                    if(map.containsKey("message")){
                        String errMessage = map.get("message");
                        Toast.makeText(AccommodationDetailsActivity.this, errMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                if(response.code() == 200){
                    AccommodationDTOResponse responseMessage =
                            ResponseParser.parseResponse(response, AccommodationDTOResponse.class, false);


                    System.out.println("kurcinaaa");
                    System.out.println(responseMessage);
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccommodationDetailsActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                // You can also log the stack trace for more detailed information
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