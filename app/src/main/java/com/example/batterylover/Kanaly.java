package com.example.batterylover;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;


import androidx.core.app.NotificationCompat;

public class Kanaly extends Application
{
    public static final String CHANNEL_1_ID = "Aktywnosc";
    public static final String CHANNEL_2_ID = "Dolny";
    public static final String CHANNEL_3_ID = "Gorny";
    @Override
    public void onCreate() {
        super.onCreate();

        utworzKanalyPowiadomien();

    }

    private void utworzKanalyPowiadomien()
    {
        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .build();
        Uri customSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.plug_in); //custom dzwieki

        NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"Aktywność", NotificationManager.IMPORTANCE_LOW);

        channel1.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        //channel1.setImportance(NotificationManager.IMPORTANCE_LOW);
        channel1.setDescription("Monitorowanie baterii");

        NotificationChannel channel2 = new NotificationChannel(CHANNEL_2_ID,"Poziom Dolny", NotificationManager.IMPORTANCE_HIGH);
        channel2.setSound(customSoundUri,att);
        channel2.enableVibration(true);
        //channel2.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel2.setVibrationPattern(new long[]{0, 200, 200, 100,200,200});
        channel2.enableLights(true);
        channel2.setLightColor(Color.LTGRAY);
        channel2.setDescription("Dolny poziom naładowania baterii");
        channel2.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationChannel channel3 = new NotificationChannel(CHANNEL_3_ID,"Poziom Górny", NotificationManager.IMPORTANCE_HIGH);
        channel3.setSound(customSoundUri,att);
        channel3.enableVibration(true);
        //channel3.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel3.setVibrationPattern(new long[]{0, 200, 200, 500,200,200});
        channel3.enableLights(true);
        channel3.setLightColor(Color.CYAN);
        channel3.setDescription("Górny poziom naładowania baterii");
        channel3.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel3);
        manager.createNotificationChannel(channel1);
        manager.createNotificationChannel(channel2);
    }
}