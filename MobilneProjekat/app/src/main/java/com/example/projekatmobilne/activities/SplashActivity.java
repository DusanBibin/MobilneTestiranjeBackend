package com.example.projekatmobilne.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.databinding.ActivitySplashBinding;
import com.example.projekatmobilne.receivers.Receiver;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    private Timer splashTimer;
    private Button btnExit, btnOpenSettings;

    private ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new Receiver(), filter);




        //getSupportActionBar().hide();
        int SPLASH_TIME_OUT = 3000;
        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask() {
            @Override
            public void run() {



                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    public void stopSplashTimer() {
        if (splashTimer != null) {
            splashTimer.cancel();
            splashTimer = null;
        }
    }
}