package com.example.projekatmobilne.tools;

import android.util.Log;

import com.example.projekatmobilne.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ResponseParser {

    private static final Gson gsonCustom = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
        }
    }).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    public static <T> T parseResponse(Response<ResponseBody> response, Class<T> classOfT, Boolean isErrorResponse){

        String responseBody = "";
        try {
            if(isErrorResponse) responseBody = response.errorBody().string();
            else responseBody = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (classOfT == String.class) return (T) responseBody;
        else return gsonCustom.fromJson(responseBody, classOfT);

    }

    public static <T> T parseTypeResponse(String responseBody, Type typeOfT, Boolean isErrorResponse){
        Log.d("ResponseParser", "parseTypeResponse: " + responseBody);
        if (typeOfT == String.class) return (T) responseBody;
        else return gsonCustom.fromJson(responseBody, typeOfT);
    }
}
