package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.ImageAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityAccommodationDetailsBinding;
import com.example.projekatmobilne.model.responseDTO.AccommodationDTOResponse;
import com.example.projekatmobilne.tools.ResponseParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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


        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        binding.mainScrollview.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        binding.mainScrollview.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        binding.mainScrollview.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });



        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("accommodationId")){
            accommodationId = (Long) intent.getSerializableExtra("accommodationId");
        }



        List<Bitmap> imageList = new ArrayList<>();
        ImageAdapter adapter = new ImageAdapter(this, imageList);
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
                    accommodationDTO = ResponseParser.parseResponse(response, AccommodationDTOResponse.class, false);

                    for(Long imageId: accommodationDTO.getImageIds()){
                        Call<ResponseBody> imageResponse = ClientUtils.apiService.getAccommodationImage(accommodationId, imageId);
                        imageResponse.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.code() == 200){
                                    byte[] imageBytes;
                                    try {
                                        imageBytes = response.body().bytes();
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                        imageList.add(bitmap);
                                        adapter.notifyDataSetChanged();
//                                        a.setImageBitmap(bitmap);
//                                        ac.setImageBitmap(bitmap);
//                                        System.out.println("TEK SE SAD UPALILO AJAOOO");
//                                        adapter.setSearchList(dataList);
                                    }catch (IOException e){
                                        e.printStackTrace();
                                        Toast.makeText(AccommodationDetailsActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(AccommodationDetailsActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });
                    }




                    binding.txtName.setText(accommodationDTO.getName());
                    binding.txtAdress.setText(accommodationDTO.getAddress());
                    binding.txtDescription.setText(accommodationDTO.getDescription());

                    StringBuilder amenities = new StringBuilder("Amenities: ");
                    if(accommodationDTO.getAmenities().isEmpty()) amenities.append(" None");
                    for(int i = 0; i < accommodationDTO.getAmenities().size(); i++){
                        if(i != accommodationDTO.getAmenities().size() - 1) amenities.append(accommodationDTO.getAmenities().get(i)).append(", ");
                        else amenities.append(accommodationDTO.getAmenities().get(i));
                    }
                    binding.txtAmenities.setText(amenities);

                    String guests = "Possible guests: " + accommodationDTO.getMinGuests() + "-" +  accommodationDTO.getMaxGuests();
                    String type = "Type: " + accommodationDTO.getAccommodationType().toString();

                    binding.txtGuests.setText(guests);
                    binding.txtType.setText(type);



                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccommodationDetailsActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
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