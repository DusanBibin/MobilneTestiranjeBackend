package com.example.projekatmobilne.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

   private FragmentHomeBinding binding;
   private LocalDate dateStart;
   private LocalDate dateEnd;

   List<AccommodationCard> dataList;

   AccommodationSearchAdapter adapter;

   AccommodationCard androidData;

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
            String search = binding.searchAccommodations.getQuery().toString();
            Long guestNum = Long.valueOf(binding.guestNumberInputEditText.getText().toString());

            Call<ResponseBody> call = ClientUtils.apiService.getAccommodationsSearch(guestNum,
                    search, dateStart, dateEnd, null, null, null,
                    null, 0, 10);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    PagedSearchDTOResponse responseDTO =
                            ResponseParser.parseResponse(response, PagedSearchDTOResponse.class, false);
                    System.out.println(responseDTO);

                    for(AccommodationSearchDTO a: responseDTO.getContent()){

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