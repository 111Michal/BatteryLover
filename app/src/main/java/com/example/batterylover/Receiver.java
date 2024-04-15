package com.example.batterylover;

import static android.graphics.Color.CYAN;
import static android.graphics.Color.LTGRAY;
import static android.graphics.Color.rgb;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.BatteryManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Receiver extends BroadcastReceiver {
    private final int dol;
    private final int gora;
    private boolean wykonano = false;
    private int procent = 0;

    Receiver(int dolny, int gorny) {
        this.dol = dolny;
        this.gora = gorny;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Uri customSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.plug_in); //custom dzwieki


        //Algorytm resetu zmiennej wykonano, DO PRZETESTOWANIA (Na ten moment wydaje się, że działa poprawnie)
        if (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0 && wykonano && (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)) >= 0 && (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)) <= dol) {
            wykonano = false; //Reset zmiennej wykonano (obsługa spadku/wzrostu poziomu baterii)
            NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);//kasowanie powiadomienia o podlaczeniu ladowarki
            nMgr.cancel(2);
        }
        if (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == 0 && wykonano /*&& (intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)) <=100  chyba useless*/ && (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)) > dol) {
            wykonano = false;
            if ((intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)) >= gora) {
                NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); //kasowanie powiadomienia o odlaczeniu ladowarki
                nMgr.cancel(3);
            }
        }

        if ((intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)) <= dol && (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) == 0) {

            if (!wykonano) {
                procent = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) + 1; //przypisanie poziomu baterii +1 do zmiennej
                wykonano = true;
            }
            if (procent - intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) == 1) //warunek zabezpieczający przed zbyt częstym uruchamianiem receivera
            {
                Notification notification = new NotificationCompat.Builder(context, Kanaly.CHANNEL_2_ID)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setColor(LTGRAY)
                        .setContentTitle("Podłącz ładowarkę")
                        .setContentText("Osiągnięto dolny poziom naładowania")
                        //.setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setLights(rgb(181, 254, 0), 1000, 500)
                        .setVibrate(new long[]{0, 200, 200, 500, 200, 200})
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_SYSTEM)
                        .setSound(customSoundUri)
                        .build();
                notificationManager.notify(2, notification);
                procent--;
            }

        }

        if((intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)) >= gora && (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) != 0) {

            if (!wykonano) {
                procent = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) - 1;
                wykonano = true;
            }
            if (procent - intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) == -1) {
                Notification notification = new NotificationCompat.Builder(context, Kanaly.CHANNEL_3_ID)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setColor(CYAN)
                        .setContentTitle("Odłącz łądowarkę")
                        .setContentText("Osiągnięto górny poziom naładowania")
                        //.setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setLights(rgb(181, 254, 0), 1000, 500)
                        .setVibrate(new long[]{0, 200, 200, 100, 200, 200})
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_SYSTEM)
                        .setSound(customSoundUri)
                        .build();
                notificationManager.notify(3, notification);
                procent++;
            }
        }

    }
}
