package com.example.projekatmobilne.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JWTManager {

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


    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("pref_file", Context.MODE_PRIVATE);
    }

    public static void saveJWT(Context context, String jwtToken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("jwt", jwtToken);
        editor.apply();

        saveUserData(context, jwtToken);
    }

    public static String getJWT(Context context) {
        return getSharedPreferences(context).getString("jwt", null);
    }

    private static void saveUserData(Context context, String jwtToken) {
        JSONObject userData = decodeJWT(jwtToken);

        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        try {
            editor.putString("id", userData.getString("id"));
            editor.putString("email", userData.getString("sub"));
            editor.putString("exp", userData.getString("exp"));

            JSONArray rolesArray = userData.getJSONArray("role");
            JSONObject firstRoleObject = rolesArray.getJSONObject(0);
            editor.putString("role", firstRoleObject.getString("authority"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString("id", null);
    }

    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString("email", null);
    }

    public static String getRole(Context context) {
        return getSharedPreferences(context).getString("role", null);
    }



    public static void clearUserData(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove("jwt");
        editor.remove("id");
        editor.remove("email");
        editor.remove("role");
        editor.remove("exp");
        editor.apply();
    }

}
