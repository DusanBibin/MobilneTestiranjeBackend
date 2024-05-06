package com.example.projekatmobilne.clients;


import com.example.projekatmobilne.model.AuthenticationRequestDTO;
import com.example.projekatmobilne.model.RegisterRequestDTO;
import com.example.projekatmobilne.model.UserDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;


public interface ApiService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("auth/authenticate")
    Call<ResponseBody> authenticate(@Body AuthenticationRequestDTO request);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("auth/register")
    Call<ResponseBody> register(@Body RegisterRequestDTO request);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("user")
    Call<ResponseBody> getUserData();


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("user/change-info")
    Call<ResponseBody> changeUserData(@Body UserDTO request);
}
