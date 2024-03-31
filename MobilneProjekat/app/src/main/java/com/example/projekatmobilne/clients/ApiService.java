package com.example.projekatmobilne.clients;


import com.example.projekatmobilne.model.AuthenticationRequestDTO;
import com.example.projekatmobilne.model.AuthenticationResponseDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/*
 * Klasa koja opisuje putanju servisa.
 * Opisuje koji metod koristimo ali i sta ocekujemo kao rezultat
 * */
public interface ApiService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("auth/authenticate")
    Call<AuthenticationResponseDTO> authenticate(@Body AuthenticationRequestDTO request);


}
