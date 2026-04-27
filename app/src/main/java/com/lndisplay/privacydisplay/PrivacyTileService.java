package com.lndisplay.privacydisplay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.core.content.ContextCompat;

public class PrivacyTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean(MainActivity.KEY_ENABLED, false);
        int level = prefs.getInt(MainActivity.KEY_LEVEL, 70);

        if (enabled) {
            prefs.edit().putBoolean(MainActivity.KEY_ENABLED, false).apply();
            Intent intent = new Intent(this, PrivacyOverlayService.class);
            intent.setAction(PrivacyOverlayService.ACTION_STOP);
            startService(intent);
        } else {
            if (!Settings.canDrawOverlays(this)) {
                // Open app for permission
                Intent openApp = new Intent(this, MainActivity.class);
                openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openApp);
                return;
            }
            prefs.edit().putBoolean(MainActivity.KEY_ENABLED, true).apply();
            Intent intent = new Intent(this, PrivacyOverlayService.class);
            intent.setAction(PrivacyOverlayService.ACTION_START);
            intent.putExtra(PrivacyOverlayService.EXTRA_LEVEL, level);
            ContextCompat.startForegroundService(this, intent);
        }
        updateTile();
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean(MainActivity.KEY_ENABLED, false);
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setLabel("Privacy Display");
        tile.setSubtitle(enabled ? "Ativo" : "Inativo");
        tile.updateTile();
    }
}
