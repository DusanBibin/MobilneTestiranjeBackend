package com.example.projekatmobilne.activities;

import static com.example.projekatmobilne.databinding.ActivityCreateAccommodationBinding.inflate;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.AvailabilitiesAdapter;
import com.example.projekatmobilne.adapters.ImagesAddAdapter;
import com.example.projekatmobilne.databinding.ActivityCreateAccommodationBinding;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.tools.ImageUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreateAccommodationActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private ActivityCreateAccommodationBinding binding;

    private LocalDate dateStart, dateEnd, dateCancel;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImage;
    private ImagesAddAdapter imagesAddAdapter;
    private AvailabilitiesAdapter availabilitiesAdapter;
    private Dialog addAvailabilityDialog;
    private TextInputLayout dateRangeInput, cancelDeadlineInput, priceInput;
    private EditText dateRangeEdit, cancelDeadlineEdit, priceEdit;
    private CheckBox checkBoxIsPerGuest;
    private List<MultipartBody.Part> imagesList = new ArrayList<>();
    private List<AvailabilityDTO> availabilitiesList = new ArrayList<>();


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


        LinearLayoutManager linearLayoutManagerImages = new LinearLayoutManager(CreateAccommodationActivity.this);
        LinearLayoutManager linearLayoutManagerAvailabilities = new LinearLayoutManager(CreateAccommodationActivity.this);
        binding.recyclerViewImages.setLayoutManager(linearLayoutManagerImages);
        binding.recyclerViewAvailabilities.setLayoutManager(linearLayoutManagerAvailabilities);

        imagesAddAdapter = new ImagesAddAdapter(CreateAccommodationActivity.this, imagesList);
        binding.recyclerViewImages.setAdapter(imagesAddAdapter);

        availabilitiesAdapter = new AvailabilitiesAdapter(CreateAccommodationActivity.this, availabilitiesList);
        binding.recyclerViewAvailabilities.setAdapter(availabilitiesAdapter);


        setupAddAvailabilityDialog();
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        Uri imageUri = result.getData().getData();
                        String filePath = ImageUtils.getFileNameFromPart(imageUri, getContentResolver());
                        File file = new File(filePath);
                        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                        imagesList.add(body);
                        imagesAddAdapter.notifyItemInserted(imagesList.size() - 1);

                    }
                });


        binding.btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imagesList.size() == 10) Toast.makeText(CreateAccommodationActivity.this, "You can only upload 10 images", Toast.LENGTH_SHORT).show();
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
    }



    private void setupDateRangePicker() {
        dateRangeEdit.setOnClickListener(v -> {
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

            if (!isValid) return;

            AvailabilityDTO availabilityDTO = new AvailabilityDTO();
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
                    String date = dayOfMonth + "-" + month + " " + year;
                    dateCancel = LocalDate.of(year, month, dayOfMonth);
                    cancelDeadlineEdit.setText(date);
                }
            }, LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
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

    private String getImageName(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        String imageName = "";
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            imageName = cursor.getString(nameIndex);
            cursor.close();
        }
        return imageName;
    }


}