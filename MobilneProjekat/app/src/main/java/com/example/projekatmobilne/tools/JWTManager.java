package com.example.projekatmobilne.tools;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JWTManager {
    private static SharedPreferences sharedPreferences;

    public static void setup(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static JSONObject decodeJWT(String jwtToken) {
        try {
            String[] jwtParts = jwtToken.split("\\.");
            String jwtPayload = jwtParts[1];


            byte[] decodedBytes = Base64.decode(jwtPayload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes);


            return new JSONObject(decodedPayload);
        } catch (JSONException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static void saveJWT(String jwtToken) {
        sharedPreferences.edit().putString("jwt", jwtToken).apply();

        saveUserData(jwtToken);
    }

    public static String getJWT() {
        return sharedPreferences.getString("jwt", null);
    }

    private static void saveUserData(String jwtToken) {
        JSONObject userData = decodeJWT(jwtToken);


        try {
            sharedPreferences.edit().putString("id", userData.getString("id")).apply();

            sharedPreferences.edit().putString("email", userData.getString("sub")).apply();

            sharedPreferences.edit().putString("exp", userData.getString("exp")).apply();

            JSONArray rolesArray = userData.getJSONArray("role");
            JSONObject firstRoleObject = rolesArray.getJSONObject(0);

            sharedPreferences.edit().putString("role", firstRoleObject.getString("authority")).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getUserId() {
        return sharedPreferences.getString("id", null);
    }

    public static String getEmail() {
        return sharedPreferences.getString("email", null);
    }

    public static String getRole() {
        return sharedPreferences.getString("role", null);
    }



    public static void clearUserData() {
        sharedPreferences.edit().remove("jwt").apply();
        sharedPreferences.edit().remove("id").apply();
        sharedPreferences.edit().remove("email").apply();
        sharedPreferences.edit().remove("role").apply();
        sharedPreferences.edit().remove("exp").apply();
    }

}
