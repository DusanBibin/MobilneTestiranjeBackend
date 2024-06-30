package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.ImagesAddAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityAccomodationsDifferencesCompareBinding;
import com.example.projekatmobilne.model.Enum.Amenity;
import com.example.projekatmobilne.model.responseDTO.AccommodationDTOEdit;
import com.example.projekatmobilne.model.responseDTO.AccommodationDifferencesDTO;
import com.example.projekatmobilne.tools.ResponseParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationsDifferencesCompareActivity extends AppCompatActivity {

    private Long requestId = 0L;
    private Toolbar toolbar;
    private ActivityAccomodationsDifferencesCompareBinding binding;
    private List<File> imagesList = new ArrayList<>();
    private ImagesAddAdapter imagesAddAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccomodationsDifferencesCompareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        LinearLayoutManager linearLayoutManagerImages = new LinearLayoutManager(AccommodationsDifferencesCompareActivity.this);
        binding.recyclerViewImagesDifferences.setLayoutManager(linearLayoutManagerImages);
        imagesAddAdapter = new ImagesAddAdapter(AccommodationsDifferencesCompareActivity.this, imagesList);
        binding.recyclerViewImagesDifferences.setAdapter(imagesAddAdapter);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("requestId")){
            requestId = (Long) intent.getSerializableExtra("requestId");


            Call<ResponseBody> call = ClientUtils.apiService.getOwnerAccommodationRequest(requestId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200){
                        AccommodationDifferencesDTO responseDTO = ResponseParser.parseResponse(response, AccommodationDifferencesDTO.class, false);

                        imagesAddAdapter.setDifferencesImagesToAdd(responseDTO.getImagesToAdd());
                        imagesAddAdapter.setDifferencesImagesToDelete(responseDTO.getImagesToRemove());
                        for(String str: responseDTO.getCurrentImages()){
                            Long imageId = Long.parseLong(String.valueOf(str.charAt(0)));
                            getImage(imageId);
                        }
                        for(String str: responseDTO.getImagesToAdd()){
                            Long imageId = Long.valueOf(String.valueOf(str.charAt(0)));
                            getImage(imageId);
                        }


                        AccommodationDTOEdit requestInfo = responseDTO.getRequestAccommodationInfo();
                        binding.txtAccommodationNameValue.setText(requestInfo.getName());
                        binding.txtDescriptionValue.setText(requestInfo.getDescription());
                        binding.txtGuestsValue.setText(requestInfo.getMinGuests() + "-" + requestInfo.getMaxGuests());

                        StringBuilder amenitiesBuilder = new StringBuilder();
                        for(int i = 0; i < requestInfo.getAmenities().size(); i++){
                            Amenity amenity = requestInfo.getAmenities().get(i);
                            amenitiesBuilder.append(amenity.toString());
                            if (requestInfo.getAmenities().size() != i + 1) {
                                amenitiesBuilder.append(", ");
                            }
                        }
                        String amenities = amenitiesBuilder.toString();

                        binding.txtAmenitiesValue.setText(amenities);
                        binding.txtTypeValue.setText(requestInfo.getAccommodationType().toString());
                        binding.txtAutoAcceptValue.setText(requestInfo.getAutoAcceptEnabled().toString());


                        if(responseDTO.getAccommodationInfo() != null){
                            AccommodationDTOEdit oldInfo = responseDTO.getAccommodationInfo();

                            if(!oldInfo.getName().equals(requestInfo.getName())){
                                fillOldTxtValue(binding.txtOldAccommodationName, binding.linearLayoutAccommodationNames,
                                        binding.txtAccommodationNameValue, oldInfo.getName());
                            }

                            if(!oldInfo.getDescription().equals(requestInfo.getDescription())){
                                fillOldTxtValue(binding.txtOldDescriptionValue, binding.linearLayoutDescription,
                                        binding.txtDescriptionValue, oldInfo.getDescription());
                            }

                            if(!(oldInfo.getMinGuests().equals(requestInfo.getMinGuests()) && oldInfo.getMaxGuests().equals(requestInfo.getMaxGuests()))){
                                fillOldTxtValue(binding.txtOldGuestsValue, binding.linearLayoutGuestNumber,
                                        binding.txtGuestsValue, oldInfo.getMinGuests() + "-" + oldInfo.getMaxGuests());
                            }

                            if(!((new HashSet<>(oldInfo.getAmenities())).equals(new HashSet<>(requestInfo.getAmenities())))){
                                amenitiesBuilder = new StringBuilder();
                                for(int i = 0; i < requestInfo.getAmenities().size(); i++){
                                    Amenity amenity = requestInfo.getAmenities().get(i);
                                    amenitiesBuilder.append(amenity.toString());
                                    if (requestInfo.getAmenities().size() != i + 1) {
                                        amenitiesBuilder.append(", ");
                                    }
                                }
                                fillOldTxtValue(binding.txtOldAmenitiesValue, binding.linearLayoutAmenities,
                                        binding.txtAmenitiesValue, amenitiesBuilder.toString());
                            }


                            if(!(oldInfo.getAccommodationType().equals(requestInfo.getAccommodationType()))){
                                fillOldTxtValue(binding.txtOldTypeValue, binding.linearLayoutType,
                                        binding.txtTypeValue, oldInfo.getAccommodationType().toString());
                            }

                            if(!(oldInfo.getAutoAcceptEnabled().equals(requestInfo.getAutoAcceptEnabled()))){
                                fillOldTxtValue(binding.txtOldAutoAcceptValue, binding.linearLayoutAutoAccept,
                                        binding.txtAutoAcceptValue, oldInfo.getAutoAcceptEnabled().toString());
                            }




                        }






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

    private void fillOldTxtValue(TextView txtOldValue,
                                 LinearLayout linearLayoutOldValues,
                                 TextView txtValue,
                                 String value) {
        int color = ContextCompat.getColor(AccommodationsDifferencesCompareActivity.this, R.color.orange);
        txtOldValue.setText(value);
        linearLayoutOldValues.setVisibility(View.VISIBLE);
        txtValue.setTextColor(color);
    }

    private void getImage(Long imageId) {
            Call<ResponseBody> imageResponse = ClientUtils.apiService.getAccommodationRequestImage(requestId, imageId);
            imageResponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        byte[] imageBytes;
                        try {
                            imageBytes = response.body().bytes();

                            String contentDisposition = response.headers().get("Content-Disposition");
                            String filename = null;
                            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                                filename = contentDisposition.split("filename=")[1].replace(";", "").replace("\"", "");
                            }
                            System.out.println(filename);;
                            File cacheDir = getApplicationContext().getCacheDir();
                            File tempFile = new File(cacheDir, filename);
                            FileOutputStream fos = new FileOutputStream(tempFile);
                            fos.write(imageBytes);
                            fos.close();

                            imagesList.add(tempFile);
                            imagesAddAdapter.notifyItemInserted(imagesList.size() - 1);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(AccommodationsDifferencesCompareActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(AccommodationsDifferencesCompareActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
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