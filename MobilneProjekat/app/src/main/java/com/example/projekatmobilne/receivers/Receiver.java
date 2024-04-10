package com.example.projekatmobilne.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.tools.CheckConnectionTools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int status = CheckConnectionTools.getConnectivityStatus(context);


        if (status == CheckConnectionTools.TYPE_NOT_CONNECTED) {
            Toast.makeText(context, "NIJE KONEKTOVAN JE NA INTERNET", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "KONEKTOVAN NA INTERNET", Toast.LENGTH_SHORT).show();
            checkBackendAvailability(context);
        }
    }

    private void checkBackendAvailability(Context context) {
        // Use Retrofit or Volley to make a network request to your backend
        // Replace "http://your-backend-url.com/check" with the actual endpoint you want to check


        // Use AsyncTask or other background execution methods to perform network operations
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
//                    URL url = new URL(ClientUtils.SERVICE_API_PATH);
//                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                    urlConnection.setRequestMethod("GET");
//                    urlConnection.setConnectTimeout(5000); // Adjust timeout as needed
//                    int responseCode = urlConnection.getResponseCode();
//                    System.out.println("Response code je " + responseCode);
                    //return InetAddress.getByName(ClientUtils.SERVICE_API_PATH).isReachable(5000);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean isAvailable) {
                if (isAvailable) {
                    Toast.makeText(context, "Servis je dostupan", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "KONEKTOVAN NA INTERNET ali backend nije dostupan", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}