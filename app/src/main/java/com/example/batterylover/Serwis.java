package com.example.batterylover;

import static android.graphics.Color.rgb;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Serwis extends Service {
    private Receiver broadcastReceiver;

    @Override
    public void onCreate() {

        super.onCreate();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int dolny = intent.getIntExtra("dolny",50);
        int gorny = intent.getIntExtra("gorny",90);
        broadcastReceiver = new Receiver(dolny,gorny);
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver,filter);
        Intent notificationIntent = new Intent(Serwis.this, com.example.batterylover.MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(Serwis.this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        //nastąpiła zmiana z powiadomieniam, NotificationCompat zostal deprecated i teraz jest tylko Notification

        //dwie wersje w zaleznosci od systemu
        //setPriority jest zmienione w Kanaly.java
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            notification = new Notification.Builder(this, Kanaly.CHANNEL_1_ID)
                    .setContentTitle("Monitorowanie")
                    .setContentText("Kontrola stanu baterii w trakcie...")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentIntent(pendingIntent)
                    .setColor(rgb(128, 255, 0))
                    .setCategory(NotificationCompat.CATEGORY_SYSTEM)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
                    .setOngoing(true)
                    .build();

        }
        else {
            notification = new NotificationCompat.Builder(this, Kanaly.CHANNEL_1_ID)
                    .setContentTitle("Monitorowanie")
                    .setContentText("Kontrola stanu baterii w trakcie...")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentIntent(pendingIntent)
                    .setColor(rgb(128, 255, 0))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_SYSTEM)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .build();

        }
        startForeground(1,notification);


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        //stopSelf(); //skasowałem bo nie pojawia sie powiadomienie
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
