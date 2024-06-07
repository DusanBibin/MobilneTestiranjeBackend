package com.example.projekatmobilne.activities;

import static com.example.projekatmobilne.databinding.ActivityCreateAccommodationBinding.inflate;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.ImagesAddAdapter;
import com.example.projekatmobilne.adapters.ReviewsAdapter;
import com.example.projekatmobilne.databinding.ActivityCreateAccommodationBinding;
import com.example.projekatmobilne.tools.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreateAccommodationActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private ActivityCreateAccommodationBinding binding;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImage;
    private ImagesAddAdapter imagesAddAdapter;
    List<MultipartBody.Part> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccommodationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(CreateAccommodationActivity.this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreateAccommodationActivity.this);
        binding.recyclerViewImages.setLayoutManager(linearLayoutManager);

        imagesAddAdapter = new ImagesAddAdapter(CreateAccommodationActivity.this, images);
        binding.recyclerViewImages.setAdapter(imagesAddAdapter);



        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                        selectedImage = result.getData().getData();
//                        View view = inflater.inflate(R.layout.image_add, binding.linearLayout, false);
//
//                        TextView imageName = view.findViewById(R.id.imageName);
//                        imageName.setText(getImageName(selectedImage));
//                        binding.linearLayout.addView(view);

                        Uri imageUri = result.getData().getData();
                        String filePath = ImageUtils.getFileNameFromPart(imageUri, getContentResolver());
                        File file = new File(filePath);
                        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


                        images.add(body);
                        imagesAddAdapter.notifyItemInserted(images.size() - 1);
//                        View view = inflater.inflate(R.layout.image_add, binding.linearLayout, false);
//                        TextView imageName = view.findViewById(R.id.imageName);
//                        imageName.setText(getImageName(imageUri));
//                        binding.linearLayout.addView(view);
                        System.out.println(images.size());
                    }
                });


        binding.btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(images.size() == 10) Toast.makeText(CreateAccommodationActivity.this, "You can only upload 10 images", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImageLauncher.launch(intent);
                }
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