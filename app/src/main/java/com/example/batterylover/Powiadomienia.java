package com.example.batterylover;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Powiadomienia extends AppCompatActivity {


    // zmienne do zapisu
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String dolny = "dolny";
    public static final String gorny = "gorny";

    //elementy z XML
    private TextView napisPoziomDolny, napisPoziomGorny;
    private SeekBar poziomDolny,poziomGorny;


    public void zapiszDane() // funkcja zapisujaca zmienne do pamieci
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(dolny,poziomDolny.getProgress()*5);
        editor.putInt(gorny,poziomGorny.getProgress()*5);
        editor.apply();

        NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(3);   //kasowanie powiadomienia o odlaczeniu ladowarki
        nMgr.cancel(2);   //kasowanie powiadomienia o podlaczeniu ladowarki
    }

    public void wczytajDane() //funkcja wczytujaca zmienne z pamieci
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        poziomDolny.setProgress(sharedPreferences.getInt(dolny,50)/5);
        poziomGorny.setProgress(sharedPreferences.getInt(gorny,90)/5);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_powiadomienia);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide(); //usuwa pasek u góry
        }

        //deklaracja elementów xml

        napisPoziomDolny = findViewById(R.id.napisPoziomDolny);
        napisPoziomGorny = findViewById(R.id.napisGornyPoziom);
        poziomDolny = findViewById(R.id.poziomDolny);
        poziomGorny = findViewById(R.id.poziomGorny);
        //Button zapisz= findViewById(R.id.zapisz);


        //obsługa dolnego poziomu naładowania
        poziomDolny.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                napisPoziomDolny.setText(getResources().getString(R.string.napis_dolny_poziom,progress*5,"%")); //wysypuje sie przy stringu SPRAWDZ
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress()>=poziomGorny.getProgress())
                {
                    seekBar.setProgress(poziomGorny.getProgress()-1);
                }
            }
        });

        //obsługa górnego poziomu naładowania
        poziomGorny.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                napisPoziomGorny.setText(getResources().getString(R.string.napis_gorny_poziom,progress*5,"%")); //wysypuje sie przy stringu SPRAWDZ
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress()<=poziomDolny.getProgress())
                {
                    seekBar.setProgress(poziomDolny.getProgress()+1);
                }
            }
        });

        wczytajDane(); //wczytanie danych z pamięci

    }
    public void zapis(View view) //przesłanie zmiennych do pierwszej aktywności, zapis zmian
    {
        zapiszDane();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("dolny",poziomDolny.getProgress()*5);
        resultIntent.putExtra("gorny",poziomGorny.getProgress()*5);
        setResult(RESULT_OK,resultIntent);
        finish();
    }
    public void otworzUstawienia(View v)
    {
        Intent intent;
        intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }
}