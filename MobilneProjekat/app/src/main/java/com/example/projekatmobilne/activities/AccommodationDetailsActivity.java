package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.ImageAdapter;
import com.example.projekatmobilne.adapters.ReviewsAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityAccommodationDetailsBinding;
import com.example.projekatmobilne.model.Enum.Role;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.model.requestDTO.ReservationDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.ReviewsDTOPagedResponse;
import com.example.projekatmobilne.model.responseDTO.AccommodationDTOResponse;
import com.example.projekatmobilne.model.responseDTO.innerDTO.AvailabilityDTOInner;
import com.example.projekatmobilne.model.responseDTO.innerDTO.ReservationDTOInner;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.ReviewDTOPageItem;
import com.example.projekatmobilne.tools.EventDecorator;
import com.example.projekatmobilne.tools.JWTManager;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;



import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private ActivityAccommodationDetailsBinding binding;
    private LocalDate dateStart, dateEnd;
    private Long accommodationId = 0L;
    private AccommodationDTOResponse accommodationDTO;
    private ReviewsDTOPagedResponse reviewsDTOPagedResponse;
    private List<String> pinkDateList;
    List<String> grayDateList;
    final String DATE_FORMAT = "yyyy-MM-dd";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    int pink = 0;
    int gray = 1;
    private Integer currentPage = 0;
    private Boolean isLastPage = false;

    private ReviewsAdapter reviewsAdapter;
    private List<ReviewDTOPageItem> dataList = new ArrayList<>();
    private List<Bitmap> imageList;
    private List<String> options;
    private ImageAdapter imageAdapter;
    private Boolean mapReady = false, imagesReady = false;
    MaterialCalendarView calendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccommodationDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        binding.txtNoRatings.setVisibility(View.GONE);
        binding.btnEdit.setVisibility(View.GONE);
        binding.linearLayoutCreateReservation.setVisibility(View.GONE);


        calendarView = findViewById(R.id.calendarView);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        final org.threeten.bp.LocalDate min = getLocalDate(java.time.LocalDate.now().plusDays(1).format(formatter));
        calendarView.state().edit().setMinimumDate(min).commit();

        setupDateRangePicker();

        binding.transparentImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        binding.scrollViewAccommodationDetails.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        binding.scrollViewAccommodationDetails.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        binding.scrollViewAccommodationDetails.requestDisallowInterceptTouchEvent(true);
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

        if(Role.OWNER.equals(JWTManager.getRoleEnum()) && accommodationId != 0L){
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!accommodationDTO.getFutureReservations().isEmpty()){
                        Toast.makeText(AccommodationDetailsActivity.this, "You cannot change reservation while there are future reservations", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(AccommodationDetailsActivity.this, CreateAccommodationActivity.class);
                    intent.putExtra("accommodationId", accommodationId);
                    startActivity(intent);
                }
            });
        }

        if(Role.GUEST.equals(JWTManager.getRoleEnum()) && accommodationId != 0L){
            binding.linearLayoutCreateReservation.setVisibility(View.VISIBLE);
            binding.btnCreateReservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    binding.inputLayoutDateNumberOfGuests.setError(null);
                    binding.inputLayoutDateRange.setError(null);

                    boolean isValid = true;
                    if(binding.inputEditTextDateRange.getText().toString().isEmpty()){
                        binding.inputLayoutDateRange.setError("This field cannot be empty");
                        isValid = false;
                    }


                    if(binding.inputEditTextNumberOfGuests.getText().toString().isEmpty() || binding.inputEditTextNumberOfGuests.getText().toString().equals("0")){
                        binding.inputLayoutDateNumberOfGuests.setError("This field cannot be empty");
                        isValid = false;
                    }

                    if(!isValid) return;

                    Long guestNum = Long.valueOf(binding.inputEditTextNumberOfGuests.getText().toString());
                    Call<ResponseBody> call = ClientUtils.apiService.createNewReservation(accommodationId, new ReservationDTO(dateStart, dateEnd, guestNum));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            if(response.code() == 200){
                                String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                                Toast.makeText(AccommodationDetailsActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                            }

                            if(response.code() == 400){
                                Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                                if(map.containsKey("message")) Toast.makeText(AccommodationDetailsActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            }



                            binding.progressBarCreateReservation.setVisibility(View.GONE);
                            binding.linearLayoutCreateReservation.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(AccommodationDetailsActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
                    binding.progressBarCreateReservation.setVisibility(View.VISIBLE);
                    binding.linearLayoutCreateReservation.setVisibility(View.GONE);
                }
            });
        }



        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);
        binding.viewPager.setAdapter(imageAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.txtImagesNumber.setText((position + 1) + "/" + imageList.size());
            }
        });


        binding.scrollViewAccommodationDetails.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(v.getChildAt(v.getChildCount() - 1) != null){
                    if (!isLastPage && scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())){
                        currentPage++;
                        loadReviewPage();
                    }
                }
            }
        });


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
                    loadReviewPage();
                    accommodationDTO = ResponseParser.parseResponse(response, AccommodationDTOResponse.class, false);

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapview);
                    mapFragment.getMapAsync(AccommodationDetailsActivity.this);
                    setupImages();
                    setupSpinner();
                    setupCalendar();
                    setupTextViews();

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccommodationDetailsActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }

    private void setupDateRangePicker() {
        binding.inputEditTextDateRange.setOnClickListener(v -> {

            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(calendar.getTimeInMillis()));
            CalendarConstraints constraints = constraintsBuilder.build();

            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                    null,
                    null
            )).setCalendarConstraints(constraints).build();


            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    String start = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                    String end = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second));
                    String display = start + "  " + end;


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    dateStart = java.time.LocalDate.parse(start, formatter);
                    if(dateStart.equals(java.time.LocalDate.now()) || dateStart.isBefore(java.time.LocalDate.now())){
                        Toast.makeText(AccommodationDetailsActivity.this, "Start date needs to be in the future", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dateEnd = java.time.LocalDate.parse(end,formatter);
                    binding.inputEditTextDateRange.setText(display);
                }
            });

            materialDatePicker.show(getSupportFragmentManager(), "iksde");
        });
    }


    private void setupSpinner() {
        options = new ArrayList<>();
        for(AvailabilityDTO a: accommodationDTO.getAvailabilityList()){
            options.add(a.getPrice().toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AccommodationDetailsActivity.this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinner.setAdapter(adapter);
    }

    private void setupCalendar() {
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                pinkDateList = new ArrayList<>();
                grayDateList = new ArrayList<>();

                calendarView.removeDecorators();
                String selectedOption = options.get(position);
                for(AvailabilityDTO a: accommodationDTO.getAvailabilityList()){

                    if(selectedOption.equals(a.getPrice().toString())){

                        long numDays = ChronoUnit.DAYS.between(a.getStartDate(), a.getEndDate()) + 1;
                        for(int i = 0; i < numDays; i++){
                            pinkDateList.add(a.getStartDate().plusDays(i).format(formatter));
                        }

                        for(ReservationDTOInner r: accommodationDTO.getFutureReservations()){
                            if(Objects.equals(r.getAvailabilityId(), a.getId())){
                                long numDaysRes = ChronoUnit.DAYS.between(r.getReservationStartDate(), r.getReservationEndDate()) + 1;
                                for(int i = 0; i < numDaysRes; i++){
                                    grayDateList.add(r.getReservationStartDate().plusDays(i).format(formatter));
                                }
                            }
                        }

                    }
                }
                setEvent(pinkDateList, pink);
                setEvent(grayDateList, gray);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void setupImages() {
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

                            synchronized(imageList) {
                                imageList.add(bitmap);
                                imageAdapter.notifyItemInserted(imageList.size() - 1);

                                if(accommodationDTO.getImageIds().size() == imageList.size()){
                                    runOnUiThread(() -> {
                                        binding.txtImagesNumber.setText("1/" + imageList.size());
                                        imagesReady = true;
                                        if(mapReady && imagesReady){
                                            binding.progressBar.setVisibility(View.GONE);
                                            binding.scrollViewAccommodationDetails.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
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
    }

    private void setupTextViews() {
        binding.txtName.setText(accommodationDTO.getName());
        binding.txtAddressDetails.setText(accommodationDTO.getAddress());
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

    private void loadReviewPage() {
        binding.progressBarReviews.setVisibility(View.VISIBLE);
        binding.linearLayoutReviews.setVisibility(View.GONE);
        Call<ResponseBody> callReviews = ClientUtils.apiService.getReviews(accommodationId, currentPage, 1);
        callReviews.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                reviewsDTOPagedResponse = ResponseParser.parseResponse(response, ReviewsDTOPagedResponse.class, false);


                dataList.addAll(reviewsDTOPagedResponse.getContent());
                isLastPage = reviewsDTOPagedResponse.isLast();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(AccommodationDetailsActivity.this, 1);
                binding.recyclerViewDetails.setLayoutManager(gridLayoutManager);


                reviewsAdapter = new ReviewsAdapter(AccommodationDetailsActivity.this, dataList);
                binding.recyclerViewDetails.setAdapter(reviewsAdapter);

                if(dataList.isEmpty()) binding.txtNoRatings.setVisibility(View.VISIBLE);

                binding.progressBarReviews.setVisibility(View.GONE);
                binding.linearLayoutReviews.setVisibility(View.VISIBLE);
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

    void setEvent(List<String> dateList, int color) {
        List<org.threeten.bp.LocalDate> localDateList = new ArrayList<>();

        for (String string : dateList) {
            org.threeten.bp.LocalDate calendar = getLocalDate(string);
            if (calendar != null) {
                localDateList.add(calendar);
            }
        }


        List<CalendarDay> datesLeft = new ArrayList<>();
        List<CalendarDay> datesCenter = new ArrayList<>();
        List<CalendarDay> datesRight = new ArrayList<>();
        List<CalendarDay> datesIndependent = new ArrayList<>();


        for (org.threeten.bp.LocalDate localDate : localDateList) {

            boolean right = false;
            boolean left = false;

            for (org.threeten.bp.LocalDate day1 : localDateList) {


                if (localDate.isEqual(day1.plusDays(1))) {
                    left = true;
                }
                if (day1.isEqual(localDate.plusDays(1))) {
                    right = true;
                }
            }

            if (left && right) {
                datesCenter.add(CalendarDay.from(localDate));
            } else if (left) {
                datesLeft.add(CalendarDay.from(localDate));
            } else if (right) {
                datesRight.add(CalendarDay.from(localDate));
            } else {
                datesIndependent.add(CalendarDay.from(localDate));
            }
        }

        if (color == pink) {
            setDecor(datesCenter, R.drawable.p_center);
            setDecor(datesLeft, R.drawable.p_left);
            setDecor(datesRight, R.drawable.p_right);
            setDecor(datesIndependent, R.drawable.p_independent);
        } else {
            setDecor(datesCenter, R.drawable.g_center);
            setDecor(datesLeft, R.drawable.g_left);
            setDecor(datesRight, R.drawable.g_right);
            setDecor(datesIndependent, R.drawable.g_independent);
        }
    }
    void setDecor(List<CalendarDay> calendarDayList, int drawable) {
        calendarView.addDecorators(new EventDecorator(AccommodationDetailsActivity.this
                , drawable
                , calendarDayList));
    }

    private org.threeten.bp.LocalDate getLocalDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        try {
            Date input = sdf.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(input);
            return org.threeten.bp.LocalDate.of(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));


        } catch (NullPointerException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng accommodationLocation = new LatLng(accommodationDTO.getLat(), accommodationDTO.getLon());
        googleMap.addMarker(new MarkerOptions()
                .position(accommodationLocation));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(accommodationLocation, 12.5f));
        mapReady = true;
        if(mapReady && imagesReady){
            binding.progressBar.setVisibility(View.GONE);
            binding.scrollViewAccommodationDetails.setVisibility(View.VISIBLE);
        }
    }
}