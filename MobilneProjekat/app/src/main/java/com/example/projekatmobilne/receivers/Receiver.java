package com.example.projekatmobilne.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.projekatmobilne.tools.CheckConnectionTools;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int status = CheckConnectionTools.getConnectivityStatus(context);

        System.out.println(status);
        if (status == CheckConnectionTools.TYPE_NOT_CONNECTED) {
            Toast.makeText(context, "NIJE KONEKTOVAN JE NA INTERNET", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "KONEKTOVAN NA INTERNET", Toast.LENGTH_SHORT).show();
        }
    }
}