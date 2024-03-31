package com.example.projekatmobilne.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityLoginBinding;
import com.example.projekatmobilne.model.AuthenticationRequestDTO;
import com.example.projekatmobilne.model.AuthenticationResponseDTO;

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

            Call<AuthenticationResponseDTO> call = ClientUtils.apiService.authenticate(request);
            call.enqueue(new Callback<AuthenticationResponseDTO>() {
                @Override
                public void onResponse(Call<AuthenticationResponseDTO> call, Response<AuthenticationResponseDTO> response) {
                    Log.i("uspeh", "uspeh");
                    System.out.println(response);
                }

                @Override
                public void onFailure(Call<AuthenticationResponseDTO> call, Throwable t) {
                    Log.i("neuspeh", "neuspeh");
                    System.out.println(t);
                }
            });
        });

    }
}