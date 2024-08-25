package com.example.projekatmobilne.activities;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.databinding.ActivitySplashBinding;
import com.example.projekatmobilne.receivers.ConnectionStatusReceiver;
import com.example.projekatmobilne.services.ConnectionCheckService;
import com.example.projekatmobilne.tools.CheckConnectionTools;
import com.example.projekatmobilne.tools.JWTManager;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    private Timer splashTimer;
    private Button btnExit, btnOpenSettings;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    private Dialog dialog;
    private ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the notification permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    public void stopSplashTimer() {
        if (splashTimer != null) {
            splashTimer.cancel();
            splashTimer = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConnectionStatusReceiver receiver = new ConnectionStatusReceiver();
        IntentFilter filter = new IntentFilter("com.example.projekatmobilne.NO_INTERNET");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }



        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        btnExit = dialog.findViewById(R.id.btnExit);
        btnOpenSettings = dialog.findViewById(R.id.btnSettings);

        btnExit.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        btnOpenSettings.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(intent);
            dialog.dismiss();
        });

        JWTManager.setup(this);
        JWTManager.clearUserData();
        //getSupportActionBar().hide();
        int SPLASH_TIME_OUT = 3000;
        String jwt = JWTManager.getJWT();

        if(jwt != null) { if(JWTManager.isExpired()) JWTManager.clearUserData();
        else JWTManager.saveJWT(jwt);}



        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(jwt != null){
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);

        int status = CheckConnectionTools.getConnectivityStatus(getApplicationContext());
        if(status == CheckConnectionTools.TYPE_NOT_CONNECTED){
            dialog.show();
            stopSplashTimer();
        }

    }
}