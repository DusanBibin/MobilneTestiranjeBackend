package com.example.projekatmobilne.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.AccommodationCard;
import com.example.projekatmobilne.adapters.AccommodationSearchAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentHomeBinding;
import com.example.projekatmobilne.model.Enum.Amenity;
import com.example.projekatmobilne.model.paging.PagingDTOs.AccommodationSearchDTO;
import com.example.projekatmobilne.model.paging.PagingDTOs.PagedSearchDTOResponse;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

   private FragmentHomeBinding binding;
   private LocalDate dateStart;
   private LocalDate dateEnd;

   private List<AccommodationCard> dataList;

   private AccommodationSearchAdapter adapter;

   private AccommodationCard androidData;


    public HomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.guestNumberInputEditText.setError(null);
        binding.dateRangeInputLayout.setError(null);

        binding.dateRangeInputEditText.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new androidx.core.util.Pair<>(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
            )).build();

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    String start = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                    String end = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second));
                    String display = start + "  " + end;
                    binding.dateRangeInputEditText.setText(display);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    dateStart = LocalDate.parse(start, formatter);
                    dateEnd = LocalDate.parse(end,formatter);

                }
            });


            materialDatePicker.show(getActivity().getSupportFragmentManager(), "iksde");
        });

        binding.btnSearch.setOnClickListener(v -> {

            binding.guestNumberInputLayout.setError(null);
            binding.dateRangeInputLayout.setError(null);
            String search = binding.searchAccommodations.getQuery().toString();

            boolean isValid = true;
            if (binding.guestNumberInputEditText.getText().toString().isEmpty()) {
                binding.guestNumberInputLayout.setError("This field cannot be empty");
                isValid = false;
            }

            if (binding.dateRangeInputEditText.getText().toString().isEmpty()) {
                binding.dateRangeInputLayout.setError("This field cannot be empty");
                isValid = false;
            }
            if(!isValid) return;

            Long guestNum = Long.valueOf(binding.guestNumberInputEditText.getText().toString());
            dataList = new ArrayList<>();

            Call<ResponseBody> call = ClientUtils.apiService.getAccommodationsSearch(guestNum,
                    search, dateStart, dateEnd, null, null, null,
                    null, 0, 10);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                    if(response.code() == 200){
                        PagedSearchDTOResponse responseDTO =
                                ResponseParser.parseResponse(response, PagedSearchDTOResponse.class, false);

                        if(responseDTO.getContent().isEmpty()){
                            Toast.makeText(getActivity(), "There are no accommodations that are available within this period", Toast.LENGTH_SHORT).show();
                        }




                        for(AccommodationSearchDTO a: responseDTO.getContent()){

                            StringBuilder amenities = new StringBuilder("Amenities: ");
                            if(a.getAmenities().isEmpty()) amenities.append(" None");
                            for(int i = 0; i < a.getAmenities().size(); i++){
                                if(i != a.getAmenities().size() - 1) amenities.append(a.getAmenities().get(i)).append(", ");
                                else amenities.append(a.getAmenities().get(i));
                            }
                            String guests = "Possible guests: " + a.getMinGuests() + "-" +  a.getMaxGuests();
                            String type = "Type: " + a.getAccommodationType().toString();
                            String isPerPerson = "Is price per person:" + a.getPerPerson();
                            String oneNightPrice = "One night price: " + a.getOneNightPrice();
                            String totalPrice = "Total price: " + a.getTotalPrice();
                            AccommodationCard ac = new AccommodationCard(a.getAddress(),
                                    guests,
                                    type, isPerPerson, oneNightPrice,
                                    totalPrice, a.getName(), amenities.toString(), a.getRating());
                            dataList.add(ac);




                            Call<ResponseBody> imageCall = ClientUtils.apiService.getAccommodationImage(a.getAccommodationId(), 1L);
                            imageCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.code() == 200){
                                        byte[] imageBytes;
                                        try {
                                            imageBytes = response.body().bytes();
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                            a.setImageBitmap(bitmap);
                                            ac.setImageBitmap(bitmap);
                                            System.out.println("TEK SE SAD UPALILO AJAOOO");
                                            adapter.setSearchList(dataList);
                                        }catch (IOException e){
                                            e.printStackTrace();
                                            Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                                    // You can also log the stack trace for more detailed information
                                    t.printStackTrace();
                                }
                            });
                        }


                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
                        binding.recyclerView.setLayoutManager(gridLayoutManager);


                        adapter = new AccommodationSearchAdapter(getActivity(), dataList);
                        binding.recyclerView.setAdapter(adapter);
                    }
                    if(response.code() == 400){
                        Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);

                        if(map.containsKey("message")){
                            String errMessage = map.get("message");
                            binding.dateRangeInputLayout.setError(errMessage);
                        }

                    }



                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                    // You can also log the stack trace for more detailed information
                    t.printStackTrace();
                }
            });

        });

        binding.btnFilters.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
        });


    }
}