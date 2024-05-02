package com.example.projekatmobilne.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.example.projekatmobilne.tools.CheckConnectionTools;

public class ConnectionCheckService extends Service {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnableCode = null;


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                checkInternetConnection();
                handler.postDelayed(this, 30000);
            }
        };

        handler.post(runnableCode);
    }

    private void checkInternetConnection(){
        int status = CheckConnectionTools.getConnectivityStatus(getApplicationContext());
        Intent broadcastIntent = new Intent("com.example.projekatmobilne.NO_INTERNET");
        broadcastIntent.putExtra("connection_status", status);
        sendBroadcast(broadcastIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
    }
}
