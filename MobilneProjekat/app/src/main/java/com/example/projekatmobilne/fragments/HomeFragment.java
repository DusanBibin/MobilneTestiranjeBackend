package com.example.projekatmobilne.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.AccommodationCard;
import com.example.projekatmobilne.adapters.AccommodationSearchAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentHomeBinding;
import com.example.projekatmobilne.model.Enum.AccommodationType;
import com.example.projekatmobilne.model.Enum.Amenity;
import com.example.projekatmobilne.model.paging.PagingDTOs.AccommodationSearchDTO;
import com.example.projekatmobilne.model.paging.PagingDTOs.PagedSearchDTOResponse;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

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
   private LocalDate dateStart, dateEnd;

   private List<AccommodationCard> dataList;

   private AccommodationSearchAdapter adapter;

   private AccommodationCard androidData;

   private TextInputEditText editTextMaxValue, editTextMinValue;
   private AccommodationType type;
   private View dialogView;

   private CheckBox checkBoxWifi, checkBoxAC, checkBoxPool, checkBoxParking;

    private Integer currentPage = 0;
    private Boolean isLastPage = false;
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.FullScreenBottomSheetDialog);

        dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        bottomSheetDialog.setContentView(dialogView);

        setupRadioGroup();
        setupDateRangePicker();

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLastPage && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                    currentPage++;
                    loadPage();
                }
            }
        });


        binding.btnSearch.setOnClickListener(v -> {

            if(!inputsValid()) return;
            dataList = new ArrayList<>();
            isLastPage = false;
            currentPage = 0;
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
            binding.recyclerView.setLayoutManager(gridLayoutManager);


            adapter = new AccommodationSearchAdapter(getActivity(), dataList);
            binding.recyclerView.setAdapter(adapter);
            loadPage();

        });

        binding.btnFilters.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });


    }

    private void loadPage() {
        Long guestNum = Long.valueOf(binding.guestNumberInputEditText.getText().toString());
        String search = binding.searchAccommodations.getQuery().toString();

        Long maxValue = null;
        Long minValue = null;
        if(editTextMinValue.getText() != null && !editTextMinValue.getText().toString().isEmpty()) minValue = Long.parseLong(editTextMinValue.getText().toString());
        if(editTextMaxValue.getText() != null && !editTextMaxValue.getText().toString().isEmpty()) maxValue = Long.parseLong(editTextMaxValue.getText().toString());

        Call<ResponseBody> call = ClientUtils.apiService.getAccommodationsSearch(guestNum,
                search, dateStart, dateEnd, getCheckBoxAmenities(), type, minValue,
                maxValue, currentPage, 10);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if(response.code() == 200){
                    PagedSearchDTOResponse responseDTO = ResponseParser.parseResponse(response, PagedSearchDTOResponse.class, false);

                    if(responseDTO.getContent().isEmpty()){
                        Toast.makeText(getActivity(), "There are no accommodations that are available within this period", Toast.LENGTH_SHORT).show();
                    }

                    isLastPage = responseDTO.isLast();
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
                                totalPrice, a.getName(), amenities.toString(), a.getRating(), a.getAccommodationId());
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
    }

    private void setupRadioGroup() {

        checkBoxWifi = dialogView.findViewById(R.id.checkBoxWifi);
        checkBoxAC = dialogView.findViewById(R.id.checkBoxAC);
        checkBoxPool = dialogView.findViewById(R.id.checkBoxPool);
        checkBoxParking = dialogView.findViewById(R.id.checkBoxParking);


        editTextMaxValue = dialogView.findViewById(R.id.inputEditTextMaxPrice);
        editTextMinValue = dialogView.findViewById(R.id.inputEditTextMinPrice);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupType);
        type = null;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = dialogView.findViewById(checkedId);
                String selectedText = selectedRadioButton.getText().toString();
                if(selectedText.equals("Any")) type = null;
                else type = AccommodationType.valueOf(selectedText.toUpperCase());
            }
        });
    }

    private Boolean inputsValid() {
        binding.guestNumberInputLayout.setError(null);
        binding.dateRangeInputLayout.setError(null);


        boolean isValid = true;
        if (binding.guestNumberInputEditText.getText().toString().isEmpty()) {
            binding.guestNumberInputLayout.setError("This field cannot be empty");
            isValid = false;
        }

        if (binding.dateRangeInputEditText.getText().toString().isEmpty()) {
            binding.dateRangeInputLayout.setError("This field cannot be empty");
            isValid = false;
        }
        if(!isValid) return false;


        dataList = new ArrayList<>();
        return true;
    }

    private void setupDateRangePicker() {
        binding.guestNumberInputEditText.setError(null);
        binding.dateRangeInputLayout.setError(null);
        binding.dateRangeInputEditText.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
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
    }

    private List<Amenity> getCheckBoxAmenities(){
        List<Amenity> amenityList = new ArrayList<>();
        if(checkBoxParking.isChecked()) amenityList.add(Amenity.PARKING);
        if(checkBoxPool.isChecked()) amenityList.add(Amenity.POOL);
        if(checkBoxAC.isChecked()) amenityList.add(Amenity.AC);
        if(checkBoxWifi.isChecked()) amenityList.add(Amenity.WIFI);

        return amenityList;
    }
}