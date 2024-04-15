package com.example.batterylover;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {


    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())

                .setMessage("Aplikacja działa nieprzerwanie w tle. W tym celu należy wyłączyć oszczędzanie energii, wyłączyć optymalizację zużycia energii oraz wyłączyć wszystko co może zapobiegać działaniu w tle. W przeciwnym razie aplikacja może działać nieprawidłowo.")
                .setTitle("Ostrzeżenie")
                .setNegativeButton("Anuluj", (dialogInterface, i) -> {
                })
                .setPositiveButton("Przejdź do ustawień", (dialogInterface, i) -> {
                    //przeniesienie do ustawień optymalizacji baterii
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    requireContext().startActivity(intent);
                });

        return builder.create();
    }

}