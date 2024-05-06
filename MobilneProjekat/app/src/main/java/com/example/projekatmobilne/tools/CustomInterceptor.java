package com.example.projekatmobilne.tools;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.projekatmobilne.activities.LoginActivity;
import com.example.projekatmobilne.activities.RegisterActivity;

public class CustomInterceptor implements okhttp3.Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = JWTManager.getJWT();
        Request originalRequest = chain.request();




        if (token != null) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            Response response = chain.proceed(newRequest);
            JWTManager.isExpired();

            return response;
        } else {
            return chain.proceed(originalRequest);
        }
    }
}
