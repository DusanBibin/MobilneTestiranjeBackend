package com.example.projekatmobilne.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityLoginBinding;
import com.example.projekatmobilne.model.AuthenticationRequestDTO;
import com.example.projekatmobilne.model.AuthenticationResponseDTO;
import com.example.projekatmobilne.tools.JWTManager;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();

        setContentView(binding.getRoot());

        binding.button.setOnClickListener(v -> {

            AuthenticationRequestDTO request = new AuthenticationRequestDTO();
            request.setEmail(binding.editTextTextEmailAddress.getText().toString());
            request.setPassword(binding.editTextTextPassword.getText().toString());

            Call<ResponseBody> call = ClientUtils.apiService.authenticate(request);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if(response.code() == 400){

                        Map map = ResponseParser.parseResponse(response, Map.class , true);

                        System.out.println("kuraci");
                        System.out.println(map);

                    }
                    if(response.code() == 200) {
                        AuthenticationResponseDTO responseDTO =
                                ResponseParser.parseResponse(response, AuthenticationResponseDTO.class, false);

                          JWTManager.saveJWT(getApplicationContext(), responseDTO.getToken());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.i("neuspeh", "neuspeh");
                }
            });
        });

    }
}