package com.lndisplay.privacydisplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        SharedPreferences prefs = context.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean(MainActivity.KEY_ENABLED, false);
        int level = prefs.getInt(MainActivity.KEY_LEVEL, 70);

        if (enabled && Settings.canDrawOverlays(context)) {
            Intent serviceIntent = new Intent(context, PrivacyOverlayService.class);
            serviceIntent.setAction(PrivacyOverlayService.ACTION_START);
            serviceIntent.putExtra(PrivacyOverlayService.EXTRA_LEVEL, level);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}
