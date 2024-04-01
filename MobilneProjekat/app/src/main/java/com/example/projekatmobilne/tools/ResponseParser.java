package com.example.projekatmobilne.tools;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ResponseParser {

    public static <T> T parseResponse(Response<ResponseBody> response, Class<T> classOfT, Boolean isErrorResponse){

        String responseBody;
        try {
            if(isErrorResponse) responseBody = response.errorBody().string();
            else responseBody = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        return gson.fromJson(responseBody, classOfT);


    }
}
