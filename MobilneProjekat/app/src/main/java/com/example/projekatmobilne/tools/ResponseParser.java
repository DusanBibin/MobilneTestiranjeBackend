package com.example.projekatmobilne.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ResponseParser {

    private static final Gson gsonCustom = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
        }
    }).create();

    public static <T> T parseResponse(Response<ResponseBody> response, Class<T> classOfT, Boolean isErrorResponse){

        String responseBody = "";
        try {
            if(isErrorResponse) responseBody = response.errorBody().string();
            else responseBody = response.body().string();
            System.out.println(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if (classOfT == String.class) return (T) responseBody;
        else return gsonCustom.fromJson(responseBody, classOfT);

    }


}
