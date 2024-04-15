package com.example.projekatmobilne.receivers;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.SplashActivity;
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

            Boolean kurac = context instanceof SplashActivity;
            Log.i("aaa", String.valueOf(kurac));

            if(context instanceof SplashActivity){
                ((SplashActivity) context).stopSplashTimer();
            }


            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_dialog_box);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.custom_dialog_bg));
            dialog.setCancelable(false);

            Button btnExit = dialog.findViewById(R.id.btnExit);
            Button btnOpenSettings = dialog.findViewById(R.id.btnSettings);





            btnExit.setOnClickListener(v -> {
                dialog.dismiss();
            });


            btnOpenSettings.setOnClickListener(v -> {
                dialog.dismiss();
            });


            dialog.show();
            Toast.makeText(context, "NIJE KONEKTOVAN JE NA INTERNET", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "KONEKTOVAN NA INTERNET", Toast.LENGTH_SHORT).show();
        }
    }



}