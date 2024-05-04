package com.example.projekatmobilne.tools;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class CustomInterceptor implements okhttp3.Interceptor {
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = JWTManager.getJWT();
        Request originalRequest = chain.request();
        System.out.println("JWT JE OVO: " + token);
        if (token != null) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        } else {
            return chain.proceed(originalRequest);
        }
    }
}
