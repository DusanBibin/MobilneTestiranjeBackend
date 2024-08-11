package com.example.projekatmobilne.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.example.projekatmobilne.databinding.ActivityCreateAccommodationBinding.inflate;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.AvailabilitiesAdapter;
import com.example.projekatmobilne.adapters.ImagesAddAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityCreateAccommodationBinding;
import com.example.projekatmobilne.model.Enum.AccommodationType;
import com.example.projekatmobilne.model.Enum.Amenity;
import com.example.projekatmobilne.model.Enum.RequestType;
import com.example.projekatmobilne.model.requestDTO.AccommodationDTO;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.model.responseDTO.AccommodationDTOResponse;
import com.example.projekatmobilne.model.responseDTO.innerDTO.AvailabilityDTOInner;
import com.example.projekatmobilne.tools.ImageUtils;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class CreateAccommodationActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int REQUEST_PERMISSION_CODE = 1;
    private Toolbar toolbar;
    private ActivityCreateAccommodationBinding binding;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private LocalDate dateStart, dateEnd, dateCancel;
    private Address address;
    private AccommodationType accommodationType = AccommodationType.STUDIO;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImage;
    private ImagesAddAdapter imagesAddAdapter;
    private AvailabilitiesAdapter availabilitiesAdapter;
    private Dialog addAvailabilityDialog;
    private TextInputLayout dateRangeInput, cancelDeadlineInput, priceInput;
    private EditText dateRangeEdit, cancelDeadlineEdit, priceEdit;
    private CheckBox checkBoxIsPerGuest;
    private List<File> imagesList = new ArrayList<>();
    private AccommodationDTO accommodation;
    private List<AvailabilityDTO> availabilitiesList = new ArrayList<>();
    private Long accommodationId = 0L;
    private AccommodationDTOResponse accommodationDTO;
    private Long newAvailabilityCreateId = 1L;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccommodationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //LayoutInflater inflater = LayoutInflater.from(CreateAccommodationActivity.this);


        if (!checkPermissions()) requestPermissions();

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("accommodationId")){
            accommodationId = (Long) intent.getSerializableExtra("accommodationId");
        }


        LinearLayoutManager linearLayoutManagerImages = new LinearLayoutManager(CreateAccommodationActivity.this);
        LinearLayoutManager linearLayoutManagerAvailabilities = new LinearLayoutManager(CreateAccommodationActivity.this);
        binding.recyclerViewImages.setLayoutManager(linearLayoutManagerImages);
        binding.recyclerViewAvailabilities.setLayoutManager(linearLayoutManagerAvailabilities);

        imagesAddAdapter = new ImagesAddAdapter(CreateAccommodationActivity.this, imagesList);
        binding.recyclerViewImages.setAdapter(imagesAddAdapter);

        availabilitiesAdapter = new AvailabilitiesAdapter(CreateAccommodationActivity.this, availabilitiesList, getSupportFragmentManager());
        binding.recyclerViewAvailabilities.setAdapter(availabilitiesAdapter);



        setupAddAvailabilityDialog();
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        Uri imageUri = result.getData().getData();
                        String filePath = ImageUtils.getFileNameFromPart(imageUri, getContentResolver());

                        File file = new File(filePath);

                        imagesList.add(file);
                        imagesAddAdapter.addNewImage(file);
                        imagesAddAdapter.notifyItemInserted(imagesList.size() - 1);
                    }
                });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(CreateAccommodationActivity.this);
        geocoder = new Geocoder(this);
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSearch.setVisibility(View.GONE);
                binding.progressBarAddressSearch.setVisibility(View.VISIBLE);
                try {
                    String addressStr = binding.searchView.getQuery().toString();
                    if(addressStr.equals("")){
                        Toast.makeText(CreateAccommodationActivity.this, "This field cannot be empty", Toast.LENGTH_SHORT).show();
                        binding.btnSearch.setVisibility(View.VISIBLE);
                        binding.progressBarAddressSearch.setVisibility(View.GONE);
                    }else{
                        List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);

                        binding.btnSearch.setVisibility(View.VISIBLE);
                        binding.progressBarAddressSearch.setVisibility(View.GONE);
                        if(addresses.isEmpty()) {Toast.makeText(CreateAccommodationActivity.this, "There are no results", Toast.LENGTH_SHORT).show(); return;}
                        address = addresses.get(0);

                        String addressFull = address.getAddressLine(0) + " " + address.getLocality() + " " + address.getAdminArea() + " " + address.getCountryName();
                        binding.txtAddress.setText(addressFull);
                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                        if(currentMarker == null){
                            MarkerOptions markerOptions = new MarkerOptions().position(location);
                            currentMarker = mMap.addMarker(markerOptions);
                        }else{
                            currentMarker.setPosition(location);
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.5f));


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
        binding.btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imagesList.size() == 5) Toast.makeText(CreateAccommodationActivity.this, "You can only upload 5 images", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImageLauncher.launch(intent);
                }
            }
        });

        binding.btnAddAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAvailabilityDialog.show();
                dateRangeInput.setError(null);
                cancelDeadlineInput.setError(null);
                priceInput.setError(null);
                checkBoxIsPerGuest.setChecked(true);

                dateRangeEdit.setText(null);
                cancelDeadlineEdit.setText(null);
                priceEdit.setText(null);

            }
        });

        binding.transparentImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        binding.nestedScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        binding.nestedScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        binding.nestedScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });



        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = checkInputs();

                if(!isValid) {Toast.makeText(CreateAccommodationActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show(); return;}

                binding.btnConfirm.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                accommodation = new AccommodationDTO();
                accommodation.setId(0L);
                accommodation.setName(binding.inputEditTextAccommodationName.getText().toString());
                accommodation.setDescription(binding.inputEditTextDetails.getText().toString());
                accommodation.setAddress(binding.txtAddress.getText().toString());
                Double lat, lon;
                if(accommodationId == 0) {lat = address.getLatitude(); lon = address.getLongitude();}
                else {lat = accommodationDTO.getLat(); lon = accommodationDTO.getLon();}
                accommodation.setLat(lat);
                accommodation.setLon(lon);

                List<Amenity> amenities = new ArrayList<>();
                if(binding.checkBoxAC.isChecked()) amenities.add(Amenity.AC);
                if(binding.checkBoxParking.isChecked()) amenities.add(Amenity.PARKING);
                if(binding.checkBoxPool.isChecked()) amenities.add(Amenity.POOL);
                if(binding.checkBoxWifi.isChecked()) amenities.add(Amenity.WIFI);
                accommodation.setAmenities(amenities);

                accommodation.setMinGuests(Long.valueOf(binding.inputEditTextMinGuests.getText().toString()));
                accommodation.setMaxGuests(Long.valueOf(binding.inputEditTextMaxGuests.getText().toString()));
                accommodation.setAccommodationType(accommodationType);
                accommodation.setAvailabilityList(availabilitiesAdapter.getEditList());
                accommodation.setImagesToDelete(imagesAddAdapter.getImagesToDelete());

                for(File iksde: imagesAddAdapter.getImagesToAdd()){
                    System.out.println(iksde.getName());
                }
                List<MultipartBody.Part> images = prepareFilePart("images", imagesAddAdapter.getImagesToAdd());
                System.out.println("broj slika je:" );
                System.out.println(images.size());
                Call<ResponseBody> call;
                if(accommodationId == 0) call = ClientUtils.apiService.createNewAccommodationRequest(accommodation, images);
                else call = ClientUtils.apiService.createNewEditAccommodationRequest(accommodation, images, accommodationId);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.code() == 200){
                            String responseMessage = ResponseParser.parseResponse(response, String.class, false);
                            Toast.makeText(CreateAccommodationActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                            getOnBackPressedDispatcher().onBackPressed();
                            finish();
                        }
                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")) Toast.makeText(CreateAccommodationActivity.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            binding.btnConfirm.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(CreateAccommodationActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });

        binding.radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = group.findViewById(checkedId);
                String accommodationTypeStr = selectedRadioButton.getText().toString();
                accommodationType = AccommodationType.valueOf(accommodationTypeStr.toUpperCase());
            }
        });

        if(accommodationId != 0) getAccommodationForEditing();
    }

    private void getAccommodationForEditing() {
        binding.nestedScrollView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = ClientUtils.apiService.getAccommodation(accommodationId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 400){
                    Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);

                    if(map.containsKey("message")){
                        String errMessage = map.get("message");
                        Toast.makeText(CreateAccommodationActivity.this, errMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                if(response.code() == 200){
                    accommodationDTO = ResponseParser.parseResponse(response, AccommodationDTOResponse.class, false);
                    getImages();
                    binding.inputEditTextAccommodationName.setText(accommodationDTO.getName());
                    binding.inputEditTextDetails.setText(accommodationDTO.getDescription());
                    binding.inputEditTextMinGuests.setText(accommodationDTO.getMinGuests().toString());
                    binding.inputEditTextMaxGuests.setText(accommodationDTO.getMaxGuests().toString());
                    accommodationType = accommodationDTO.getAccommodationType();
                    binding.radioButtonStudio.setChecked(accommodationType.equals(AccommodationType.STUDIO));
                    binding.radioButtonRoom.setChecked(accommodationType.equals(AccommodationType.ROOM));
                    binding.radioButtonApartment.setChecked(accommodationType.equals(AccommodationType.APARTMENT));
                    for(Amenity amenity: accommodationDTO.getAmenities()){
                        if(amenity.equals(Amenity.AC)) binding.checkBoxAC.setChecked(true);
                        if(amenity.equals(Amenity.PARKING)) binding.checkBoxParking.setChecked(true);
                        if(amenity.equals(Amenity.POOL)) binding.checkBoxPool.setChecked(true);
                        if(amenity.equals(Amenity.WIFI)) binding.checkBoxWifi.setChecked(true);
                    }


                    binding.searchView.setQuery(accommodationDTO.getAddress(), false);
                    binding.txtAddress.setText(accommodationDTO.getAddress());
                    LatLng location = new LatLng(accommodationDTO.getLat(), accommodationDTO.getLon());

                    if(currentMarker == null){
                        MarkerOptions markerOptions = new MarkerOptions().position(location);
                        currentMarker = mMap.addMarker(markerOptions);
                    }else{
                        currentMarker.setPosition(location);
                    }


                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.5f));

                    for(AvailabilityDTO avail: accommodationDTO.getAvailabilityList()){
                        avail.setRequestType(RequestType.EDIT);
                        availabilitiesList.add(avail);
                        availabilitiesAdapter.notifyItemInserted(availabilitiesList.size() - 1);
                    }
                }
                binding.nestedScrollView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CreateAccommodationActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void getImages() {
        for(Long imageId: accommodationDTO.getImageIds()){
            Call<ResponseBody> imageResponse = ClientUtils.apiService.getAccommodationImage(accommodationId, imageId);
            imageResponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200){
                        byte[] imageBytes;
                        try {
                            imageBytes = response.body().bytes();

                            String contentDisposition = response.headers().get("Content-Disposition");
                            String filename = null;
                            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                                filename = contentDisposition.split("filename=")[1].replace(";", "").replace("\"", "");
                            }
                            System.out.println(filename);
                            File cacheDir = getApplicationContext().getCacheDir();
                            File tempFile = new File(cacheDir, filename);
                            FileOutputStream fos = new FileOutputStream(tempFile);
                            fos.write(imageBytes);
                            fos.close();

                            imagesList.add(tempFile);
                            imagesAddAdapter.notifyItemInserted(imagesList.size() - 1);

                        }catch (IOException e){
                            e.printStackTrace();
                            Toast.makeText(CreateAccommodationActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CreateAccommodationActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }
    }

    public static List<MultipartBody.Part> prepareFilePart(String partName, List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (File file : files) {
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    file
            );
            MultipartBody.Part body = MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
            parts.add(body);
        }
        return parts;
    }

    private boolean checkInputs() {
        binding.inputLayoutAccommodationName.setError(null);
        binding.inputLayoutDetails.setError(null);
        binding.inputLayoutMinGuests.setError(null);
        binding.inputLayoutMaxGuests.setError(null);

        boolean isValid = true;
        if(binding.inputEditTextAccommodationName.getText().toString().isEmpty()){
            binding.inputLayoutAccommodationName.setError("Name must be provided");isValid = false;
        }

        if(binding.inputEditTextDetails.getText().toString().isEmpty()){
            binding.inputLayoutDetails.setError("Details must be provided");isValid = false;
        }

        Long min = 1L, max = 1L;
        if(binding.inputEditTextMinGuests.getText().toString().isEmpty()){
            binding.inputLayoutMinGuests.setError("Min guests must be provided");isValid = false;
        }else{
            min = Long.valueOf(binding.inputEditTextMinGuests.getText().toString());
        }
        if(binding.inputEditTextMaxGuests.getText().toString().isEmpty()){
            binding.inputLayoutMaxGuests.setError("Max guests must be provided");isValid = false;
        }else{
            max = Long.valueOf(binding.inputEditTextMaxGuests.getText().toString());
        }

        if(max < min || max == 0L || min == 0L) {
            binding.inputLayoutMaxGuests.setError("Max value cannot be lesser than min value");
            binding.inputLayoutMinGuests.setError("Max value cannot be lesser than min value");
            isValid = false;
        }

        if(binding.txtAddress.getText().toString().isEmpty()) isValid = false;

        if(availabilitiesList.isEmpty()) isValid = false;
        if(imagesList.isEmpty()) isValid = false;

        return isValid;
    }


    private void setupDateRangePicker() {
        dateRangeEdit.setOnClickListener(v -> {

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

                    dateStart = LocalDate.parse(start, formatter);
                    if(dateStart.equals(LocalDate.now()) || dateStart.isBefore(LocalDate.now())){
                        Toast.makeText(CreateAccommodationActivity.this, "Start date needs to be in the future", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dateEnd = LocalDate.parse(end,formatter);
                    dateRangeEdit.setText(display);
                }
            });

            materialDatePicker.show(getSupportFragmentManager(), "iksde");
        });
    }

    private void setupAddAvailabilityDialog(){
        addAvailabilityDialog = new Dialog(this);
        addAvailabilityDialog.setContentView(R.layout.custom_dialog_availability);
        addAvailabilityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addAvailabilityDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.custom_dialog_bg));
        
        dateRangeEdit = addAvailabilityDialog.findViewById(R.id.inputEditTextDateRange);
        cancelDeadlineEdit = addAvailabilityDialog.findViewById(R.id.inputEditTextDateCancel);
        priceEdit = addAvailabilityDialog.findViewById(R.id.inputEditTextPricePerGuest);
        dateRangeInput = addAvailabilityDialog.findViewById(R.id.inputLayoutDateRange);
        cancelDeadlineInput = addAvailabilityDialog.findViewById(R.id.inputLayoutDateCancel);
        priceInput = addAvailabilityDialog.findViewById(R.id.inputLayoutPricePerGuest);

        checkBoxIsPerGuest = addAvailabilityDialog.findViewById(R.id.checkBoxIsPerGuest);


        addAvailabilityDialog.findViewById(R.id.btnCancelInfo).setOnClickListener(v -> {
            addAvailabilityDialog.dismiss();
        });

        addAvailabilityDialog.findViewById(R.id.btnConfirmInfo).setOnClickListener(v -> {

            dateRangeInput.setError(null);
            cancelDeadlineInput.setError(null);
            priceInput.setError(null);

            boolean isValid = true;
            if (dateRangeEdit.getText().toString().isEmpty()) {
                dateRangeInput.setError("This field cannot be empty");
                isValid = false;
            }
            if (priceEdit.getText().toString().isEmpty() || Long.parseLong(priceEdit.getText().toString()) == 0) {
                priceInput.setError("This field cannot be empty");
                isValid = false;
            }
            if (cancelDeadlineEdit.getText().toString().isEmpty()) {
                cancelDeadlineInput.setError("This field cannot be empty");
                isValid = false;
            }
            if(!isValid) return;
            for(AvailabilityDTO a: availabilitiesList){
                if((dateStart.isAfter(a.getStartDate()) && dateStart.isBefore(a.getEndDate()))
                        || dateStart.isEqual(a.getStartDate()) || dateStart.isEqual(a.getEndDate())){
                    isValid = false;
                    Toast.makeText(CreateAccommodationActivity.this, "Date range interferes with existing availability", Toast.LENGTH_SHORT).show();
                }
                if((dateEnd.isAfter(a.getStartDate()) && dateEnd.isBefore(a.getEndDate()))
                        || dateEnd.isEqual(a.getStartDate()) || dateEnd.isEqual(a.getEndDate())){
                    isValid = false;
                    Toast.makeText(CreateAccommodationActivity.this, "Date range interferes with existing availability", Toast.LENGTH_SHORT).show();
                }
            }

            if(!dateCancel.isBefore(dateStart)){
                isValid = false;
                Toast.makeText(CreateAccommodationActivity.this, "Cancellation date must be before start date", Toast.LENGTH_SHORT).show();
            }

            if (!isValid) return;


            AvailabilityDTO availabilityDTO = new AvailabilityDTO();
            availabilityDTO.setRequestType(RequestType.CREATE);
            availabilityDTO.setId(newAvailabilityCreateId -= 1);
            availabilityDTO.setPrice(Long.parseLong(priceEdit.getText().toString()));
            availabilityDTO.setStartDate(dateStart);
            availabilityDTO.setEndDate(dateEnd);
            availabilityDTO.setCancellationDeadline(dateCancel);
            availabilityDTO.setPricePerGuest(checkBoxIsPerGuest.isChecked());
            availabilitiesList.add(availabilityDTO);
            availabilitiesAdapter.notifyItemInserted(availabilitiesList.size() - 1);
            addAvailabilityDialog.dismiss();
        });


        setupDateRangePicker();
        cancelDeadlineEdit.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String date = dayOfMonth + "-" + (month+1) + "-" + year;
                    dateCancel = LocalDate.of(year, month+1, dayOfMonth);
                    cancelDeadlineEdit.setText(date);
                }
            }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());

            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            dialog.show();
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }


    public boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(CreateAccommodationActivity.this, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}