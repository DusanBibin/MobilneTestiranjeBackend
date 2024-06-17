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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private ViewPager2 viewPager;
    private Long accommodationId;
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
        if(Role.OWNER.equals(Role.valueOf(JWTManager.getRole()))){
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AccommodationDetailsActivity.this, CreateAccommodationActivity.class);
                    intent.putExtra("accommodationId", accommodationId);
                    startActivity(intent);
                }
            });
        }

        calendarView = findViewById(R.id.calendarView);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        final LocalDate min = getLocalDate(java.time.LocalDate.now().plusDays(1).format(formatter));
        calendarView.state().edit().setMinimumDate(min).commit();

        binding.transparentImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        binding.scrollViewReviews.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        binding.scrollViewReviews.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        binding.scrollViewReviews.requestDisallowInterceptTouchEvent(true);
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



        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);
        binding.viewPager.setAdapter(imageAdapter);




        binding.scrollViewReviews.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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
                    setupSpinner();
                    setupCalendar();
                    setupImages();
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
                            imageList.add(bitmap);
                            imageAdapter.notifyItemInserted(imageList.size() - 1);

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
        List<LocalDate> localDateList = new ArrayList<>();

        for (String string : dateList) {
            LocalDate calendar = getLocalDate(string);
            if (calendar != null) {
                localDateList.add(calendar);
            }
        }


        List<CalendarDay> datesLeft = new ArrayList<>();
        List<CalendarDay> datesCenter = new ArrayList<>();
        List<CalendarDay> datesRight = new ArrayList<>();
        List<CalendarDay> datesIndependent = new ArrayList<>();


        for (LocalDate localDate : localDateList) {

            boolean right = false;
            boolean left = false;

            for (LocalDate day1 : localDateList) {


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
    LocalDate getLocalDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        try {
            Date input = sdf.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(input);
            return LocalDate.of(cal.get(Calendar.YEAR),
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
    }
}