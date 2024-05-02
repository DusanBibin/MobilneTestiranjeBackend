package com.example.projekatmobilne.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.tools.CheckConnectionTools;

public class ConnectionStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("com.example.projekatmobilne.NO_INTERNET".equals(intent.getAction())){
            int status = intent.getIntExtra("connection_status", CheckConnectionTools.TYPE_NOT_CONNECTED);
            if(status == CheckConnectionTools.TYPE_NOT_CONNECTED){
                showNotification(context);

            }
        }
    }


    private void showNotification(Context context){
        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "internet_status_channel";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId, "Internet Status", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(context, channelId)
                .setContentTitle("No internet Connection")
                .setContentText("Click to enable internet.")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(PendingIntent.getActivities(context, 0, new Intent[]{new Intent(Settings.ACTION_WIRELESS_SETTINGS)}, 0))
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }
}
