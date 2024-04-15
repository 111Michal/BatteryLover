package com.example.batterylover;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    //do otwarcia drugiej aktywności
    private ActivityResultLauncher<Intent> intentLauncher;

    //zmienne do zapisu danych
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String przelacznik = "przelacznik";
    public static final String dolnyPoziom = "dolny";
    public static final String gornyPoziom = "gorny";

    //deklaracja elementów z XML

    private SwitchCompat switchPowiadomienia;

    private int dolny=0, gorny=0;

    //sprawdza czy serwis działa w tle
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Serwis.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    //zapis danych do pamieci
    public void zapiszDane()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(przelacznik,switchPowiadomienia.isChecked());
        editor.apply();
    }

    //odczyt danych z pamieci
    public void wczytajDane()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        switchPowiadomienia.setChecked(sharedPreferences.getBoolean(przelacznik,false));
        dolny = (sharedPreferences.getInt(dolnyPoziom,50));
        gorny = (sharedPreferences.getInt(gornyPoziom,90));
    }

    //otwiera ostrzeżenie przy uruchomieniu
    public void otworzDialog() {
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    //otwiera drugą aktywność
    public void otworzPowiadomienia(View view)
    {
        Intent intent = new Intent(this, Powiadomienia.class);
        intentLauncher.launch(intent);
    }


    //receiver do sprawdzania baterii IRT

    private final BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //znajdowanie elementów z XML
            TextView poziomBaterii = findViewById(R.id.poziomBaterii);
            TextView ladowanie = findViewById(R.id.ladowanie);
            TextView statusAC = findViewById(R.id.statusAC);
            ImageView zdjecie = findViewById(R.id.zdjecie);
            TextView temperatura = findViewById(R.id.temperatura);
            TextView napiecie = findViewById(R.id.napiecie);

            //zmiana zdjecia w zaleznosci od poziomu baterii
            if(intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1) >=0 && intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)<=30)
            {
                zdjecie.setImageResource(R.drawable.low_battery);
                //pierwsze zdj
            }
            else if(intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)>30 && intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1) <=50)
            {
                zdjecie.setImageResource(R.drawable.half_battery);
                //drugie zdj
            }
            else if(intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)>50 && intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1) <=80)
            {
                zdjecie.setImageResource(R.drawable.medium_battery);
                //trzecie zdj
            }
            else if(intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)>80 && intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)<=100)
            {
                zdjecie.setImageResource(R.drawable.full_battery);
                //czwarte zdj
            }

            //wyswietlenie poziomu baterii i zapis jego wartości do zmiennej
            //poziomBaterii.setText(intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)+"%");
            poziomBaterii.setText(getResources().getString(R.string.poziom_baterii,intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),"%"));

            //temperatura baterii
            temperatura.setText((float)intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1)/10+" °C");


            //
            napiecie.setText((float)intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,-1)/1000+" V");

            //odczyt stanu ładowania
            switch(intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1))
            {
                case 1:
                    ladowanie.setText("???");
                    break;
                case 2:
                    ladowanie.setText(R.string.napis_laduje);
                    break;
                case 5:
                    ladowanie.setText(R.string.napis_naladowany);
                    break;
                default:
                    ladowanie.setText(R.string.napis_nieladuje);
                    break;
            }

            //odczyt stanu ładowarki/USB
            switch(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1))
            {
                case 0: //niepodłączony do ładowania
                    statusAC.setText(R.string.napis_niepodlaczony);
                    break;
                case 1: //podłączony do ładowania AC
                    statusAC.setText(R.string.napis_podlaczony);
                    break;
                case 2: //podłączony do ładowania USB
                    statusAC.setText(R.string.napis_USB);
                    break;
                case 4: //podłączony do ładowania wireless
                    ladowanie.setText(R.string.napis_indukcyjnie);
                    break;
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide(); //usuwa pasek u góry
        }


        //dodanie aplikacji do wyjątków DOZE
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if(!pm.isIgnoringBatteryOptimizations(getPackageName()))
        {
            otworzDialog(); //wyświetlenie komunikatu z ostrzeżeniem o wyłączeniu oszczędzania energii
        }

        //sprawdzenie czy jest pozwolenie na powiadomienia od api 33
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // jesli nie masz dostepu do powiadomien
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 9);
            }
        }

        this.registerReceiver(this.broadcast,new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); //odpalenie receivera

        //deklaracja elementów XML

        switchPowiadomienia = findViewById(R.id.switchPowiadomienia);

        //wczytanie danych

        wczytajDane();
        //uruchomienie powiadomień po starcie apki
        if(switchPowiadomienia.isChecked() && !isMyServiceRunning())
        {
            Intent serviceIntent = new Intent(MainActivity.this,Serwis.class);
            serviceIntent.putExtra("dolny", dolny);
            serviceIntent.putExtra("gorny", gorny);
            ContextCompat.startForegroundService(this, serviceIntent);
        }

        //obsluga switcha
        switchPowiadomienia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent serviceIntent = new Intent(MainActivity.this,Serwis.class);
            if(isChecked)
            {
                serviceIntent.putExtra("dolny", dolny);
                serviceIntent.putExtra("gorny", gorny);
                ContextCompat.startForegroundService(this, serviceIntent);
            }
            else
            {
                stopService(serviceIntent);
            }
            zapiszDane();
        });

        intentLauncher = registerForActivityResult(           //do otwarcia drugiej aktywności oraz odebrania danych z drugiej aktywności
                new ActivityResultContracts.StartActivityForResult(),result-> {
                    if(result.getResultCode() == RESULT_OK)
                    {

                        dolny = Objects.requireNonNull(result.getData()).getIntExtra("dolny",0);
                        gorny = Objects.requireNonNull(result.getData()).getIntExtra("gorny",0);

                        //restart serwisu
                        if(switchPowiadomienia.isChecked())
                        {
                            Intent serviceIntent = new Intent(MainActivity.this,Serwis.class);
                            serviceIntent.putExtra("dolny", dolny);
                            serviceIntent.putExtra("gorny", gorny);
                            stopService(serviceIntent); //zatrzymanie serwisu
                            ContextCompat.startForegroundService(this, serviceIntent); //uruchomienie z nowymi zmiennymi
                            //niestety, ponowne wywołanie startService nie inicjalizuje go z nowymi zmiennymi
                        }
                    }

                }
        );

    }

}