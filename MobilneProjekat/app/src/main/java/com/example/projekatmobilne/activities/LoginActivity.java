// app/src/main/java/com/example/projekatmobilne/activities/LoginActivity.java
package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityLoginBinding;
import com.example.projekatmobilne.model.requestDTO.AuthenticationRequestDTO;
import com.example.projekatmobilne.model.responseDTO.AuthenticationDTOResponse;
import com.example.projekatmobilne.services.ConnectionCheckService;
import com.example.projekatmobilne.tools.JWTManager;
import com.example.projekatmobilne.tools.ResponseParser;

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
        FirebaseApp.initializeApp(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent serviceIntent = new Intent(this, ConnectionCheckService.class);
        startService(serviceIntent);

        binding.textRedirectRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.btnContinueGuest.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        binding.loginButton.setOnClickListener(v -> {
            binding.emailInputLayout.setError(null);
            binding.passwordInputLayout.setError(null);

            boolean isValid = true;
            if (binding.emailInputEditText.getText().toString().isEmpty()) {
                binding.emailInputLayout.setError("This field cannot be empty");
                isValid = false;
            }

            if (binding.passwordInputEditText.getText().toString().isEmpty()) {
                binding.passwordInputLayout.setError("This field cannot be empty");
                isValid = false;
            }
            if(!isValid) return;

            binding.loginButton.setVisibility(View.INVISIBLE);
            binding.progressBarLogin.setVisibility(View.VISIBLE);
            AuthenticationRequestDTO request = new AuthenticationRequestDTO();
            request.setEmail(binding.emailInputEditText.getText().toString());
            request.setPassword(binding.passwordInputEditText.getText().toString());

            Call<ResponseBody> call = ClientUtils.apiService.authenticate(request);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if(response.code() == 400){
                        Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                        if(map.containsKey("message")){
                            String errMessage = map.get("message");
                            binding.emailInputLayout.setError(errMessage);
                            binding.passwordInputLayout.setError(errMessage);
                        }
                        if(map.containsKey("email")){
                            binding.emailInputLayout.setError(map.get("email"));
                        }
                        if(map.containsKey("password")){
                            binding.passwordInputLayout.setError(map.get("password"));
                        }

                        binding.loginButton.setVisibility(View.VISIBLE);
                        binding.progressBarLogin.setVisibility(View.INVISIBLE);
                    }
                    if(response.code() == 200) {
                        AuthenticationDTOResponse responseDTO =
                                ResponseParser.parseResponse(response, AuthenticationDTOResponse.class, false);

                        JWTManager.saveJWT(responseDTO.getToken());
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    return;
                                }
                                String token = task.getResult();
                                System.out.println("FCM Token: " + token);
                                Long userId = Long.valueOf(JWTManager.getUserId());
                                sendFcmTokenToServer(userId, token);
                            }
                        });
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(LoginActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        });
    }

    private void sendFcmTokenToServer(Long userId, String token) {
        Call<ResponseBody> call = ClientUtils.apiService.saveFcmToken(userId, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    System.out.println("FCM Token saved successfully");
                } else {
                    System.out.println("Failed to save FCM Token");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                System.out.println("Error saving FCM Token");
                t.printStackTrace();
            }
        });
    }
}